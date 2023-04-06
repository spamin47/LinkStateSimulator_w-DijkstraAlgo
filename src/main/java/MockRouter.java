import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.TreeSet;
// import java.util.random.*;

public class MockRouter{
    private int portNumber;
    private String[] adjacents;
    private ConcurrentHashMap<Integer, String> messages;
    private ArrayList<String> history;
    public boolean isRunning = true;
    // history
    // routing table
    public TreeSet<Integer> routingTable;

    
    public MockRouter(int portNumber, String[] adjacents)
    {
        this.portNumber     = portNumber;
        this.adjacents      = adjacents;
        this.messages       = new ConcurrentHashMap<>();
        this.history        = new ArrayList<>();
        routingTable   = new TreeSet<>(); //For storing newly discovered routers
        routingTable.add(portNumber);

        System.out.println("Initiating");
        SocketThread.start();
        RoutingThread.start();
    }   
    
    Thread SocketThread = new Thread(new Runnable() {
        public void run()
        {
            long startTime = System.currentTimeMillis();

            try 
            {
                ServerSocket    server     = new ServerSocket(portNumber);
                while(isRunning)
                {
//                    System.out.println("port:" + portNumber + " waiting for request.");
                    Socket              socket  = server.accept();
                    InputStreamReader in = new InputStreamReader(socket.getInputStream());
                    BufferedReader br = new BufferedReader(in);
                    String line = br.readLine();
                    PrintStream out = new PrintStream(socket.getOutputStream());

//                    System.out.println("Port:" + portNumber + " from client(" + socket.getPort()+"): "+ line);
                    try {


                        if (line == null) {
                            // do nothing
                            // we should never get null messages, but just in case
                        } else if (line.charAt(0) == 'l') {
                            String split[] = line.split("\\s+");
                            int senderPort = Integer.parseInt(split[1]);
                            int seqNum = Integer.parseInt(split[3]);

                            synchronized (this) {
                                //update routing table
                                routingTable.add(Integer.parseInt(split[2]));

                                // if we already have a copy of this link state message, compare the sequence numbers
                                if (messages.containsKey(senderPort)) {
                                    String newestMessage = messages.get(senderPort);
                                    int currentSeqNum = Integer.parseInt(newestMessage.split("\\s+")[3]);

                                    // store message with the larger sequence number
                                    if (seqNum > currentSeqNum)
                                        messages.put(senderPort, line);
                                } else if (senderPort != portNumber) // <-- may be redundant check
                                {
                                    // if we don't have a copy and its not our own link state message, store it
                                    messages.put(senderPort, line);
                                }
                            }
                            // get time elapsed, converted to seconds
                            long timeElapsed = (System.currentTimeMillis() - startTime) / 1000;
                            history.add("(" + timeElapsed + " sec) : " + line);
                            // store in history
                            // send an ACK back to the sender
                            out.println("ACK\n");
                        } else if (line.equals("h")) {
                            // need to implement link state message history, routing table
                            // for every link state message recieved, store the time elapsed and link state message
                            out.println("HISTORY");

                            for (String s : history) {
                                out.println(s);
                            }
                        } else if (line.equals("s")) {
                            System.out.println("Port:" + portNumber + " shutting down...");
                            out.println("STOPPING\r\n");
                            isRunning = false;
                        } else if (line.equals("t")) //return routing table
                        {
                            String sendBack = "";
                            for (int r : routingTable) {
                                sendBack += r + " ";
                            }
                            out.println(sendBack);
                        } else if (line.equals("p")) {
                            String sendBack = "Local Port: " + server.getLocalPort() +
                                    ", InetAddress: " + server.getInetAddress() +
                                    ", LocalSocketAddress: " + server.getLocalSocketAddress() +
                                    ", Channel: " + server.getChannel() +
                                    ", " + server.toString();
                            out.println(sendBack);
                        }
                    }catch(IndexOutOfBoundsException e){ //handles any random packets that can cause out of bound exception error
                        e.printStackTrace();
                    }catch(NumberFormatException e){
                        e.printStackTrace();
                    }

                    out.close();
                    br.close();
                    in.close();
                    socket.close();
                }

                server.close();
                return;
            }
            catch (IOException io)
            {
                io.printStackTrace();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    });


    Thread RoutingThread = new Thread(new Runnable() {
        @Override
        public void run()
        {
            int seqNum = 0;//link state msg for sequence number
            int ttl = 60; //link state msg for time to live
            double rand = (3 + Math.random())*1000;
            String message = ""; //link state message of adjacent neighbors

            for(String rd: adjacents)
            {
                message = message+ " " + rd;
            }

            while(isRunning)
            {
                for(String rd: adjacents){
                    try{
                        String split[] = rd.split("-");
                        String routerPort = split[0];
                        String distance = split[1];
//                        System.out.println("Port: "+ portNumber + "'s neighbor: " + "Port:"+routerPort + " Distance: " + distance);

                        Socket s = new Socket("localhost", Integer.parseInt(routerPort));
                        PrintStream out = new PrintStream(s.getOutputStream());

                        // send our link state message: l sender originator seqNum TTL adjacent routers
                        out.println("l " + portNumber + " " + portNumber + " " + seqNum + " " + ttl  + message);
                        s.close();

                        // forward all link state messages recieved from other routers to adjacent router
                        for (ConcurrentHashMap.Entry<Integer,String> mapElement : messages.entrySet())
                        {
                            String msgToFwd = mapElement.getValue();
                            String[] msgSplit = msgToFwd.split("\\s+");
                            String senderPort = msgSplit[1]; // get source port
                            msgToFwd = msgSplit[0] + " " + portNumber + " " + msgSplit[2] + " " + msgSplit[3] + " " + msgSplit[4] + " " + msgSplit[5];

                            // only open the connection and forward the message if the recipient is not the previous sender of that message
                            if(!senderPort.equals(routerPort))
                            {
                                s = new Socket("localhost", Integer.parseInt(routerPort));
                                out = new PrintStream(s.getOutputStream());
                                out.println(msgToFwd);
                                s.close();   
                            }
                        }

                        // out.println("l " + portNumber+ " " + seqNum + " " + ttl  + message); //send linkstate message
                        // s.close();
//                        Socket s2 = new Socket("localhost", Integer.parseInt(routerPort));
//                        out = new PrintStream(s2.getOutputStream());
//                        String routersFound = "RD"; //SocketThread can read "RD"
//                        for(int r: routersDiscovered){
//                            routersFound = routersFound + " " + r;
//                        }
//                        out.println(routersFound); //send routers discovered message to neighboring routers
//                        s2.close();
                    }catch(IOException e){
                        System.out.println("Cannot connect to router " + rd);
                        e.printStackTrace();
                    }

                }

                //send link state message to all adjacent neighbors

                // wait for ~3.xxxxx seconds
                try {
                    Thread.sleep((long)rand);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                seqNum++;
            }



        }
    });


    public String toString(){
        String adj = "";
        for(String s: adjacents){
                adj+=s + " ";
        }
        return "Port: " + portNumber + " Adjacents: " + adj;
    }
}
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.TreeSet;
import java.util.random.*;

public class MockRouter{
    private int portNumber;
    private String[] adjacents;
    private ConcurrentHashMap<Integer, String> messages;
    private ArrayList<String> history;
    public boolean isRunning = true;
    // history
    // routing table
    public TreeSet<Integer> routersDiscovered;

    
    public MockRouter(int portNumber, String[] adjacents)
    {
        this.portNumber  = portNumber;
        this.adjacents   = adjacents;
        this.messages    = new ConcurrentHashMap<>();
        routersDiscovered = new TreeSet<>(); //For storing newly discovered routers
        for(String r:adjacents){
            String[] split = r.split("-");
            routersDiscovered.add(Integer.parseInt(split[0]));
        }


        System.out.println("Starting thread");
        SocketThread.start();
        RoutingThread.start();
    }   
    
    Thread SocketThread = new Thread(new Runnable() {
        public void run()
        {
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

                    System.out.println("Port:" + portNumber + " from client(" + socket.getPort()+"): "+ line);
                    if(line == null)
                    {
                        // do nothing
                        // we should never get null messages, but just in case
                    }
                    else if(line.charAt(0) == 'l')
                    {
                        String split[] = line.split("\\s+");
                        int senderPort = Integer.parseInt(split[1]);
                        int seqNum = Integer.parseInt(split[2]);

                        synchronized(this)
                        {
                            // if we already have a copy of this link state message, compare the sequence numbers
                            if(messages.containsKey(senderPort))
                            { 
                                String newestMessage = messages.get(senderPort);
                                int currentSeqNum = Integer.parseInt(newestMessage.split("\\s+")[2]);

                                // store message with the larger sequence number
                                if(seqNum > currentSeqNum)
                                    messages.put(senderPort, line);
                            }
                            else if(senderPort != portNumber)
                            {
                                // if we don't have a copy and its not our own link state message, store it
                                messages.put(senderPort, line);
                            }
                        }

                        // send an ACK back to the sender
                        out.println("ACK\n");
                    }
                    else if (line.equals("h"))
                    {
                        // need to implement link state message history, routing table
                        // for every link state message recieved, store the time elapsed and link state message
                        out.println("history\n");
                    }
                    else if (line.equals("s"))
                    {
                        System.out.println("Port:" + portNumber + " shutting down...");
                        out.println("STOPPING\n");
                        isRunning = false;
                    }else if(line.equals("table")){
                        String sendBack = "";
                        for(int r:routersDiscovered){
                            System.out.print( r+", ");
                            sendBack += r + " ";
                        }
                        out.println(sendBack);
                        System.out.println("");
                    }else if(line.substring(0,2).equals("RD")){
                        String routersFound[] = line.substring(3).split(" ");
                        for(String rd:routersFound){
                            routersDiscovered.add(Integer.parseInt(rd));
                        }
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

                        // send our link state message
                        out.println("l " + portNumber+ " " + seqNum + " " + ttl  + message);
                        s.close();

                        // forward all link state messages recieved from other routers to adjacent router
                        for (ConcurrentHashMap.Entry<Integer,String> mapElement : messages.entrySet())
                        {
                            String messageToForward = mapElement.getValue();
                            String senderPort = messageToForward.split("\\s+")[1];

                            // only open the connecion and forward the message if the recipient is not the original sender of that message
                            if(!senderPort.equals(routerPort))
                            {
                                s = new Socket("localhost", Integer.parseInt(routerPort));
                                out = new PrintStream(s.getOutputStream());
                                out.println(messageToForward);
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
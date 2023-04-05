import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class MockRouter{
    private int portNumber;
    private String[] adjacents;
    private ConcurrentHashMap<Integer, String> messages;
    private ArrayList<String> history;
    public boolean isRunning = true;
    // history
    // messages
    // routing table 
    
    public MockRouter(int portNumber, String[] adjacents)
    {
        this.portNumber  = portNumber;
        this.adjacents   = adjacents;
        this.messages    = new ConcurrentHashMap<>();
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
                    System.out.println("port:" + portNumber + " waiting for request.");
                    Socket              socket  = server.accept();
                    InputStreamReader in = new InputStreamReader(socket.getInputStream());
                    BufferedReader br = new BufferedReader(in);
                    String line = br.readLine();
                    PrintStream out = new PrintStream(socket.getOutputStream());

                    System.out.println("Port:" + portNumber + " from client(" + socket.getPort()+"): "+ line);
                    out.println("Port(" + portNumber+"): I received " + line);
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

            try{
                while(isRunning)
                {
                    //send link state message to all adjacent neighbors
                    for(String rd: adjacents){
                        String split[] = rd.split("-");
                        String routerPort = split[0];
                        String distance = split[1];
                        System.out.println("Port: "+ portNumber + "'s neighbor: " + "Port:"+routerPort + " Distance: " + distance);

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
                    }
                    // wait for ~3.xxxxx seconds
                    Thread.sleep((long)rand);
                    seqNum++;
                }

            }catch(IOException e){
                    e.printStackTrace();
            }catch(InterruptedException e){
                    e.printStackTrace();
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
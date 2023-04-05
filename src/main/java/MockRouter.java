import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

public class MockRouter{
    private int portNumber;
    private String[] adjacents;
    private Hashtable<Integer, String> messages;
    private ArrayList<String> history;
    public boolean isRunning = true;
    // history
    // messages
    // routing table 
    
    public MockRouter(int portNumber, String[] adjacents)
    {
        this.portNumber  = portNumber;
        this.adjacents   = adjacents;
        this.messages    = new Hashtable<>();
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
                    PrintStream out = new PrintStream(socket.getOutputStream());
                    String line = br.readLine();

                    System.out.println("Port:" + portNumber + " from client(" + socket.getPort()+"): "+ line);
                    out.println("Port(" + portNumber+"): I received " + line);
                    if(line == null)
                    {
                        // do nothing
                    }
                    else if(line.charAt(0) == 'l')
                    {
                        String split[] = line.split("\\s+");
                        int senderPort = Integer.parseInt(split[1]);
                        int seqNum = Integer.parseInt(split[2]);

                        synchronized(this)
                        {
                            // if the 
                            if(messages.containsKey(senderPort))
                            { 
                                String newestMessage = messages.get(senderPort);
                                int currentSeqNum = Integer.parseInt(newestMessage.split("\\s+")[2]);

                                if(seqNum > currentSeqNum)
                                    messages.put(senderPort, line);
                            }
                            else if(senderPort != portNumber)
                            {
                                messages.put(senderPort, line);
                            }
                        }

                        // send back an ACK to the sender
                        out.println("ACK\n");
                    }
                    else if (line.equals("h"))
                    {
                        // need to implement link state message history, routing table
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

                        out.println("l " + portNumber+ " " + seqNum + " " + ttl  + message); //send linkstate message

                        synchronized(this)
                        {
                            // forward all messages
                            Enumeration<Integer> enumKeys = messages.keys();
                            while(enumKeys.hasMoreElements())
                            {
                                s = new Socket("localhost", Integer.parseInt(routerPort));
                                out = new PrintStream(s.getOutputStream());
                                Integer key = enumKeys.nextElement();
                                String messageToForward = messages.get(key);
                                String senderPort = messageToForward.split("\\s+")[1];
                                
                                // if the message is one we recieved from the router we're about to send it to, don't send it
                                if(!senderPort.equals(routerPort))
                                    out.println(messageToForward);

                                s.close();
                            }
                        }
                    }
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.random.*;

public class MockRouter{
    private int portNumber;
    private String[] adjacents;
    public boolean isRunning = true;
    // history
    // routing table
    public TreeSet<Integer> routersDiscovered;

    
    public MockRouter(int portNumber, String[] adjacents)
    {
        this.portNumber  = portNumber;
        this.adjacents   = adjacents;
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
                    // DataInputStream     in      = new DataInputStream(new BufferedInputStream(sender.getInputStream()));
                    // DataOutputStream    out     = new DataOutputStream(new BufferedOutputStream(sender.getOutputStream()));
                    // String              line    = in.readUTF();
                    InputStreamReader in = new InputStreamReader(socket.getInputStream());
                    BufferedReader br = new BufferedReader(in);
                    PrintStream out = new PrintStream(socket.getOutputStream());
                    String line = br.readLine();

                    System.out.println("Port:" + portNumber + " from client(" + socket.getPort()+"): "+line);

                    if(line.charAt(0) == 'l')
                    {
                        out.println("ACK\n");
                    }
                    else if (line.equals("h\n"))
                    {
                        // need to implement link state message history, routing table
                        out.println("history\n");
                    }
                    else if (line.equals("s\n"))
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
        public void run()
        {
            int seqNum = 0;//link state msg for sequence number
            int ttl = 60; //link state msg for time to live
            double rand = (3 + Math.random())*1000;
            String message = ""; //link state message of adjacent neighbors
            for(String rd: adjacents){
                message = message+ " " + rd;
            }
            try{
                while(isRunning){
                    //send link state message to all adjacent neighbors
                    for(String rd: adjacents){
                        String split[] = rd.split("-");
                        String routerPort = split[0];
                        String distance = split[1];
//                        System.out.println("Port: "+ portNumber + "'s neighbor: " + "Port:"+routerPort + " Distance: " + distance);

                        Socket s = new Socket("localhost", Integer.parseInt(routerPort));
                        PrintStream out = new PrintStream(s.getOutputStream());
                        out.println("l " + portNumber+ " " + seqNum + " " + ttl  + message); //send linkstate message
                        s.close();

                        Socket s2 = new Socket("localhost", Integer.parseInt(routerPort));
                        out = new PrintStream(s2.getOutputStream());
                        String routersFound = "RD"; //SocketThread can read "RD"
                        for(int r: routersDiscovered){
                            routersFound = routersFound + " " + r;
                        }
                        out.println(routersFound); //send routers discovered message to neighboring routers
                        s2.close();
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
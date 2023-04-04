import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MockRouter{
    private int portNumber;
    private String[] adjacents;
    // history
    // routing table 
    
    public MockRouter(int portNumber, String[] adjacents)
    {
        this.portNumber  = portNumber;
        this.adjacents   = adjacents;

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
                boolean         isRunning  = true;

                while(isRunning)
                {
                    System.out.println("running thread for port:" + portNumber);
                    Socket              socket  = server.accept();
                    // DataInputStream     in      = new DataInputStream(new BufferedInputStream(sender.getInputStream()));
                    // DataOutputStream    out     = new DataOutputStream(new BufferedOutputStream(sender.getOutputStream()));
                    // String              line    = in.readUTF();
                    InputStreamReader in = new InputStreamReader(socket.getInputStream());
                    BufferedReader br = new BufferedReader(in);
                    PrintStream out = new PrintStream(socket.getOutputStream());
                    String line = br.readLine();

                    System.out.println("Port: " + portNumber + " from client(" + socket.getPort()+": "+line);
                    out.println("Port(" + portNumber+"): I received " + line);

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

//     public MockRouter(int portNumber, String[] adjacents){
//         this.portNumber = portNumber;
//         this.adjacents = adjacents;
//         Thread SocketThread = new Thread(new Runnable() {
//             public void run()
//             {
//                 try
//                 {
//                     ServerSocket    server     = new ServerSocket(portNumber);
//                     boolean         isRunning  = true;

//                     while(isRunning)
//                     {
//                         System.out.println("Port: " + portNumber + " waiting for request.");
//                         Socket              sender  = server.accept();
//                         DataInputStream     input   = new DataInputStream(new BufferedInputStream(sender.getInputStream()));
//                         DataOutputStream    output  = new DataOutputStream(new BufferedOutputStream(sender.getOutputStream()));
// //                        String              line    = input.readUTF();
//                         InputStreamReader in = new InputStreamReader(sender.getInputStream());
//                         BufferedReader bf = new BufferedReader(in);
//                         String line = bf.readLine();

//                         System.out.println("Port: " + portNumber + " from client(" + sender.getPort()+": "+line);
//                         output.writeUTF("Port(" + portNumber+"): I received " + line);
//                         if(line.charAt(0) == 'l')
//                         {
//                             output.writeUTF("ACK\n");
//                         }
//                         else if (line.equals("h\n"))
//                         {
//                             // need to implement link state message history, routing table
//                             output.writeUTF("history\n");
//                         }
//                         else if (line.equals("s\n"))
//                         {
//                             output.writeUTF("STOPPING\n");
//                             isRunning = false;
//                         }

//                         output.close();
//                         input.close();
//                         sender.close();
//                     }

//                     server.close();
//                     return;
//                 }
//                 catch (IOException io)
//                 {
//                     io.printStackTrace();
//                 }
//                 catch (Exception e)
//                 {
//                     e.printStackTrace();
//                 }
//             }
//         });

    Thread RoutingThread = new Thread(new Runnable() {
        public void run()
        {
            try{
                for(String rd: adjacents){
                    String split[] = rd.split("-");
                    String routerPort = split[0];
                    String distance = split[1];
                    System.out.println("Port: "+portNumber + "'s neighbor: " + "Port:"+routerPort + " Distance: " + distance);
                    System.out.println("Enter the port to connect to:");
                    BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
                    Socket s = new Socket(portNumber+"",Integer.parseInt(keyboard.readLine()));
                    PrintWriter pr = new PrintWriter(s.getOutputStream(),true);
                    pr.println(keyboard.readLine());

                }
            }catch(IOException e){
                    e.printStackTrace();
            }


//            System.out.println("test");
//            try {
//                System.out.println("Enter the port to connect to:");
//                BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
//                Socket s = new Socket(portNumber+"",Integer.parseInt(keyboard.readLine()));
//                PrintWriter pr = new PrintWriter(s.getOutputStream(),true);
//                pr.println(keyboard.readLine());
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }

            }
        });

//    @Override
//    public void run(){
//        System.out.println("running thread for port:" + portNumber);
//        try {
//            ServerSocket ss = new ServerSocket(portNumber);
//            Socket s = ss.accept();
//
//            System.out.println("client connected to server port " + portNumber);
//
//            InputStreamReader in = new InputStreamReader(s.getInputStream());
//            BufferedReader bf = new BufferedReader(in);
//            String str = bf.readLine();
//            System.out.println("client("+s.getPort()+"): " + str);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }


    public String toString(){
        String adj = "";
        for(String s: adjacents){
                adj+=s + " ";
        }
        return "Port: " + portNumber + " Adjacents: " + adj;
    }
}
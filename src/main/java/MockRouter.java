import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;

public class MockRouter{
    private String[] adjacents;
    private int portNumber;

    public MockRouter(int portNumber, String[] adjacents){
        this.portNumber = portNumber;
        this.adjacents = adjacents;
        Thread SocketThread = new Thread(new Runnable() {
            public void run()
            {
                try
                {
                    ServerSocket    server     = new ServerSocket(portNumber);
                    boolean         isRunning  = true;

                    while(isRunning)
                    {
                        System.out.println("Port: " + portNumber + " waiting for request.");
                        Socket              sender  = server.accept();
                        DataInputStream     input   = new DataInputStream(new BufferedInputStream(sender.getInputStream()));
                        DataOutputStream    output  = new DataOutputStream(new BufferedOutputStream(sender.getOutputStream()));
//                        String              line    = input.readUTF();
                        InputStreamReader in = new InputStreamReader(sender.getInputStream());
                        BufferedReader bf = new BufferedReader(in);
                        String line = bf.readLine();

                        System.out.println("Port: " + portNumber + " from client(" + sender.getPort()+": "+line);
                        output.writeUTF("Port(" + portNumber+"): I received " + line);
                        if(line.charAt(0) == 'l')
                        {
                            output.writeUTF("ACK\n");
                        }
                        else if (line.equals("h\n"))
                        {
                            // need to implement link state message history, routing table
                            output.writeUTF("history\n");
                        }
                        else if (line.equals("s\n"))
                        {
                            output.writeUTF("STOPPING\n");
                            isRunning = false;
                        }

                        output.close();
                        input.close();
                        sender.close();
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
        System.out.println("Starting thread");
        SocketThread.start();
    }

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
    Thread RoutingThread = new Thread(new Runnable() {
        public void run()
        {


            System.out.println("test");
            try {
                System.out.println("Enter the port to connect to:");
                BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
                Socket s = new Socket(portNumber+"",Integer.parseInt(keyboard.readLine()));
                PrintWriter pr = new PrintWriter(s.getOutputStream(),true);
                pr.println(keyboard.readLine());
            } catch (IOException e) {
                throw new RuntimeException(e);
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

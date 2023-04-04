import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MockRouter 
{
    private int portNumber;
    private String[] adjacents;
    // history
    // routing table 
    
    public MockRouter(int portNumber, String[] adjacents)
    {
        this.portNumber  = portNumber;
        this.adjacents   = adjacents;


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
                    Socket              sender  = server.accept();
                    DataInputStream     in      = new DataInputStream(new BufferedInputStream(sender.getInputStream()));
                    DataOutputStream    out     = new DataOutputStream(new BufferedOutputStream(sender.getOutputStream()));
                    String              line    = in.readUTF();
    
                    if(line.charAt(0) == 'l')
                    {
                        out.writeUTF("ACK\n");
                    }
                    else if (line.equals("h\n"))
                    {
                        // need to implement link state message history, routing table
                        out.writeUTF("history\n");
                    }
                    else if (line.equals("s\n"))
                    {
                        out.writeUTF("STOPPING\n");
                        isRunning = false;
                    }

                    out.close();
                    in.close();
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

    
    Thread RoutingThread = new Thread(new Runnable() {
        public void run()
        {
        }
    });
}

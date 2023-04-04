import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MockRouter 
{
    private int portNumber;
    private String[] adjacents;
    
    
    public MockRouter(int portNumber, String[] adjacents)
    {
        this.portNumber  = portNumber;
        this.adjacents   = adjacents;


    }   
    
    Thread SocketThread = new Thread(new Runnable() {
        int port_num = portNumber;

        public void run()
        {
            try 
            {
                ServerSocket socket     = new ServerSocket(portNumber);
                Socket sender           = socket.accept();
                DataInputStream input   = new DataInputStream(new BufferedInputStream(sender.getInputStream()));
                String line             = input.readUTF();

                if()
                input.close();
                socket.close();
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
        int port_num = portNumber;

        public void run()
        {
        }
    });
}

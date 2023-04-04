import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;

public class MockRouter implements Runnable{
    private String[] adjacents;
    private int portNumber;

    public MockRouter(int portNumber, String[] adjacents){
        this.portNumber = portNumber;
        this.adjacents = adjacents;
    }

    @Override
    public void run(){
        System.out.println("running thread for port:" + portNumber);
        try {
            ServerSocket ss = new ServerSocket(portNumber);
            Socket s = ss.accept();

            System.out.println("client connected to server port " + portNumber);

            InputStreamReader in = new InputStreamReader(s.getInputStream());
            BufferedReader bf = new BufferedReader(in);
            String str = bf.readLine();
            System.out.println("client: " + str);


            ss.close(); //close after receiving message from client
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String toString(){
        String adj = "";
        for(String s: adjacents){
                adj+=s + " ";
        }
        return "Port: " + portNumber + " Adjacents: " + adj;
    }
}

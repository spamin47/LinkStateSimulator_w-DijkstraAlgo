import java.net.*;
import java.io.*;
import java.nio.Buffer;

public class client {
    public static void main(String[] args) throws IOException {
        System.out.println("Enter the port to connect to:");
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        Socket s = new Socket("localhost",Integer.parseInt(keyboard.readLine()));

//        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter pr = new PrintWriter(s.getOutputStream(),true);
        pr.println(keyboard.readLine());
    }
}
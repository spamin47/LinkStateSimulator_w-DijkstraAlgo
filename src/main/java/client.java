import java.net.*;
import java.io.*;
import java.nio.Buffer;

public class client {
    public static void main(String[] args) throws IOException {
        while (true) {
//            System.out.println("Enter the port to connect to:");
            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
            String command = keyboard.readLine();
            int portNum = 0;
            if (command.equals("e")) { //Exits program
                break;
            } else if (command.charAt(0) == 'h' | command.charAt(0) == 's') {
                portNum = Integer.parseInt(command.substring(2));
                System.out.println(portNum);
                Socket s = new Socket("localhost", portNum);
                System.out.println("connected to server");

                PrintWriter pr = new PrintWriter(s.getOutputStream(), true);
                pr.println(command.charAt(0));

                //get response from server
                InputStreamReader in = new InputStreamReader(s.getInputStream());
                BufferedReader bf = new BufferedReader(in);
                String line = bf.readLine();
                System.out.println("Server: " + line);
                // line = bf.readLine();

                s.close();
            }
        }
    }
}


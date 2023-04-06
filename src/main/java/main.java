import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class main {
    public static void main(String[] args) throws IOException {
        // String filename = "D:\\projects\\Java\\LinkStateSimulator\\src\\main\\java\\test_topology_file.txt";
        String filename = "src/main/java/test_topology_file.txt";
        ReadFile rf = new ReadFile("src/main/java/test_topology_file.txt");

        //instantiate
        ArrayList<MockRouter> routers = new ArrayList<>();
        try{
            File myFile = new File(filename);
            Scanner myReader = new Scanner(myFile);
            while(myReader.hasNextLine()){
                String data = myReader.nextLine();

                int port = Integer.parseInt(data.substring(0,5));

                String[] split = data.substring(6).split(" ");
                routers.add(new MockRouter(port,split));

            }
            myReader.close();
        }catch(Exception e){
            System.out.println("error occurred");
            e.printStackTrace();
            return;
        }

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
                System.out.println("Server: " + bf.readLine());
                s.close();
            }


        }
    }



}

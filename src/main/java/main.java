import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class main {
    public static void main(String[] args){
        // String filename = "D:\\projects\\Java\\LinkStateSimulator\\src\\main\\java\\test_topology_file.txt";
        String filename = "src/main/java/test_topology_file.txt";
        ReadFile rf = new ReadFile("src/main/java/test_topology_file.txt");
//        rf.printFile();
//        rf.storeRouterPorts();
//        rf.printRouterPortsStored();
//        rf.createGraph();
//        rf.printGraph();

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


    }
}

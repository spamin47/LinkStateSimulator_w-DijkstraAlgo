import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class main {
    public static void main(String[] args){
        String filename = "E:\\Projects\\Java\\CS158 - computer network\\LinkStateSimulator_w-DijkstraAlgo\\src\\main\\java\\test_topology_file.txt";
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
//                System.out.println(data);
                int port = Integer.parseInt(data.substring(0,5));
//                System.out.println("Main Port " + port);
                String[] split = data.substring(6).split(" ");
                routers.add(new MockRouter(port,split));
//                System.out.println("");

            }
            myReader.close();
        }catch(Exception e){
            System.out.println("error occurred");
            e.printStackTrace();
            return;
        }

//        for(MockRouter r:routers){
//            System.out.println(r);
//            Thread router = new Thread(r);
//            router.start();
//        }

    }
}

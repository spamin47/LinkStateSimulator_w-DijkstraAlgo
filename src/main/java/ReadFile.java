import java.io.*;
import java.util.*;

public class ReadFile {
    private String filename;
    private ArrayList<Integer> routerPort;

    public ReadFile(String filename){
        this.filename = filename;
        routerPort = new ArrayList<>();
    }

    public void printFile(){
        try{
            File myFile = new File(filename);
            Scanner myReader = new Scanner(myFile);

            while(myReader.hasNextLine()){
                String data = myReader.nextLine();
                System.out.println(data);
            }
            myReader.close();
        }catch(Exception e) {
            System.out.println("error occurred");
            e.printStackTrace();
        }
    }

    //create a weighted 2d array graph from the stored router ports and topology file.
    public void createGraph(){
        int numOfPortsStored = routerPort.size();
        if(numOfPortsStored <= 0 ){
            System.out.println("Router Ports not stored.");
            return;
        }
        int graph[][] = new int[numOfPortsStored][numOfPortsStored];

        try{
            File myFile = new File(filename);
            Scanner myReader = new Scanner(myFile);

            while(myReader.hasNextLine()){
                String data = myReader.nextLine();
//                System.out.println(data);

                String[] split = data.split("[ -]+");
                for(String s:split){
                    System.out.println(s);
                }
                System.out.println("");

            }
            myReader.close();
        }catch(Exception e){
            System.out.println("error occurred");
            e.printStackTrace();
        }
    }

    //Read and store all router ports in the topology text file.
    public void storeRouterPorts(){
        try{
            File myFile = new File(filename);
            Scanner myReader = new Scanner(myFile);
            while(myReader.hasNextLine()){
                String data = myReader.nextLine();
                String[] split = data.split(" ");
                routerPort.add(Integer.parseInt(split[0]));
//                System.out.println("");
            }
            myReader.close();
        }catch(Exception e){
            System.out.println("error occurred");
            e.printStackTrace();
            return;
        }
        System.out.println("Router ports successfully stored.");
    }
    public void printRouterPortsStored(){
        int len = routerPort.size();
        System.out.println("# of ports: " + len);
        for(int i = 0; i<len;i++){
            System.out.println(routerPort.get(i));
        }
    }
}

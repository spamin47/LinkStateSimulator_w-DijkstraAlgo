import java.io.*;
import java.util.*;

public class ReadFile {
    private String filename;
    private ArrayList<Integer> routerPort;
    private HashMap<Integer,Integer> routerPorts;
    private int weightedGraph[][];

    public ReadFile(String filename){
        this.filename = filename;
        routerPort = new ArrayList<>();
        routerPorts= new HashMap<Integer,Integer>();
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
            System.out.println("");
            while(myReader.hasNextLine()){
                String data = myReader.nextLine();
//                System.out.println(data);
                int port = Integer.parseInt(data.substring(0,5));
                System.out.println("Main Port " + port);
                String[] split = data.substring(6).split("[ -]+");
                for(int i = 0;i<split.length;i+=2){
                    System.out.println("Port: " + split[i]);
                    System.out.println("Distance: " + split[i+1]);
                    graph[routerPorts.get(port)][routerPorts.get(Integer.parseInt(split[i]))] = Integer.parseInt(split[i+1]);
                }
                System.out.println("");

            }
            weightedGraph = graph;
            myReader.close();
        }catch(Exception e){
            System.out.println("error occurred");
            e.printStackTrace();
            return;
        }
    }

    //Read and store all router ports in the topology text file.
    public void storeRouterPorts(){
        try{
            File myFile = new File(filename);
            Scanner myReader = new Scanner(myFile);
            int indx = 0;
            while(myReader.hasNextLine()){
                String data = myReader.nextLine();
                String[] split = data.split(" ");
                routerPort.add(Integer.parseInt(split[0]));
                routerPorts.put(Integer.parseInt(split[0]),indx);
                indx++;
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
    public void printGraph(){
        for(int r = 0;r<weightedGraph.length;r++){
            for(int c = 0;c<weightedGraph[0].length;c++){
                System.out.print(weightedGraph[r][c] + " ");
            }
            System.out.println("");
        }
        System.out.println("");
    }
}

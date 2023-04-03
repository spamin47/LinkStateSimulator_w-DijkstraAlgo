
public class main {
    public static void main(String[] args){
        System.out.println("hello world");
        ReadFile rf = new ReadFile("src/main/java/test_topology_file.txt");
        rf.printFile();
        rf.storeRouterPorts();
        rf.printRouterPortsStored();
        rf.createGraph();
        rf.printGraph();
    }
}

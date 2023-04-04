public class MockRouter {
    private String[] adjacents;
    private int portNumber;

    public MockRouter(int portNumber, String[] adjacents){
        this.portNumber = portNumber;
        this.adjacents = adjacents;
    }


    public String toString(){
        String adj = "";
        for(String s: adjacents){
                adj+=s + " ";
        }
        return "Port: " + portNumber + " Adjacents: " + adj;
    }
}

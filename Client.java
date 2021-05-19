import java.net.Socket;

public class Client {

    private Socket socket;
    private String serverIP;
    private int serverPort;

    public Client(String serverIP, int serverPort){
        this.serverIP = serverIP;
        this.serverPort = serverPort;
    }

    private void connect(){
        try {
            System.out.println("Connecting to Server on " + serverIP);
            socket = new Socket(serverIP, serverPort);
            System.out.println("Connected");

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    public static void main(String[] args) {
        if(args.length < 2){
            System.out.println("Expected: IP-Adress | Port");
            System.exit(0);
        }
    
        String ip = args[0];
        int port = Integer.parseInt(args[1]);
    
        Client c = new Client(ip, port);
        c.connect();
    
        
    }

}
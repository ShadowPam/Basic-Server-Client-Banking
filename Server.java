import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server{
    
    private ServerSocket sSock;
    private boolean running;
    private String ipAdress;
    private int port;

    private HashMap<String, String> users;
    private HashMap<Integer, Integer> balances;
    
    public Server(String ipAdress){
        this.ipAdress = ipAdress;
        running = false;
        users = new HashMap<>();
        balances = new HashMap<>();
    }

    private void startServer(){
        try {
            sSock = new ServerSocket(0,1,InetAddress.getByName(ipAdress));
            port = sSock.getLocalPort();
            System.out.println("Server started on " + ipAdress + " over port " + port);
            run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void run(){
        running = true;
        while(running){
            try {
                System.out.println("Waiting for connection...");
                Socket cSock = sSock.accept();

                System.out.println("Connection established with " + cSock.getInetAddress().getHostAddress());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        if(args.length == 0)
        {
            System.out.println("Error: Expected IP-Adress");
            System.exit(0);
        }
        String ip = args[0];

        Server server = new Server(ip);
        server.startServer();
    }
}
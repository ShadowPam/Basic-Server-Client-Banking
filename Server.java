import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
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
    private HashMap<String, Integer> balances;
    
    public Server(String ipAdress){
        this.ipAdress = ipAdress;
        running = false;
        users = new HashMap<>();
        balances = new HashMap<>();

        //Add two basic users for testing purposes
        users.put("admin", "admin");
        users.put("foo", "bar");

        //Should be extended with reading in from a file
        balances.put("admin", 0);
        balances.put("foo", 0);
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
                PrintStream output = new PrintStream(cSock.getOutputStream());
                BufferedReader input = new BufferedReader(new InputStreamReader(cSock.getInputStream()));

                //Client connected start handling login req
                String line = input.readLine();
                if(line.equals("login")){
                    //Send back confirmation
                    output.println("confirmed");
                    output.flush();
                }else{
                    //For future in case login was not typed::when not hardcoded
                }
                
                String[] usrAndPwd = input.readLine().split(" ");
                String currentClient;
                //Check for existing users and pwd
                if(users.get(usrAndPwd[0]).equals(usrAndPwd[1])){
                    //login success
                    currentClient = usrAndPwd[0];
                    output.println("success");
                    output.flush();

                    boolean requestLoop = true;
                    while(requestLoop){
                        String[] command = input.readLine().split(" ");

                        switch(command[0]){
                            case "get":
                                if(command[1].equals("balance")){
                                    //Send balance
                                    output.println(balances.get(currentClient));
                                    output.flush();
                                    break;
                                }

                            case "withdraw":
                                int withdrawAmount = Integer.parseInt(command[1]);

                                //Try to withdraw
                                int cBalance = balances.get(currentClient);

                                if(cBalance < withdrawAmount){
                                    //Send error
                                    output.println("illegal");
                                    output.flush();
                                    break;
                                }

                                //Complete withdrawal and send success
                                balances.put(currentClient, balances.get(currentClient)-withdrawAmount);
                                output.println("ok!");
                                output.flush();
                                break;

                            case "deposit":
                                int depositAmount = Integer.parseInt(command[1]);

                                //Complete deposit and send success
                                balances.put(currentClient, balances.get(currentClient)+depositAmount);
                                output.println("ok!");
                                output.flush();
                                break;

                            case "transfer":
                                int transferAmount = Integer.parseInt(command[1]);
                                String transferUsr = command[2];

                                //Check legal transfer amount
                                if(transferAmount > balances.get(currentClient)){
                                    //Send error
                                    output.println("e1");
                                    output.flush();
                                    break;
                                }

                                //Check legal username
                                if(!users.containsKey(command[2])){
                                    //Send error
                                    output.println("e2");
                                    output.flush();
                                    break;
                                }

                                //Send transfer
                                balances.put(currentClient, balances.get(currentClient)-transferAmount);
                                balances.put(transferUsr, balances.get(transferUsr)+transferAmount);
                                output.println("ok!");
                                output.flush();
                                break;

                            case "logout":
                                requestLoop = false;
                                break;
                        }
                    }
                }else{
                    output.println("Illegal login");
                    output.flush();
                }

                
                output.close();
                cSock.close();
                input.close();
                System.out.println("Connection lost.");
                System.out.println();
                
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
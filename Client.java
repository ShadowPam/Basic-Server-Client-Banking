import java.net.Socket;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;

public class Client {

    private Socket socket;
    private Scanner clientIn;
    private String serverIP;
    private int serverPort;

    public Client(String serverIP, int serverPort){
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        clientIn = new Scanner(System.in);
    }

    private void connect(String usr, String pwd){
        try {
            System.out.println("Connecting to Server on " + serverIP);
            socket = new Socket(serverIP, serverPort);
            System.out.println("Connected");
            PrintStream output = new PrintStream(socket.getOutputStream());
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //Send login request
            output.println("login");
            output.flush();

            //Wait for response
            String line = input.readLine();

            if(line != null){
                //send username and password
                output.println(usr + " " + pwd);
                output.flush();

                //Receive login success
                line = input.readLine();

                if(!line.equals("success")){
                    return;
                }
                //send login success to client
                System.out.println("Login successful");
                System.out.println();

                boolean running = true;

                while(running){
                    String[] command = clientIn.nextLine().split(" ");

                    switch(command[0]){
                        case "get":
                            if(command[1].equals("balance")){
                                //Send get balance req
                                output.println("get balance");
                                output.flush();

                                //Receive current balance for client and print it
                                line = input.readLine();
                                System.out.println(line);
                                break;
                            }

                        case "withdraw":
                            int withdrawAmount;
                            try {
                                withdrawAmount = Integer.parseInt(command[1]);
                            } catch (NumberFormatException e) {
                                //Throw error to client
                                System.out.println("Withdrawal amount not specified");
                                break;
                            }

                            //Send withdraw request
                            output.println("withdraw " + withdrawAmount);
                            output.flush();

                            line = input.readLine();
                            if(line.equals("illegal")){
                                System.out.println("You can't withdraw that much.");
                            }else{
                                System.out.println(line);
                            }
                            break;

                        case "deposit":
                            int depositAmount;
                            try {
                                depositAmount = Integer.parseInt(command[1]);
                            } catch (NumberFormatException e) {
                                //Throw error to client
                                System.out.println("Deposit amount not specified");
                                break;
                            }

                            //Send deposit request
                            output.println("deposit " + depositAmount);
                            output.flush();

                            line = input.readLine();
                            System.out.println(line);
                            break;

                        case "transfer":
                            int transferAmount;
                            try {
                                transferAmount = Integer.parseInt(command[1]);
                            } catch (NumberFormatException e) {
                                //Throw error to client
                                System.out.println("Transfer amount not specified");
                                break;
                            }

                            //Send transfer
                            output.println("transfer " + transferAmount + " " + command[2]);
                            output.flush();

                            //Receive information back and handle
                            line = input.readLine();

                            if(line.equals("e1")){
                                System.out.println("You can't transfer that much");
                            }else if(line.equals("e2")){
                                System.out.println("That username does not exist.");
                            }else{
                                System.out.println(line);
                            }
                            break;

                        case "logout":
                            running = false;
                            break;
                    }
                }

                //Send logout request
                output.println("logout");
                output.flush();

                System.out.println("Logging off...");
                output.close();
                input.close();
                socket.close();
                clientIn.close();
                System.exit(0);
            }        

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

        System.out.println("Welcome to Simple Banking App 101");
        while(true){
            System.out.println("Please login by following this syntax: login username passwd");
            String[] loginReq = c.clientIn.nextLine().split(" ");

            if(loginReq[0].equals("login")){
                c.connect(loginReq[1], loginReq[2]);
            }

            System.out.println("Illegal username or password");
        }
    }

}
package sample.server;

import sample.Data;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Data {

    private ServerSocket server;
    private Socket socket;
    private DBOperator dbOperator;

    private ExecutorService executorService;
    public ExecutorService getExecutorService() {return executorService;}

    private List<ClientHandler> openedBoxes;
    public List<ClientHandler> getOpenedBoxes() {return openedBoxes;}

    private Authorisation auth;
    public Authorisation getAuth() {return auth;}


    public Server(){
        try {
            this.dbOperator = new DBOperator();
            this.server = new ServerSocket(PORT);
            this.executorService = Executors.newCachedThreadPool();
            this.auth = new Authorisation(this.dbOperator);
            this.openedBoxes = Collections.synchronizedList(new ArrayList<ClientHandler>());
            System.out.println("Server running and awaiting connections");
            while (true){
                this.socket = server.accept();
                openedBoxes.add(new ClientHandler(this,socket));
                System.out.println("Client connected");

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                dbOperator.closeDBConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public boolean isNickBusy(String nick){
        for (ClientHandler box : openedBoxes) {
            if (box.getNick().equals(nick))return true;
        }
        return false;
    }

}

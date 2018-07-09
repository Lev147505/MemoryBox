package sample.server;

import sample.authservice.Authorisation;
import sample.authservice.DBOperator;
import sample.Data;
import sample.fileservice.FileManagerServer;

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
    private FileManagerServer fileManagerServer;

    private ExecutorService executorService;
    public ExecutorService getExecutorService() {return executorService;}

    private List<ClientHandler> openedBoxes;
    public List<ClientHandler> getOpenedBoxes() {return openedBoxes;}

    private Authorisation auth;
    public Authorisation getAuth() {return auth;}


    public Server(){
        try {
            this.dbOperator = new DBOperator();
            this.fileManagerServer = new FileManagerServer();
            this.server = new ServerSocket(PORT);
            this.executorService = Executors.newCachedThreadPool();
            this.auth = new Authorisation(this.dbOperator);
            this.openedBoxes = Collections.synchronizedList(new ArrayList<ClientHandler>());
            System.out.println("Server running and awaiting connections");
            while (true){
                this.socket = server.accept();
                openedBoxes.add(new ClientHandler(this,socket,fileManagerServer));
                System.out.println("Client connected");
                statistic();
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

    public boolean exitUser(ClientHandler client){
        if (openedBoxes.remove(client)){
            statistic();
            return true;
        }
        return false;
    }

    public void statistic(){
        System.out.println("Size: " + openedBoxes.size());
        openedBoxes.forEach((user) -> user.sendMsg(STATISTIC + "~" + dbOperator.getRegisteredUsers() + "~" + openedBoxes.size()));
    }

}

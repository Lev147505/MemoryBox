package sample.server;

import sample.Data;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Data {

    private ServerSocket server;
    private Socket socket;

    private ExecutorService executorService;
    public ExecutorService getExecutorService() {return executorService;}

    private Vector<ClientHandler> openedBoxes;
    public Vector<ClientHandler> getOpenedBoxes() {return openedBoxes;}

    private LoginService login;
    private RegService register;
    public LoginService getLogin (){return login;}
    public RegService getRegister() {return register;}

    public Server(){
        try {
            this.server = new ServerSocket(PORT);
            this.executorService = Executors.newCachedThreadPool();
            this.login = new LoginService();
            this.register = new RegService();
            this.openedBoxes = new Vector<>();
            System.out.println("Server running and awaiting connections");
            while (true){
                this.socket = server.accept();
                openedBoxes.add(new ClientHandler(this,socket));
                System.out.println("Client connected");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean isNickBusy(String nick){
        for (ClientHandler box : openedBoxes) {
            if (box.getNick().equals(nick))return true;
        }
        return false;
    }

}

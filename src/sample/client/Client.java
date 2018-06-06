package sample.client;

import sample.Controller;
import sample.Data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client implements Data{

    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private Controller controller;

    public Client(Controller controller){
        try{
            this.socket = new Socket(SERVER_URL, PORT);
            this.out = new DataOutputStream(socket.getOutputStream());
            this.in = new DataInputStream(socket.getInputStream());
            this.controller = controller;
            init();
        }catch (IOException exc){
            exc.printStackTrace();
        }

    }

    public void init (){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        if (in.available() != 0) {
                            String msg = in.readUTF();
                            if (msg.startsWith(AUTH_DONE)) {
                                controller.writeInTextArea("You in your MemoryBox, " + msg.substring(AUTH_DONE.length())+"\n");
                                //метод для смены окна на рабочее
                                break;
                            }
                            if (msg.startsWith(REG_DATA_OK)) {
                                controller.writeInTextArea("Success registration!\nEnter, using your login and password.\n");
                            } else if (msg.startsWith(SAME_LOGINPASS)) {
                                controller.writeInTextArea("Login/Password combination is not unique!\n");
                            } else if (msg.startsWith(SAME_NICK)) {
                                controller.writeInTextArea("Nick is not unique!\n");
                            }else if (msg.startsWith(BUSY_BOX)){
                                controller.writeInTextArea(msg.substring(BUSY_BOX.length())+"\n");
                            }
                        }
                    }
                    while (true) {
                        //String msg = in.readUTF();
                        //if (msg.startsWith(UPDATE_ONLINE_LIST)){
                        //    view.upOnlineUsers(msg.substring(UPDATE_ONLINE_LIST.length()));
                        //}else view.showMsg(msg);
                    }
                }catch (IOException exc){
                    exc.printStackTrace();
                }
            }
        }).start();
    }

    public void auth(String login, String password){
        try {
            out.writeUTF(AUTH_ASK + " " + login + " " + password);
        }catch (IOException exc){
            exc.printStackTrace();
        }
    }

    public void reg(String login, String password, String nickName){
        try {
            out.writeUTF(REG_ASK + " " + login + " " + password + " " + nickName);
        }catch (IOException exc){
            exc.printStackTrace();
        }
    }
}

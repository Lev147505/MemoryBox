package sample.client;

import sample.Data;
import sample.client.controllers.ControllerLoginArea;
import sample.client.controllers.ControllerWorkArea;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client implements Data{

    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private ControllerLoginArea controllerLoginArea;
    private ControllerWorkArea controllerWorkArea;
    private ScreenManager screenManager;

    public Client(){
        try{
            this.socket = new Socket(SERVER_URL, PORT);
            this.out = new DataOutputStream(socket.getOutputStream());
            this.in = new DataInputStream(socket.getInputStream());
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
                                screenManager.setLogin(true);
                                screenManager.changeScreen();//смена root на сцене javafx
                                break;
                            }
                            if (msg.startsWith(REG_DATA_OK)) {
                                controllerLoginArea.writeInTextArea("Success registration!\nEnter, using your login and password.\n");
                            } else if (msg.startsWith(SAME_LOGINPASS)) {
                                controllerLoginArea.writeInTextArea("Login/Password combination is not unique!\n");
                            } else if (msg.startsWith(SAME_NICK)) {
                                controllerLoginArea.writeInTextArea("Nick is not unique!\n");
                            }else if (msg.startsWith(BUSY_BOX)){
                                controllerLoginArea.writeInTextArea(msg.substring(BUSY_BOX.length())+"\n");
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
                } catch (Exception e) {
                    e.printStackTrace();
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

    public void setControllerLoginArea(ControllerLoginArea controllerLoginArea) {
        this.controllerLoginArea = controllerLoginArea;
    }

    public void setControllerWorkArea(ControllerWorkArea controllerWorkArea) {
        this.controllerWorkArea = controllerWorkArea;
    }

    public void setScreenManager(ScreenManager screenManager) {
        this.screenManager = screenManager;
    }
}

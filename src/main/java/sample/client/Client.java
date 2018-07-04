package sample.client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Data;
import sample.gui.ControllerLoginArea;
import sample.gui.ControllerWorkArea;
import sample.gui.ScreenManager;

import java.io.*;
import java.net.Socket;

public class Client implements Data{

    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private ControllerLoginArea controllerLoginArea;
    private ControllerWorkArea controllerWorkArea;
    private ScreenManager screenManager;

    private boolean clientActive;

    //Список файлов хранящихся в профиле пользователя,
    //считывается из текстового файла на сервере при вызове initArrayList(), сразу после инициплизации ника
    private ObservableList<String> filesList;

    public Client(){
        try{
            this.socket = new Socket(SERVER_URL, PORT);
            this.out = new DataOutputStream(socket.getOutputStream());
            this.in = new DataInputStream(socket.getInputStream());
            this.filesList = FXCollections.observableArrayList();
            init();
        }catch (IOException exc){
            exc.printStackTrace();
        }
    }

    public void init (){
        new Thread(new Runnable() {
            @Override
            public void run() {
                clientActive = true;
                String msg;
                String[] elements;
                try {
                    while (clientActive) {
                        if (in.available() != 0) {
                            msg = in.readUTF();
                            if (msg.startsWith(AUTH_DONE)) {
                                screenManager.setLogin(true);// меняем флаговую переменную в ScreenManager на true
                                initObservableArrayList();
                                screenManager.changeScreen(filesList);//смена root на сцене javafx и трасфер списка файлов через менеджера экранов
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
                    while (clientActive) {
                        if (in.available() != 0){
                            msg = in.readUTF();
                            if (msg.startsWith(REFRESH_LIST)){
                                elements = msg.split("~");
                                String[] finalElements = elements;
                                Platform.runLater(() -> {
                                    filesList.add(finalElements[finalElements.length-1]);
                                    filesList.forEach((str) -> sendMsg(REFRESH_LIST + "~" + str));
                                });//Лютая штука для корректировки элемента UI вне потокак FX Application
                                //В этом же потоке шлём всю коллекцию файлов на сервер чтобы переписать list.txt
                            }
                            if (msg.startsWith(SHOW_FILE)){
                                elements = msg.split("~");
                                controllerWorkArea.appendCurrentFileText(elements[elements.length-1] + "\n");
                            }
                            if (msg.startsWith(DROP_OK)){
                                elements = msg.split("~");
                                String[] finalElements = elements;
                                Platform.runLater(() -> {
                                    filesList.remove(finalElements[finalElements.length-1]);
                                    filesList.forEach((str) -> sendMsg(REFRESH_LIST + "~" + str));
                                });
                            }
                        }
                    }
                }catch (IOException exc){
                    exc.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void sendMsg(String msg){
        try{
            out.writeUTF(msg);
            out.flush();
        }catch (IOException exc){
            exc.printStackTrace();
        }
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

    public void exit(){
        try {
            clientActive = false;
            socket.close();
            out.close();
            in.close();
            screenManager.closePrimary();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initObservableArrayList() throws Exception{
        String str;
        String[] elements;
        while (true){
            if (in.available() != 0){
                while (in.available() != 0&&(str = in.readUTF()).startsWith(INIT_OAL)){
                    elements = str.split("\\s");
                    filesList.add(elements[elements.length-1]);
                }
                break;

            }
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

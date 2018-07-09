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
            this.clientActive = true;
            init();
        }catch (IOException exc){
            exc.printStackTrace();
        }
    }

    public void init (){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msg;
                String[] elements;
                try {
                    point:while (clientActive) {
                        if (in.available() != 0) {
                            msg = in.readUTF();
                            elements = msg.split("~");
                            String[] finalElements = elements;
                            switch (elements[0]){
                                case AUTH_OK:
                                    initObservableArrayList();
                                    screenManager.setLogin(true);// меняем флаговую переменную в ScreenManager на true
                                    screenManager.changeScreen(filesList);//смена root на сцене javafx и трасфер списка файлов через менеджера экранов
                                    break point;
                                case REG_DATA_OK:
                                    controllerLoginArea.writeInTextArea("Success registration!\nEnter, using your login and password.\n");
                                    break;
                                case SAME_LOGINPASS:
                                    controllerLoginArea.writeInTextArea("Login/Password combination is not unique!\n");
                                    break;
                                case SAME_NICK:
                                    controllerLoginArea.writeInTextArea("Nick is not unique!\n");
                                    break;
                                case BUSY_BOX:
                                    controllerLoginArea.writeInTextArea(elements[1] + " " + elements[2]+" !!!\n");
                                    break;
                                case STATISTIC:
                                    Platform.runLater(() -> controllerLoginArea.setStatistic(finalElements[1], finalElements[2]));
                                    break;
                                case INFO_ASK:
                                    controllerLoginArea.appendTextAreaInfo(elements[elements.length-1]);
                                    break;
                                case EXIT_OK:
                                    socket.close();
                                    out.close();
                                    in.close();
                                    Platform.runLater(() -> screenManager.closePrimary());
                                    clientActive = false;
                                    break;
                            }
                        }
                    }
                    while (clientActive) {
                        if (in.available() != 0){
                            msg = in.readUTF();
                            elements = msg.split("~");
                            String[] finalElements = elements;
                            switch (elements[0]){
                                case DOWNLOAD_ASK:
                                    controllerWorkArea.getFileManagerClient().writeDownloadFile(in,elements[elements.length-1]);
                                    break;
                                case REFRESH_LIST:
                                    Platform.runLater(() -> {
                                        filesList.add(finalElements[finalElements.length-1]);
                                        filesList.forEach((str) -> sendMsg(REFRESH_LIST + "~" + str));
                                    });//Лютая штука для корректировки элемента UI вне потокак FX Application
                                    //В этом же потоке шлём всю коллекцию файлов на сервер чтобы переписать list.txt;
                                    break;
                                case SHOW_FILE:
                                    controllerWorkArea.appendCurrentFileText(elements[elements.length-1] + "\n");
                                    break;
                                case DROP_OK:
                                    Platform.runLater(() -> {
                                        filesList.remove(finalElements[finalElements.length-1]);
                                        filesList.forEach((str) -> sendMsg(REFRESH_LIST + "~" + str));
                                    });
                                    break;
                                case EXIT_OK:
                                    socket.close();
                                    out.close();
                                    in.close();
                                    Platform.runLater(() -> screenManager.closePrimary());
                                    clientActive = false;
                                    break;
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
            out.writeUTF(AUTH_ASK + "~" + login + "~" + password);
        }catch (IOException exc){
            exc.printStackTrace();
        }
    }

    public void reg(String login, String password, String nickName){
        try {
            out.writeUTF(REG_ASK + "~" + login + "~" + password + "~" + nickName);
        }catch (IOException exc){
            exc.printStackTrace();
        }
    }

    public void exit(){
        sendMsg(EXIT_ASK);
    }

    private void initObservableArrayList() throws Exception{
        String str;
        String[] elements;
        while (true){
            if (in.available() != 0){
                str = in.readUTF();
                if (str.startsWith(EMPTY_FILE)){
                    break;
                }
                if(str.startsWith(INIT_OAL)){
                    elements = str.split("~");
                    filesList.add(elements[elements.length-1]);
                }
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

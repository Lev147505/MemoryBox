package sample.server;

import sample.Data;
import sample.fileservice.FileManagerServer;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Data {

    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private String nick;
    public String getNick(){return this.nick;}

    public ClientHandler(final Server server, Socket socket, FileManagerServer fileManagerServer){
        this.server = server;
        this.socket = socket;
        this.nick = "undefined";

        try {
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
        }catch (IOException exc){
            exc.printStackTrace();
        }

        server.getExecutorService().submit(new Runnable() {
            @Override
            public void run() {
                String msg;
                String elements[];
                try {
                    while (true){

                        if (in.available() != 0){
                            msg = in.readUTF();
                            //если с клиента пришло сообщение начинающееся с кодового слова AUTH_ASK
                            // и метод logIn выполнился и вернул true, тогда рвём цикл мониторинга входящего потока
                            if (msg.startsWith(AUTH_ASK) && logIn(msg)){break;}
                            //если с клиента пришло сообщение начинающееся с кодового слова REG_ASK
                            // вызываем метод regBox для проверки и заведения новых данных в бвзу Authorisation
                            if (msg.startsWith(REG_ASK)){
                                regBox(msg);
                            }
                        }
                    }
                    while (true){
                        if (in.available() != 0){
                            msg = in.readUTF();
                            if (msg.startsWith(UP_FILE)){
                                elements = msg.split("~");
                                fileManagerServer.uploadFile(nick,in, elements[1], elements[elements.length-1]);
                                sendMsg(REFRESH_LIST + "~" + elements[1]);
                            }
                            if (msg.startsWith(REFRESH_LIST)){
                                elements = msg.split("~");
                                fileManagerServer.refreshList(nick,in, elements[elements.length-1]);
                            }
                            if (msg.startsWith(SHOW_FILE)){
                                elements = msg.split("~");
                                fileManagerServer.showCurrentFile(nick,out, elements[elements.length-1]);
                            }
                            if (msg.startsWith(DROP_FILE)){
                                elements = msg.split("~");
                                fileManagerServer.deleteCurrentFile(nick,out,elements[elements.length-1]);
                            }
                        }
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean  logIn(String msg) throws IOException{
        String[] elements = msg.split("\\s");
        String nick = server.getAuth().getNickByLoginPass(elements[1],elements[2]);
        if (nick != null){
            if (!server.isNickBusy(nick)){
                sendMsg(AUTH_DONE + " " + nick);
                this.nick = nick;
                sendListFile();//Читаем список загруженных файлов и отправляем построчно клиенту для инициализации коллекции хранимых файлов
                return true;
            }else {
                sendMsg(BUSY_BOX + " " + nick + " " + "already in MBox");
            }
        }
        return false;
    }

    private void   regBox(String msg) throws IOException{
        String[] elements = msg.split("\\s");
        String result = server.getAuth().checkNewUserData(elements[1],elements[2],elements[3]);
        switch (result){
            case SAME_LOGINPASS:
                sendMsg(SAME_LOGINPASS);
                break;
            case SAME_NICK:
                sendMsg(SAME_NICK);
                break;
            case REG_DATA_OK:
                sendMsg(REG_DATA_OK);
                break;
        }
    }

    public void sendMsg (String msg){
        try{
            out.writeUTF(msg);
            out.flush();
        }catch (IOException exc){
            exc.printStackTrace();
        }
    }

    private void sendListFile(){
        String str;
        try {
            BufferedReader in = new BufferedReader(new FileReader( GENERAL_DIR + nick.toLowerCase() + "/list.txt"));
            while ((str = in.readLine()) != null){
                sendMsg(INIT_OAL + " " + str);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

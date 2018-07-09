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
    private boolean clientActive;
    private String nick;
    public String getNick(){return this.nick;}

    public ClientHandler(final Server server, Socket socket, FileManagerServer fileManagerServer){
        this.server = server;
        this.socket = socket;
        this.nick = "undefined";
        this.clientActive = true;

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
                    while (clientActive){
                        if (in.available() != 0){
                            msg = in.readUTF();
                            //если с клиента пришло сообщение начинающееся с кодового слова AUTH_ASK
                            // и метод logIn выполнился и вернул true, тогда рвём цикл мониторинга входящего потока
                            if (msg.startsWith(AUTH_ASK) && logIn(msg)){
                                server.getOpenedBoxes().forEach((obj) -> System.out.println(obj.nick));
                                break;}
                            //если с клиента пришло сообщение начинающееся с кодового слова REG_ASK
                            // вызываем метод regBox для проверки и заведения новых данных в бвзу Authorisation
                            if (msg.startsWith(REG_ASK)){
                                regBox(msg);
                            }
                            if(msg.startsWith(INFO_ASK)){
                                fileManagerServer.getInfoFile(out);
                            }
                            if (msg.startsWith(EXIT_ASK)){
                                if (server.exitUser(getThis())){
                                    sendMsg(EXIT_OK);
                                    if (in != null || out != null){
                                        in.close();
                                        out.close();
                                    }
                                }
                                clientActive = false;
                            }
                        }
                    }
                    while (clientActive){
                        if (in.available() != 0){
                            msg = in.readUTF();
                            elements = msg.split("~");
                            switch (elements[0]){
                                case UP_FILE:
                                    fileManagerServer.uploadFile(nick,in, elements[1], elements[elements.length-1]);
                                    sendMsg(REFRESH_LIST + "~" + elements[1]);
                                    break;
                                case REFRESH_LIST:
                                    fileManagerServer.refreshList(nick,in, elements[elements.length-1]);
                                    break;
                                case SHOW_FILE:
                                    fileManagerServer.showCurrentFile(nick,out, elements[elements.length-1]);
                                    break;
                                case DROP_FILE:
                                    fileManagerServer.deleteCurrentFile(nick,out,elements[elements.length-1]);
                                    break;
                                case DOWNLOAD_ASK:
                                    fileManagerServer.sendCurrentFile(nick,out, elements[elements.length-1]);
                                    break;
                                case EXIT_ASK:
                                    if (server.exitUser(getThis())){
                                        sendMsg(EXIT_OK);
                                        if (in != null || out != null){
                                            in.close();
                                            out.close();
                                        }
                                    }
                                    clientActive = false;
                                    break;
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
        String[] elements = msg.split("~");
        String nick = server.getAuth().getNickByLoginPass(elements[1],elements[2]);
        if (nick != null){
            if (!server.isNickBusy(nick)){
                sendMsg(AUTH_OK + "~" + nick);
                this.nick = nick;
                sendListFile();//Читаем список загруженных файлов и отправляем построчно клиенту для инициализации коллекции хранимых файлов
                return true;
            }else {
                sendMsg(BUSY_BOX + "~" + nick + "~" + "already in MBox");
            }
        }
        return false;
    }

    private void   regBox(String msg) throws IOException{
        String[] elements = msg.split("~");
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
                server.statistic();
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
                sendMsg(INIT_OAL + "~" + str);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ClientHandler getThis(){
        return this;
    }
}

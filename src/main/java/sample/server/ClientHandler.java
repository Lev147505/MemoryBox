package sample.server;

import sample.Data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Data {

    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private String nick;
    public String getNick(){return this.nick;}

    public ClientHandler(final Server server, Socket socket){
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
                while (true){
                    try {
                        if (in.available() != 0){
                            String msg = in.readUTF();
                            //если с клиента пришло сообщение начинающееся с кодового слова AUTH_ASK
                            // и метод logIn выполнился и вернул true, тогда рвём цикл мониторинга входящего потока
                            if (msg.startsWith(AUTH_ASK) && logIn(msg)){break;}
                            //если с клиента пришло сообщение начинающееся с кодового слова REG_ASK
                            // вызываем метод regBox для проверки и заведения новых данных в бвзу Authorisation1
                            if (msg.startsWith(REG_ASK)){
                                regBox(msg);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                for (ClientHandler ch:server.getOpenedBoxes()) {
                    System.out.println(ch.getNick()+" "+"open his MemoryBox");
                }
            }
        });
    }

    private boolean  logIn(String msg) throws IOException{
        String[] elements = msg.split(" ");
        String nick = server.getAuth().getNickByLoginPass(elements[1],elements[2]);
        if (nick != null){
            if (!server.isNickBusy(nick)){
                sendMsg(AUTH_DONE + " " + nick);
                this.nick = nick;
                return true;
            }else {
                sendMsg(BUSY_BOX + " " + nick + " " + "already in MBox");
            }
        }
        return false;
    }

    private void   regBox(String msg) throws IOException{
        String[] elements = msg.split(" ");
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
}

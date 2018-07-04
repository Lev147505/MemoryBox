package sample.fileservice;

import sample.Data;
import java.io.*;

public class FileManagerServer implements Data {

    public void uploadFile(String nick, DataInputStream in, String fileName, String firsStr) {
        String elements[];
        String msg;
        try {
            File newFile = new File(GENERAL_DIR + nick + "/" + fileName);
            if (!newFile.exists()){
                newFile.createNewFile();
            }
            PrintWriter out = new PrintWriter(newFile.getAbsolutePath());
            out.println(firsStr);
            while (in.available() != 0 && (msg = in.readUTF()).startsWith(UP_FILE)){
                elements = msg.split("~");
                out.println(elements[2]);
            }
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void refreshList(String nick, DataInputStream in, String firsStr){
        String elements[];
        String msg;
        PrintWriter out = null;
        try {
            out = new PrintWriter(GENERAL_DIR + nick + "/list.txt");
            out.println(firsStr);
            while (in.available() != 0 && (msg = in.readUTF()).startsWith(REFRESH_LIST)) {
                elements = msg.split("~");
                out.println(elements[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (out != null){
                out.close();
            }
        }
    }

    public void showCurrentFile(String nick, DataOutputStream out, String fileName){
        String str;
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(GENERAL_DIR + nick + "/" + fileName));
            while ((str = in.readLine()) != null){
                out.writeUTF(SHOW_FILE + "~" + str);
                out.flush();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void deleteCurrentFile(String nick, DataOutputStream out, String fileName){
        File file = new File(GENERAL_DIR + nick + "/" + fileName);
        if (file.exists()){
            try {
                if (file.delete()){
                    out.writeUTF(DROP_OK + "~" + fileName);
                    out.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

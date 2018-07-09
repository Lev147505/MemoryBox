package sample.fileservice;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sample.Data;
import sample.client.Client;

import java.io.*;

public class FileManagerClient implements Data {

    private File file;
    private File newFile;
    private String fileExtension;
    private Stage subStage;
    private FileChooser fileChooser;
    private ObservableList<String> filesList;

    BufferedReader in = null;
    PrintWriter out = null;

    public FileManagerClient(){
        this.file = null;
        this.newFile = null;
        this.fileExtension = null;
        this.subStage = new Stage();
        this.fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
    }

    public void uploadFile(Client client){
        fileChooser.setTitle("Uploading Files in MemoryBox...");
        file = fileChooser.showOpenDialog(subStage);

        if (file != null){
            fileExtension = getFileExtension(file.getName());
            try {
                String str;
                in = new BufferedReader(new FileReader( file.getAbsoluteFile()));
                while ((str = in.readLine()) != null){client.sendMsg(UP_FILE + "~" + file.getName() + "~" + str);}

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
    }

    public void setDownloadedFile(Client client, String currentFile){
        fileChooser.setTitle("Downloading Files out of MemoryBox...");
        file = fileChooser.showSaveDialog(subStage);
        client.sendMsg(DOWNLOAD_ASK + "~" + currentFile);
    }

    public void writeDownloadFile(DataInputStream in, String firstRow){
        String elements[];
        String msg;
        if (file != null){
            try {
                out = new PrintWriter(file.getAbsolutePath());
                out.println(firstRow);
                while (in.available() != 0 && (msg = in.readUTF()).startsWith(DOWNLOAD_ASK)){
                    elements = msg.split("~");
                    out.println(elements[elements.length-1]);
                }
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getFileExtension(String str) {
        int index = str.indexOf('.') + 1;
        return index == -1? null : str.substring(index);
    }

    public void setFilesList(ObservableList<String> filesList) {
        this.filesList = filesList;
    }
}

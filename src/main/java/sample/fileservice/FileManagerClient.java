package sample.fileservice;

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

    public FileManagerClient(){
        this.file = null;
        this.newFile = null;
        this.fileExtension = null;
        this.subStage = new Stage();
        this.fileChooser = new FileChooser();
        fileChooser.setTitle("Uploading Files in MemoryBox...");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
    }

    public void uploadFile(Client client){

        BufferedReader in = null;
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

    public boolean deleteFile(String fileName) {
        return false;
    }

    public String generateFilePath(String fileName, String nick){
        return GENERAL_DIR+nick+"/"+fileName;
    }

    private String getFileExtension(String str) {
        int index = str.indexOf('.') + 1;
        return index == -1? null : str.substring(index);
    }

    public void setFilesList(ObservableList<String> filesList) {
        this.filesList = filesList;
    }
}

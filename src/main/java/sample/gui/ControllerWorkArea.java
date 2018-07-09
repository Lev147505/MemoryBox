package sample.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sample.Data;
import sample.client.Client;
import sample.fileservice.FileManagerClient;

import java.io.DataInputStream;
import java.io.File;

public class ControllerWorkArea implements Data{

    private Client client;
    private FileManagerClient fileManagerClient;
    private String currentFile;

    @FXML private Button exitBtn;
    @FXML private Button uploadFiles;
    @FXML private Button deleteCurrentFile;
    @FXML private ListView<String> listView;
    @FXML private TextArea currentFileText;

    public void initControllerWorkArea (Client client, FileManagerClient fileManagerClient){
        this.client = client;
        this.fileManagerClient = fileManagerClient;
        this.currentFile = null;
        client.setControllerWorkArea(this);
    }

    public void setListView(ObservableList<String> filesList){
        listView.setItems(filesList);
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                currentFile = newValue;
                client.sendMsg(SHOW_FILE + "~" + newValue);
                currentFileText.clear();
            }
        });
    }

    public void appendCurrentFileText(String text){
        currentFileText.appendText(text);
    }

    @FXML
    public void deleteCurrentFile(){
        if (currentFile != null){
            client.sendMsg(DROP_FILE + "~" + currentFile);
        }
    }

    @FXML
    public void chooseDownloadFile(){
        if (currentFile != null){
            fileManagerClient.setDownloadedFile(client,currentFile);
        }
    }

    @FXML
    public void exitMBox(){
        client.exit();
    }

    @FXML
    public  void chooseUploadFiles(){fileManagerClient.uploadFile(client);}

    public FileManagerClient getFileManagerClient() {
        return fileManagerClient;
    }
}

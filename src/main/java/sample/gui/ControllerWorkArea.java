package sample.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import sample.Data;
import sample.client.Client;
import sample.fileservice.FileManagerClient;

public class ControllerWorkArea{

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
                client.sendMsg(Data.SHOW_FILE + "~" + newValue);
                currentFileText.clear();
            }
        });
    }

    public void appendCurrentFileText(String text){
        currentFileText.appendText(text);
    }

    public void deleteCurrentFile(){
        if (currentFile != null){
            client.sendMsg(Data.DROP_FILE + "~" + currentFile);
        }
    }

    @FXML
    public void exitMBox(){
        client.exit();
    }

    @FXML
    public  void chooseFiles(){fileManagerClient.uploadFile(client);}

}

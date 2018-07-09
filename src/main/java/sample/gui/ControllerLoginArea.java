package sample.gui;

import javafx.fxml.FXML;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.Data;
import sample.client.Client;

public class ControllerLoginArea implements Data{
    private Client client;
    private Stage primaryStage;

    @FXML private TextField loginField;
    @FXML private PasswordField passField;
    @FXML private TextField nick;
    @FXML private TextArea textArea;
    @FXML private Button exit;
    @FXML private TextArea haveMBox;
    @FXML private TextArea inMBox;
    private TextArea info;

    public void initControllerLoginArea(Client client, Stage primaryStage){
        this.client = client;
        this.primaryStage = primaryStage;
        client.setControllerLoginArea(this);
    }

    @FXML
    public void logIn() {
        if (loginField.getText().length() != 0 && passField.getText().length() != 0){
            client.auth(loginField.getText(),passField.getText());
        }
        loginField.clear();
        passField.clear();
        nick.clear();
    }

    @FXML
    public void register(){
        if (loginField.getText().length() != 0 && passField.getText().length() != 0 && nick.getText().length() != 0){
            client.reg(loginField.getText(),passField.getText(), nick.getText());
        }
    }

    @FXML
    public void writeInTextArea(String msg){
        textArea.appendText(msg);
    }

    @FXML
    public void setStatistic(String haveMBox, String inMBox){
        this.haveMBox.clear();
        this.inMBox.clear();
        this.haveMBox.appendText(haveMBox);
        this.inMBox.appendText(inMBox);
    }

    @FXML
    public void exitMBox(){
        client.exit();
    }

    @FXML
    public void showMBoxInfo(){
        client.sendMsg(INFO_ASK);

        info = new TextArea();
        info.setEditable(false);
        info.setCenterShape(true);

        StackPane infoLayout = new StackPane();
        infoLayout.getChildren().add(info);

        Scene infoScene = new Scene(infoLayout, 300, 150);

        Stage infoWindow = new Stage();
        infoWindow.setTitle("About MBox");
        infoWindow.setScene(infoScene);
        infoWindow.initModality(Modality.WINDOW_MODAL);
        infoWindow.initOwner(primaryStage);

        infoWindow.show();
    }

    public void appendTextAreaInfo(String str){
        info.appendText(str + "\n");
    }
}

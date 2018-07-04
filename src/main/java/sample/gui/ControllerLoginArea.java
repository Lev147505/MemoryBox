package sample.gui;

import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import sample.client.Client;

public class ControllerLoginArea {
    private Client client;

    @FXML private TextField loginField;
    @FXML private PasswordField passField;
    @FXML private TextField nick;
    @FXML private TextArea textArea;
    @FXML private Button exit;

    public void initControllerLoginArea(Client client){
        this.client = client;
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
    public void exitMBox(){
        client.exit();
    }
}

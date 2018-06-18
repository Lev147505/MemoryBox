package sample.client;

import javafx.fxml.FXML;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;


public class Controller implements ViewControl {
    @FXML private TextField loginField;
    @FXML private PasswordField passField;
    @FXML private TextField nick;
    @FXML private TextArea textArea;

    private Client client = new Client(this);

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

}

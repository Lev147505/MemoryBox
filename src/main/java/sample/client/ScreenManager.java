package sample.client;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.client.controllers.ControllerLoginArea;
import sample.client.controllers.ControllerWorkArea;

public class ScreenManager {
    private Stage primaryStage;
    private Parent loginGUI;
    private Parent workGUI;
    private FXMLLoader loader;
    private FXMLLoader loader1;
    private ControllerLoginArea controllerLoginArea;
    private ControllerWorkArea controllerWorkArea;
    private Scene scene;
    private Main main;
    private boolean login = false;


    public ScreenManager (Stage primaryStage, Client client, Main main) throws Exception{
        client.setScreenManager(this);
        this.primaryStage = primaryStage;
        this.main = main;
        loader = new FXMLLoader(getClass().getClassLoader().getResource("login_area.fxml"));
        loginGUI = loader.load();
        controllerLoginArea = loader.getController();
        controllerLoginArea.initControllerLoginArea(client);
        loader1 = new  FXMLLoader(getClass().getClassLoader().getResource("work_area.fxml"));
        workGUI = loader1.load();
        controllerWorkArea = loader1.getController();
        controllerWorkArea.initControllerWorkArea(client);
        scene = new Scene(loginGUI, 600, 400);
        this.primaryStage.setTitle("Memory Box");
        this.primaryStage.setScene(scene);
        this.primaryStage.setResizable(false);
        this.primaryStage.show();
    }

    public void changeScreen() throws Exception{
        if (login){
            primaryStage.getScene().setRoot(workGUI);
        }else {
            primaryStage.getScene().setRoot(loginGUI);
        }
    }

    public void setLogin(boolean login) {
        this.login = login;
    }
}

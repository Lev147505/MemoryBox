package sample.client;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    private Client client;
    private ScreenManager screenManager;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init(){
        this.client = new Client();
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        screenManager = new ScreenManager(primaryStage, client, this);
    }

}

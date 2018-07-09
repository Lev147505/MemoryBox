package sample.gui;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.client.Client;
import sample.fileservice.FileManagerClient;

public class ScreenManager {
    private Stage primaryStage;
    private FileManagerClient fileManagerClient;
    private Parent loginGUI;
    private Parent workGUI;
    private FXMLLoader loginLoader;
    private FXMLLoader workLoader;
    private ControllerLoginArea controllerLoginArea;
    private ControllerWorkArea controllerWorkArea;
    private Scene scene;
    private boolean login = false;


    public ScreenManager (Stage primaryStage, Client client) throws Exception{
        client.setScreenManager(this);
        this.primaryStage = primaryStage;
        this.fileManagerClient = new FileManagerClient();

        //сщздание root`а для авторизации, получение контроллера и его иницализация в клиенте
        loginLoader = new FXMLLoader(getClass().getClassLoader().getResource("login_area.fxml"));
        loginGUI = loginLoader.load();
        controllerLoginArea = loginLoader.getController();
        controllerLoginArea.initControllerLoginArea(client,primaryStage);

        //создание root`а для рабочего окна, получение контроллера и его иницализация в клиенте
        //передача ссылки на объект FileManagerClient контроллеру
        workLoader = new  FXMLLoader(getClass().getClassLoader().getResource("work_area.fxml"));
        workGUI = workLoader.load();
        controllerWorkArea = workLoader.getController();
        controllerWorkArea.initControllerWorkArea(client, fileManagerClient);

        //Создаём сцену и отдаём ей декорации для формы авторизации, ставим сцену на подмостки
        scene = new Scene(loginGUI, 600, 400);
        this.primaryStage.setTitle("Memory Box");
        this.primaryStage.setScene(scene);
        this.primaryStage.setResizable(false);
        this.primaryStage.show();
    }

    //Смена декораций(root) на сцене после авторизации, вызывается клиентом после подтверждения авторизации с сервера
    public void changeScreen(ObservableList<String> filesList) throws Exception{
        if (login){
            this.primaryStage.getScene().setRoot(workGUI); //меняем декорации на сцене
            controllerWorkArea.setListView(filesList); //отдаём контроллеру коллекцию со списком файлов уже добавленных и отрисовываем на ListView
            fileManagerClient.setFilesList(filesList); //инициализируем коллекцию существующих файлов в менеджере файлов
        }else {
            this.primaryStage.getScene().setRoot(loginGUI);
        }
    }

    //Вызывается из клиента при нажатии на EXIT в рабочем окне MBox
    public void closePrimary(){
        this.primaryStage.close();
    }

    public void setLogin(boolean login) {
        this.login = login;
    }
}

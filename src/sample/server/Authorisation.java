package sample.server;

import sample.Data;

import java.util.ArrayList;

public class Authorisation implements Data {

    //Коллекция содержит список объектов
    //BoxOwner, каждый из которых содержит
    //данные о владельце коробки
    protected static ArrayList<BoxOwner> boxOwners = new ArrayList<>();

    //Внутренний класс инкапсулирует
    //информацию о владельце коробки
    protected class BoxOwner{
        protected String login;
        protected String password;
        protected String nick;

        protected BoxOwner(String login, String password, String nick){
            this.login = login;
            this.password = password;
            this.nick = nick;
        }
    }
}

package sample.authservice;

import sample.Data;

public class Authorisation implements Data {

    private DBOperator dbOperator;

    public Authorisation(DBOperator dbOperator){
        this.dbOperator = dbOperator;
    }

    public String getNickByLoginPass(String login, String password) {
        return dbOperator.findNickByLoginPass(login,password);
    }

    public String checkNewUserData(String login, String password,String nick){
        return dbOperator.checkRegistrationData(login,password,nick);
    }
}

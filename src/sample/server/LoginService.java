package sample.server;


public class LoginService extends Authorisation  {

    public LoginService(){
        boxOwners.add(new BoxOwner("1","1","Rick"));
    }

    public String getNickByLoginPass(String login, String password) {
        for (BoxOwner bo : boxOwners) {
            if (bo.login.equals(login) && bo.password.equals(password))return bo.nick;
        }
        return null;
    }
}

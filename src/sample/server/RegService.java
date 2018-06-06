package sample.server;

public class RegService extends Authorisation {

    protected String checkNewUserData(String login, String password,String nick){
        for (BoxOwner bo : boxOwners) {
            if (bo.login.equals(login) && bo.password.equals(password)){
                return SAME_LOGINPASS;
            }else if (bo.nick.equals(nick)){
                return SAME_NICK;
            }
        }
        boxOwners.add(new BoxOwner(login,password,nick));
        return REG_DATA_OK;
    }
}

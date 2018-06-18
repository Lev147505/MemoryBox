package sample.server;

import sample.Data;

import java.sql.*;

public class DBOperator implements Data{
    private Connection connection;
    private Statement statement;
    private ResultSet rs;

    public DBOperator() throws Exception{
        connect();
    }

    private void connect() throws Exception{
        Class.forName("com.mysql.cj.jdbc.Driver");
        this.connection = DriverManager.getConnection(DB_URL,DB_USER_NAME,DB_PASSWORD);
    }

    public void closeDBConnection() throws Exception{
        if (!connection.isClosed())connection.close();
    }

    public String findNickByLoginPass(String login, String password){
        String sqlQuery = "SELECT nick FROM users WHERE login = '" + login + "' AND password = '" + password + "';";
        System.out.println(sqlQuery);
        try {
            statement = connection.createStatement();
            rs = statement.executeQuery(sqlQuery);
            if (rs.next()){
                return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                if (rs!=null)rs.close();
                if (statement!=null)statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String checkRegistrationData(String login, String password, String nick){
        String sqlRegister = "INSERT INTO users (`login`, `password`, `nick`) VALUES ( '"+login+"', '"+password+"', '"+nick+ "');";
        String sqlQueryLogPass = "SELECT * FROM users WHERE login = '" + login + "' AND password = '" + password + "';";
        String sqlQueryNick = "SELECT * FROM users WHERE nick = '" + nick + "';";
        try {
            statement = connection.createStatement();
            rs = statement.executeQuery(sqlQueryLogPass);
            System.out.println(rs);
            if (rs.next()){
                return SAME_LOGINPASS;
            }
            rs = statement.executeQuery(sqlQueryNick);
            if (rs.next()){
                return SAME_NICK;
            }
            statement.executeUpdate(sqlRegister);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                if (rs!=null)rs.close();
                if (statement!=null)statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return REG_DATA_OK;
    }

}

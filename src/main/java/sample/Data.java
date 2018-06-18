package sample;

public interface Data {
    int PORT = 8189;
    String SERVER_URL = "localhost";
    String AUTH_DONE = "SuccessAuthorization";
    String AUTH_ASK = "/auth";
    String REG_ASK = "/register";
    String SAME_LOGINPASS = "/ununiqueloginpass";
    String SAME_NICK = "/nickisallreadyinuse";
    String REG_DATA_OK = "/acceptuserdata";
    String BUSY_BOX =  "/allreadyinuse";
    String DB_URL = "jdbc:mysql://localhost:3306/m_box_users" +
                        "?verifyServerCertificate=false&useSSL=true&useUnicode=true&" +
                        "useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    String DB_USER_NAME = "root";
    String DB_PASSWORD = "leo147_505SQD";

}

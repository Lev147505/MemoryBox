package sample;

public interface Data {
    int PORT = 8189;
    String SERVER_URL = "localhost";
    String AUTH_DONE = "SuccessAuthorization";
    String AUTH_ASK = "/auth";
    String REG_ASK = "/register";
    String EXIT_ASK = "/exit";
    String SAME_LOGINPASS = "/ununiqueloginpass";
    String SAME_NICK = "/nickisallreadyinuse";
    String REG_DATA_OK = "/acceptuserdata";
    String BUSY_BOX =  "/allreadyinuse";
    String INIT_OAL = "/oal";
    String UP_FILE = "/uploadfile";
    String REFRESH_LIST = "/freshlist";
    String SHOW_FILE = "/showfile";
    String DROP_FILE = "/deletefile";
    String DROP_OK = "/filewasdropped";
    String DB_URL = "jdbc:mysql://localhost:3306/m_box_users" +
                        "?verifyServerCertificate=false&useSSL=true&useUnicode=true&" +
                        "useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    String DB_USER_NAME = "root";
    String DB_PASSWORD = "leo147_505SQD";
    String GENERAL_DIR = "src/main/resources/users_file_boxes/";

}

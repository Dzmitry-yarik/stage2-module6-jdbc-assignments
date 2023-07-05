package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class CustomConnector {
    public static final String MYSQL_JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    
    public Connection getConnection(String url) {
        try {
            return DriverManager.getConnection(url);
        } catch ( SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public Connection getConnection(String url, String user, String password)  {
            Connection connection;
        try {
            Class.forName(MYSQL_JDBC_DRIVER);
/           connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
        return connection;
    }
}

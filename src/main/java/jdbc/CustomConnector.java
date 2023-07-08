package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class CustomConnector {
    private static CustomDataSource dataSource = CustomDataSource.getInstance();
    public static Connection connection = dataSource.getConnection();

    public static Connection getConnection(String url) {
        try {
            return DriverManager.getConnection(url);
        } catch ( SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public Connection getConnection(String url, String user, String password) {
        try {
              return  connection = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }
}

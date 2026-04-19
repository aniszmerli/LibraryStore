package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton database connection using XAMPP MySQL
 */
public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/library_store";
    private static final String USER = "root";
    private static final String PASSWORD = "";   // XAMPP default: empty password

    private static Connection connection = null;

    private DBConnection() {
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL Driver not found. Add mysql-connector-j to your project libraries.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database connection failed. Make sure XAMPP MySQL is running.");
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

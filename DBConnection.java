import java.sql.*;

public class DBConnection {

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "9898");
                 Statement stmt = conn.createStatement()) {
                
                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS budgetbuddy_swing");
                
                try (Connection dbConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/budgetbuddy_swing", "root", "9898");
                     Statement dbStmt = dbConn.createStatement()) {
                    
                    dbStmt.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY," +
                            "email VARCHAR(255) UNIQUE NOT NULL," +
                            "password VARCHAR(255) NOT NULL" +
                            ")");
                    
                    dbStmt.executeUpdate("CREATE TABLE IF NOT EXISTS expense (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY," +
                            "description VARCHAR(255) NOT NULL," +
                            "amount DOUBLE NOT NULL," +
                            "category VARCHAR(100) NOT NULL," +
                            "user_id INT NOT NULL," +
                            "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                            ")");

                    dbStmt.executeUpdate("CREATE TABLE IF NOT EXISTS income (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY," +
                            "source VARCHAR(255) NOT NULL," +
                            "amount DOUBLE NOT NULL," +
                            "user_id INT NOT NULL," +
                            "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                            ")");

                    dbStmt.executeUpdate("CREATE TABLE IF NOT EXISTS asset (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY," +
                            "name VARCHAR(255) NOT NULL," +
                            "value DOUBLE NOT NULL," +
                            "user_id INT NOT NULL," +
                            "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                            ")");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/budgetbuddy_swing", "root", "9898");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
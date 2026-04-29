import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            return DriverManager.getConnection(
                // CO5: JDBC - Establishing connection using DriverManager
                    "jdbc:mysql://localhost:3306/budgetbuddy",
                    "root",
                    "9898" // change if needed
            );

        } catch (Exception e) {
             // CO3: Exception Handling using try-catch
            e.printStackTrace();
            return null;
        }
    }
}
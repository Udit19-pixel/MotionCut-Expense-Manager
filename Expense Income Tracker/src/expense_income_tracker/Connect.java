package expense_income_tracker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
//This class sends input for database connection.
public class Connect
{
    private static Connection conn = null;
    private static void startConnection()
    {
        try
        {
            String url = "jdbc:mysql://localhost:3306/udit";
            conn = DriverManager.getConnection(url,"root","Skull2@Crusher");
            System.out.println("Connected!");
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }
    public static Connection connect()
    {
        if (conn == null)
        {
            startConnection();
        }
        return conn;
    }
}
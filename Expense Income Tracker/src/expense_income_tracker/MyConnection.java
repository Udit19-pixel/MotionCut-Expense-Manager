package expense_income_tracker;

import java.sql.*;
//This class tries to connect to the database and helps to perform INSERT and SELECT queries.
public class MyConnection
{
    static Statement stat;
    static
    {
        try
        {
            stat = Connect.connect().createStatement();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static ResultSet getData(String selectSQL)
    {
        try
        {
            return stat.executeQuery(selectSQL);
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
    public static void insertData(String sql1)
    {
        try
        {
            stat.executeUpdate(sql1);
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
    public static int totalExpenses()
    {
        try
        {
            ResultSet rs = MyConnection.getData("SELECT SUM(CAST(Amount AS DECIMAL(10,2))) FROM Expense");
            if (rs.next())
            {
                return rs.getInt(1);
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        return 0;
    }
}
package expense_income_tracker;

public class Expense_Income_Tracker
{
    public static void main(String[] args)
    {
        //main() function of the file.
        new ExpensesIncomesTracker().setLocationRelativeTo(null);
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
        {
            try
            {
                if (Connect.connect() != null && !Connect.connect().isClosed())
                {
                    Connect.connect().close();
                    System.out.println("closed!!");
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }));
    }   
}

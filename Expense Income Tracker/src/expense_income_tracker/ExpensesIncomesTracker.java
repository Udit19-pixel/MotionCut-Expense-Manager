package expense_income_tracker;

import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

// The ExpensesIncomesTracker class extends JFrame to create the main application window.
public class ExpensesIncomesTracker extends JFrame
{
    private final DefaultTableModel tableModel;
    private final JTextField dateField;
    private final JTextField descriptionField;
    private final JTextField amountField;
    private final JComboBox<String> typeCombobox;
    private final JLabel balanceLabel;
    private double balance; // The current balance based on the added expenses and incomes.
    private Statement st;

    public ExpensesIncomesTracker()
    {
        try
        {
            // Apply the FlatDarkLaf look and feel for a modern and flat appearance.
            UIManager.setLookAndFeel(new FlatDarkLaf());
        }
        catch (Exception ex)
        {
            System.err.println("Failed to Set FlatDarkLaf LookAndFeel");
        }
        // Custom color schemes for specific Swing components.
        UIManager.put("TextField.foreground", Color.WHITE);
        UIManager.put("TextField.background", Color.DARK_GRAY);
        UIManager.put("TextField.caretForeground", Color.RED);
        UIManager.put("ComboBox.foreground", Color.YELLOW);
        UIManager.put("ComboBox.selectionForeground", Color.WHITE);
        UIManager.put("ComboBox.selectionBackground", Color.BLACK);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.background", Color.ORANGE);
        UIManager.put("Label.foreground", Color.WHITE);

        // Set the default font for the entire application
        Font customFont = new Font("Calibre", Font.PLAIN, 18);
        UIManager.put("Label.font", customFont);
        UIManager.put("TextField.font", customFont);
        UIManager.put("ComboBox.font", customFont);
        UIManager.put("Button.font", customFont);

        // Initialize the table model and balance variable.
        balance = 0.0;
        tableModel = new DefaultTableModel();

        // Create a JTable and set up a scroll pane to display the data.
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);

        // Create input fields and components for adding new entries.
        dateField = new JTextField(10);
        descriptionField = new JTextField(20);
        amountField = new JTextField(10);
        typeCombobox = new JComboBox<>(new String[]{"Groceries", "Transportation", "Entertainment", "Income"});

        // Attach an ActionListener to the "Add" button to handle new entry addition.
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> addEntry());
        balanceLabel = new JLabel("Balance: $" + balance);

        // Create input panel to arrange input components.
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Date"));
        inputPanel.add(dateField);

        inputPanel.add(new JLabel("Description"));
        inputPanel.add(descriptionField);

        inputPanel.add(new JLabel("Amount"));
        inputPanel.add(amountField);
        inputPanel.add(new JLabel("Type"));
        inputPanel.add(typeCombobox);

        inputPanel.add(addButton);

        // Create bottom panel to display the balance.
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(balanceLabel);
        setLayout(new BorderLayout());

        // Set the layout of the main frame and add components to appropriate positions.
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Set the title, default close operation, and visibility of the main frame.
        setTitle("Expanses And Incomes Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);

        //initialise the connection and populate the table
        new MyConnection();
        setTable();
    }

    // Method to handle adding new entries to the table.
    private void addEntry()
    {
        // Get input values from input fields.
        String date = dateField.getText();
        String description = descriptionField.getText();
        String amountStr = amountField.getText();
        String type = (String) typeCombobox.getSelectedItem();
        double amount;

        // Validate input values.
        if (amountStr.isEmpty())
        {
            JOptionPane.showMessageDialog(this, "Enter the Amount", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try
        {
            amount = Double.parseDouble(amountStr);
        }
        catch (NumberFormatException ex)
        {
            JOptionPane.showMessageDialog(this, "Invalid Amount Format", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Convert expenses to negative values.
        assert type != null;
        if (type.equals("Groceries") || type.equals("Transportation") || type.equals("Entertainment"))
        {
            amount *= -1;
        }

        // Create a new entry and add it to the table.
        ExpenseIncomeEntry entry = new ExpenseIncomeEntry(date, description, amount, type);

        tableModel.addColumn(entry.getDate());
        tableModel.addColumn(entry.getDescription());
        tableModel.addColumn(entry.getAmount());
        tableModel.addColumn(entry.getType());

        JOptionPane.showMessageDialog
                (
                        this,
                        "Summary:-\nDate: " + date + "\nDescription: " + description + "\nAmount: " + amountStr + "\nType: " + type,
                        "Expense/Income Summary",
                        JOptionPane.INFORMATION_MESSAGE
                );

        // Clear input fields for the next entry.
        clearInputFields();
        insertDB(date, description, amountStr, type);

    }

    // Method to clear input fields.
    private void clearInputFields()
    {
        dateField.setText("");
        descriptionField.setText("");
        amountField.setText("");
        typeCombobox.setSelectedIndex(0);
    }

    private void insertDB(String date, String description, String amountStr, String type)
    {
        String sql1 = "INSERT INTO Expense VALUES('" + date + "','" + description + "','" + amountStr + "','" + type + "')";
        System.out.println(st);
        MyConnection.insertData(sql1);
        setTable();

        // Update the balance and display the new balance.
        balance = MyConnection.totalExpenses();
        balanceLabel.setText("Balance: $" + balance);
    }

    public void setTable()
    {
        String sql2 = "SELECT * FROM Expense";
        try
        {
            ResultSet rs = MyConnection.getData(sql2);
            // Constructor to initialize the application and set up the form.
            ResultSetMetaData rsmd = rs.getMetaData();
            tableModel.setRowCount(0);
            int cols = rsmd.getColumnCount();
            String[] colName = new String[cols];
            for (int i = 0; i < cols; i++)
            {
                colName[i] = rsmd.getColumnName(i + 1);
            }
            tableModel.setColumnIdentifiers(colName);
            String NewDate, NewDescription, NewAmount, NewType;
            while (rs.next())
            {
                NewDate = rs.getString(1);
                NewDescription = rs.getString(2);
                NewAmount = rs.getString(3);
                NewType = rs.getString(4);
                String[] row = {NewDate, NewDescription, NewAmount, NewType};
                tableModel.addRow(row);
            }
            // Update the balance and display the new balance.
            balance = MyConnection.totalExpenses();
            balanceLabel.setText("Balance: $" + balance);
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
}
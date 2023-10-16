package expense_income_tracker;

import java.util.List;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
public class ExpenseIncomeTableModel extends AbstractTableModel
{
    // List to store the entries (rows) in the table
    private final List<ExpenseIncomeEntry> entries;

    // Column names for the table
    private final String[] columnNames = {"Date","Description","Amount","Type"};

    //Constructor to initialize the table model.
    public ExpenseIncomeTableModel()
    {
        entries = new ArrayList<>();
    }

    //Add a new entry to the table.
    //The expense or income entry to add.
    public void addEntry(ExpenseIncomeEntry entry)
    {
        entries.add(entry);
        // Notify the table that a new row has been inserted
        fireTableRowsInserted(entries.size()-1, entries.size()-1);
    }

    @Override
    public int getRowCount() { return entries.size(); }

    @Override
    public int getColumnCount() { return columnNames.length;}

    @Override
    public String getColumnName(int column) { return columnNames[column]; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        ExpenseIncomeEntry entry = entries.get(rowIndex);
        // Return the value for the cell based on the column index
        return switch (columnIndex) {
            case 0 -> entry.getDate();
            case 1 -> entry.getDescription();
            case 2 -> entry.getAmount();
            case 3 -> entry.getType();
            default -> null;
        };
    }
}
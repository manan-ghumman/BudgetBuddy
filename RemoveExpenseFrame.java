import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class RemoveExpenseFrame extends JPanel {

    int userId;
    Runnable onComplete;
    JTable expenseTable;
    DefaultTableModel tableModel;
    JComboBox<ExpenseItem> expenseComboBox;

    static class ExpenseItem {
        int id;
        String description;
        double amount;
        String category;

        public ExpenseItem(int id, String description, double amount, String category) {
            this.id = id;
            this.description = description;
            this.amount = amount;
            this.category = category;
        }

        @Override
        public String toString() {
            return description + " (₹" + amount + ") [" + category + "]";
        }
    }

    public RemoveExpenseFrame(int userId, Runnable onComplete) {
        this.userId = userId;
        this.onComplete = onComplete;

        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Remove Expense", SwingConstants.CENTER);
        titleLabel.setFont(new Font(null, Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Table definition
        tableModel = new DefaultTableModel(new Object[]{"ID", "Description", "Amount", "Category"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // read-only table
            }
        };
        expenseTable = new JTable(tableModel);
        
        // Hide the ID column
        expenseTable.getColumnModel().getColumn(0).setMinWidth(0);
        expenseTable.getColumnModel().getColumn(0).setMaxWidth(0);
        expenseTable.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(expenseTable);
        add(scrollPane, BorderLayout.CENTER);

        // Combo box & Buttons Panel
        JPanel controlPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        controlPanel.add(new JLabel("Select Expense to Remove:"), gbc);

        gbc.gridx = 1;
        expenseComboBox = new JComboBox<>();
        controlPanel.add(expenseComboBox, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton removeBtn = new JButton("Remove");
        JButton cancelBtn = new JButton("Cancel");

        buttonPanel.add(removeBtn);
        buttonPanel.add(cancelBtn);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        controlPanel.add(buttonPanel, gbc);

        add(controlPanel, BorderLayout.SOUTH);

        removeBtn.addActionListener(e -> removeExpense());
        cancelBtn.addActionListener(e -> {
            if (onComplete != null)
                onComplete.run();
        });

        loadData();
    }

    public void loadData() {
        tableModel.setRowCount(0);
        expenseComboBox.removeAllItems();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT id, description, amount, category FROM expense WHERE user_id = ?")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String description = rs.getString("description");
                    double amount = rs.getDouble("amount");
                    String category = rs.getString("category");
                    tableModel.addRow(new Object[]{id, description, amount, category});
                    expenseComboBox.addItem(new ExpenseItem(id, description, amount, category));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading expense: " + e.getMessage());
        }
    }

    void removeExpense() {
        ExpenseItem selectedExpense = (ExpenseItem) expenseComboBox.getSelectedItem();
        if (selectedExpense == null) {
            JOptionPane.showMessageDialog(this, "Please select an expense to remove.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to remove the expense '" + selectedExpense.description + "'?", 
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement("DELETE FROM expense WHERE id = ? AND user_id = ?")) {
                ps.setInt(1, selectedExpense.id);
                ps.setInt(2, userId);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Expense removed successfully!");
                loadData();

                if (onComplete != null)
                    onComplete.run();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error removing expense: " + e.getMessage());
            }
        }
    }
}

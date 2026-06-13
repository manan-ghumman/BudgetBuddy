import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class RemoveIncomeFrame extends JPanel {

    int userId;
    Runnable onComplete;
    JTable incomeTable;
    DefaultTableModel tableModel;
    JComboBox<IncomeItem> incomeComboBox;

    static class IncomeItem {
        int id;
        String source;
        double amount;

        public IncomeItem(int id, String source, double amount) {
            this.id = id;
            this.source = source;
            this.amount = amount;
        }

        @Override
        public String toString() {
            return source + " (₹" + amount + ")";
        }
    }

    public RemoveIncomeFrame(int userId, Runnable onComplete) {
        this.userId = userId;
        this.onComplete = onComplete;

        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Remove Income", SwingConstants.CENTER);
        titleLabel.setFont(new Font(null, Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Table definition
        tableModel = new DefaultTableModel(new Object[]{"ID", "Income Source", "Amount"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // read-only table
            }
        };
        incomeTable = new JTable(tableModel);
        
        // Hide the ID column
        incomeTable.getColumnModel().getColumn(0).setMinWidth(0);
        incomeTable.getColumnModel().getColumn(0).setMaxWidth(0);
        incomeTable.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(incomeTable);
        add(scrollPane, BorderLayout.CENTER);

        // Combo box & Buttons Panel
        JPanel controlPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        controlPanel.add(new JLabel("Select Income to Remove:"), gbc);

        gbc.gridx = 1;
        incomeComboBox = new JComboBox<>();
        controlPanel.add(incomeComboBox, gbc);

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

        removeBtn.addActionListener(e -> removeIncome());
        cancelBtn.addActionListener(e -> {
            if (onComplete != null)
                onComplete.run();
        });

        loadData();
    }

    public void loadData() {
        tableModel.setRowCount(0);
        incomeComboBox.removeAllItems();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT id, source, amount FROM income WHERE user_id = ?")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String source = rs.getString("source");
                    double amount = rs.getDouble("amount");
                    tableModel.addRow(new Object[]{id, source, amount});
                    incomeComboBox.addItem(new IncomeItem(id, source, amount));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading income: " + e.getMessage());
        }
    }

    void removeIncome() {
        IncomeItem selectedIncome = (IncomeItem) incomeComboBox.getSelectedItem();
        if (selectedIncome == null) {
            JOptionPane.showMessageDialog(this, "Please select an income record to remove.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to remove the income from '" + selectedIncome.source + "'?", 
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement("DELETE FROM income WHERE id = ? AND user_id = ?")) {
                ps.setInt(1, selectedIncome.id);
                ps.setInt(2, userId);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Income removed successfully!");
                loadData();

                if (onComplete != null)
                    onComplete.run();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error removing income: " + e.getMessage());
            }
        }
    }
}

import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class AddExpenseFrame extends JPanel {

    int userId;
    Runnable onComplete;

    JTextField desc = new JTextField(20);
    JTextField amount = new JTextField(20);
    JComboBox<String> categoryBox = new JComboBox<>(new String[] {
            "Food", "Transport", "Rent", "Shopping", "Entertainment", "Utilities", "Healthcare", "Others"
    });

    public AddExpenseFrame(int userId, Runnable onComplete) {
        this.userId = userId;
        this.onComplete = onComplete;

        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Add New Expense");
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(0, 2));

        formPanel.add(new JLabel("Description:"));
        formPanel.add(desc);

        formPanel.add(new JLabel("Amount:"));
        formPanel.add(amount);

        formPanel.add(new JLabel("Category:"));
        formPanel.add(categoryBox);

        JButton addBtn = new JButton("Add Expense");
        JButton cancelBtn = new JButton("Cancel");

        formPanel.add(addBtn);
        formPanel.add(cancelBtn);

        add(formPanel, BorderLayout.CENTER);

        addBtn.addActionListener(e -> addExpense());
        cancelBtn.addActionListener(e -> {
            if (onComplete != null)
                onComplete.run();
        });
    }

    JLabel createLabel(String text) {
        return new JLabel(text);
    }

    void addExpense() {
        if (desc.getText().isEmpty() || amount.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields");
            return;
        }
        try {
            Connection con = DBConnection.getConnection();
            String query = "INSERT INTO expense(description, amount, category, user_id) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, desc.getText());
            ps.setDouble(2, Double.parseDouble(amount.getText()));
            ps.setString(3, categoryBox.getSelectedItem().toString());
            ps.setInt(4, userId);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Expense Added Successfully!");

            // Clear fields
            desc.setText("");
            amount.setText("");
            categoryBox.setSelectedIndex(0);

            if (onComplete != null)
                onComplete.run();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}
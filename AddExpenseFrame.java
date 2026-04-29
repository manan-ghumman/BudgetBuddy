import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class AddExpenseFrame extends JPanel { // Extends JPanel now

    int userId;
    Runnable onComplete;

    JTextField desc = new JTextField(20);
    JTextField amount = new JTextField(20); 
    JComboBox<String> categoryBox = new JComboBox<>(new String[]{
        "Food", "Transport", "Rent", "Shopping", "Entertainment", "Utilities", "Healthcare", "Others"
    });

    public AddExpenseFrame(int userId, Runnable onComplete) {
        this.userId = userId;
        this.onComplete = onComplete;

        setLayout(new GridBagLayout());
        setBackground(new Color(245, 247, 250));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel titleLabel = new JLabel("Add New Expense");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(30, 41, 59));
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        add(createLabel("Description:"), gbc);
        gbc.gridx = 1;
        add(desc, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(createLabel("Amount:"), gbc);
        gbc.gridx = 1;
        add(amount, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(createLabel("Category:"), gbc);
        gbc.gridx = 1;
        categoryBox.setPreferredSize(new Dimension(225, 30));
        add(categoryBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        JButton addBtn = new JButton("Add Expense");
        styleButton(addBtn);
        add(addBtn, gbc);

        gbc.gridx = 1;
        JButton cancelBtn = new JButton("Cancel");
        styleButton(cancelBtn);
        cancelBtn.setBackground(new Color(156, 163, 175)); // Gray
        add(cancelBtn, gbc);

        addBtn.addActionListener(e -> addExpense());
        cancelBtn.addActionListener(e -> {
            if (onComplete != null) onComplete.run();
        });
    }

    JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        return label;
    }

    void styleButton(JButton btn) {
        btn.setBackground(new Color(59, 130, 246));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(200, 40));
        btn.setFont(new Font("Arial", Font.BOLD, 14));
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
            
            if (onComplete != null) onComplete.run();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}
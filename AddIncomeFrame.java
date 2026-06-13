import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class AddIncomeFrame extends JPanel { // Extends JPanel now

    int userId;
    Runnable onComplete;

    JTextField source = new JTextField(20);
    JTextField amount = new JTextField(20);

    public AddIncomeFrame(int userId, Runnable onComplete) {
        this.userId = userId;
        this.onComplete = onComplete;

        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Add New Income");
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(0, 2));

        formPanel.add(new JLabel("Source:"));
        formPanel.add(source);

        formPanel.add(new JLabel("Amount:"));
        formPanel.add(amount);

        JButton addBtn = new JButton("Add Income");
        JButton cancelBtn = new JButton("Cancel");

        formPanel.add(addBtn);
        formPanel.add(cancelBtn);

        add(formPanel, BorderLayout.CENTER);

        addBtn.addActionListener(e -> addIncome());
        cancelBtn.addActionListener(e -> {
            if (onComplete != null)
                onComplete.run();
        });
    }

    JLabel createLabel(String text) {
        return new JLabel(text);
    }

    void styleButton(JButton btn) {
        // No custom styling
    }

    void addIncome() {
        if (source.getText().isEmpty() || amount.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields");
            return;
        }
        try {
            Connection con = DBConnection.getConnection();
            String query = "INSERT INTO income(source, amount, user_id) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, source.getText());
            ps.setDouble(2, Double.parseDouble(amount.getText()));
            ps.setInt(3, userId);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Income Added Successfully!");

            // Clear fields
            source.setText("");
            amount.setText("");

            if (onComplete != null)
                onComplete.run();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}
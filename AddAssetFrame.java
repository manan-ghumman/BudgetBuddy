import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class AddAssetFrame extends JPanel {

    int userId;
    Runnable onComplete;

    JTextField nameField = new JTextField(20);
    JTextField valueField = new JTextField(20);

    public AddAssetFrame(int userId, Runnable onComplete) {
        this.userId = userId;
        this.onComplete = onComplete;

        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Add New Asset");
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(0, 2));

        formPanel.add(new JLabel("Asset Name:"));
        formPanel.add(nameField);

        formPanel.add(new JLabel("Value:"));
        formPanel.add(valueField);

        JButton addBtn = new JButton("Add Asset");
        JButton cancelBtn = new JButton("Cancel");

        formPanel.add(addBtn);
        formPanel.add(cancelBtn);

        add(formPanel, BorderLayout.CENTER);

        addBtn.addActionListener(e -> addAsset());
        cancelBtn.addActionListener(e -> {
            if (onComplete != null)
                onComplete.run();
        });
    }

    JLabel createLabel(String text) {
        return new JLabel(text);
    }

    void addAsset() {
        if (nameField.getText().isEmpty() || valueField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields");
            return;
        }
        try {
            Connection con = DBConnection.getConnection();
            String query = "INSERT INTO asset(name, value, user_id) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, nameField.getText());
            ps.setDouble(2, Double.parseDouble(valueField.getText()));
            ps.setInt(3, userId);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Asset Added Successfully!");
            nameField.setText("");
            valueField.setText("");

            if (onComplete != null)
                onComplete.run();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}

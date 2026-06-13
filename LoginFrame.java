import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginFrame extends JFrame {

    JTextField emailField = new JTextField(20);
    JPasswordField passwordField = new JPasswordField(20);

    public LoginFrame() {
        setTitle("BudgetBuddy - Login");
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // MAIN PANEL with basic layout
        JPanel mainPanel = new JPanel(new GridLayout(0, 1));

        // LOGO / TITLE Section
        JLabel titleLabel = new JLabel("BudgetBuddy");
        mainPanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Manage your wealth efficiently");
        mainPanel.add(subtitleLabel);

        // INPUT FIELDS
        mainPanel.add(new JLabel("Email Address:"));
        mainPanel.add(emailField);

        mainPanel.add(new JLabel("Password:"));
        mainPanel.add(passwordField);

        // BUTTONS
        JButton loginBtn = new JButton("Login");
        mainPanel.add(loginBtn);

        JButton signupBtn = new JButton("Create an Account");
        mainPanel.add(signupBtn);

        // ACTIONS
        loginBtn.addActionListener(e -> login());
        signupBtn.addActionListener(e -> signup());

        add(mainPanel);
        setVisible(true);
    }

    void login() {
        try {
            Connection con = DBConnection.getConnection();
            String query = "SELECT * FROM users WHERE email=? AND password=?";
            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, emailField.getText());
            ps.setString(2, new String(passwordField.getPassword()));

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                new DashboardFrame(rs.getInt("id"));
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Login Credentials", "Login Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection error", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    void signup() {
        if (emailField.getText().isEmpty() || new String(passwordField.getPassword()).isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter email and password", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Connection con = DBConnection.getConnection();
            String query = "INSERT INTO users(email, password) VALUES (?, ?)";
            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, emailField.getText());
            ps.setString(2, new String(passwordField.getPassword()));

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Account Created Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error during signup. Email might already exist.", "Signup Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
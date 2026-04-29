import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginFrame extends JFrame {

    JTextField emailField = new JTextField(20);
    JPasswordField passwordField = new JPasswordField(20);

    public LoginFrame() {
        setTitle("BudgetBuddy - Login");
        setSize(450, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // MAIN PANEL with dark theme
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(30, 41, 59)); // Slate 800

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 30, 10, 30);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // LOGO / TITLE Section
        JLabel titleLabel = new JLabel("BudgetBuddy", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 30, 5, 30);
        mainPanel.add(titleLabel, gbc);

        JLabel subtitleLabel = new JLabel("Manage your wealth efficiently", JLabel.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(148, 163, 184)); // Slate 400
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 30, 40, 30);
        mainPanel.add(subtitleLabel, gbc);

        // INPUT FIELDS
        gbc.insets = new Insets(10, 30, 5, 30);
        gbc.gridy = 2;
        mainPanel.add(createLabel("Email Address"), gbc);
        
        gbc.gridy = 3;
        styleTextField(emailField);
        mainPanel.add(emailField, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(20, 30, 5, 30);
        mainPanel.add(createLabel("Password"), gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(5, 30, 10, 30);
        styleTextField(passwordField);
        mainPanel.add(passwordField, gbc);

        // BUTTONS
        gbc.gridy = 6;
        gbc.insets = new Insets(30, 30, 10, 30);
        JButton loginBtn = new JButton("Login");
        stylePrimaryButton(loginBtn);
        mainPanel.add(loginBtn, gbc);

        gbc.gridy = 7;
        gbc.insets = new Insets(10, 30, 30, 30);
        JButton signupBtn = new JButton("Create an Account");
        styleSecondaryButton(signupBtn);
        mainPanel.add(signupBtn, gbc);

        // ACTIONS
        loginBtn.addActionListener(e -> login());
        signupBtn.addActionListener(e -> signup());

        add(mainPanel);
        setVisible(true);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(203, 213, 225)); // Slate 300
        label.setFont(new Font("Arial", Font.BOLD, 12));
        return label;
    }

    private void styleTextField(JTextField field) {
        field.setBackground(new Color(51, 65, 85)); // Slate 700
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(71, 85, 105), 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        field.setFont(new Font("Arial", Font.PLAIN, 14));
    }

    private void stylePrimaryButton(JButton btn) {
        btn.setBackground(new Color(59, 130, 246)); // Blue 500
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(0, 45));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(37, 99, 235)); // Blue 600
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(59, 130, 246));
            }
        });
    }

    private void styleSecondaryButton(JButton btn) {
        btn.setBackground(new Color(30, 41, 59));
        btn.setForeground(new Color(96, 165, 250)); // Blue 400
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(96, 165, 250), 1));
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(0, 45));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(51, 65, 85));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(30, 41, 59));
            }
        });
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
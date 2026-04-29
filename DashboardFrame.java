import java.awt.*;
import javax.swing.*;

public class DashboardFrame extends JFrame {

    int userId;
    JLabel incomeLabel, expenseLabel, balanceLabel, assetsLabel, warningLabel;
    JProgressBar budgetBar;
    PieChartPanel chart;
    
    JPanel contentArea;
    CardLayout cardLayout;

    public DashboardFrame(int userId) {
        this.userId = userId;

        setTitle("BudgetBuddy");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // MAIN PANEL
        JPanel mainPanel = new JPanel(new BorderLayout());

        // ================= SIDEBAR =================
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(new Color(30, 41, 59));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("BudgetBuddy");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(35, 25, 35, 25));

        JButton dashboardBtn = createSidebarButton("Dashboard");
        JButton expenseBtn = createSidebarButton("Add Expense");
        JButton incomeBtn = createSidebarButton("Add Income");
        JButton assetBtn = createSidebarButton("Add Assets");

        sidebar.add(title);
        sidebar.add(dashboardBtn);
        sidebar.add(expenseBtn);
        sidebar.add(incomeBtn);
        sidebar.add(assetBtn);

        // ================= CONTENT AREA (CARD LAYOUT) =================
        cardLayout = new CardLayout();
        contentArea = new JPanel(cardLayout);

        // 1. DASHBOARD SCREEN
        JPanel dashboardScreen = createDashboardScreen();
        
        // 2. EXPENSE SCREEN
        AddExpenseFrame expensePanel = new AddExpenseFrame(userId, () -> {
            refreshData();
            cardLayout.show(contentArea, "Dashboard");
        });

        // 3. INCOME SCREEN
        AddIncomeFrame incomePanel = new AddIncomeFrame(userId, () -> {
            refreshData();
            cardLayout.show(contentArea, "Dashboard");
        });

        // 4. ASSETS SCREEN
        AddAssetFrame assetPanel = new AddAssetFrame(userId, () -> {
            refreshData();
            cardLayout.show(contentArea, "Dashboard");
        });

        contentArea.add(dashboardScreen, "Dashboard");
        contentArea.add(expensePanel, "Expense");
        contentArea.add(incomePanel, "Income");
        contentArea.add(assetPanel, "Assets");

        // ADD TO MAIN
        mainPanel.add(sidebar, BorderLayout.WEST);
        mainPanel.add(contentArea, BorderLayout.CENTER);

        add(mainPanel);

        // ACTIONS
        dashboardBtn.addActionListener(e -> cardLayout.show(contentArea, "Dashboard"));
        expenseBtn.addActionListener(e -> cardLayout.show(contentArea, "Expense"));
        incomeBtn.addActionListener(e -> cardLayout.show(contentArea, "Income"));
        assetBtn.addActionListener(e -> cardLayout.show(contentArea, "Assets"));

        refreshData();
        new Timer(5000, e -> refreshData()).start();

        setVisible(true);
    }

    private JPanel createDashboardScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 247, 250));

        // HEADER
        JLabel header = new JLabel("Financial Dashboard", JLabel.LEFT);
        header.setFont(new Font("Arial", Font.BOLD, 28));
        header.setForeground(new Color(30, 41, 59));
        header.setBorder(BorderFactory.createEmptyBorder(25, 25, 10, 25));

        // SCROLLABLE CONTENT
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(new Color(245, 247, 250));

        // CARDS PANEL (2x2 Grid)
        JPanel cardsContainer = new JPanel(new GridLayout(2, 2, 20, 20));
        cardsContainer.setBackground(new Color(245, 247, 250));
        cardsContainer.setBorder(BorderFactory.createEmptyBorder(10, 25, 20, 25));
        cardsContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));

        incomeLabel = createCard("Total Income", new Color(34, 197, 94));
        expenseLabel = createCard("Total Expense", new Color(239, 68, 68));
        balanceLabel = createCard("Current Balance", new Color(59, 130, 246));
        assetsLabel = createCard("Total Assets", new Color(168, 85, 247)); // Purple

        cardsContainer.add(incomeLabel);
        cardsContainer.add(expenseLabel);
        cardsContainer.add(balanceLabel);
        cardsContainer.add(assetsLabel);

        // PROGRESS PANEL
        JPanel progressPanel = new JPanel(new BorderLayout(0, 5));
        progressPanel.setBackground(new Color(245, 247, 250));
        progressPanel.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        progressPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        JLabel progressTitle = new JLabel("Monthly Budget Usage");
        progressTitle.setFont(new Font("Arial", Font.BOLD, 14));
        
        budgetBar = new JProgressBar(0, 100);
        budgetBar.setStringPainted(true);
        budgetBar.setPreferredSize(new Dimension(0, 20));

        warningLabel = new JLabel("Checking status...", JLabel.LEFT);
        warningLabel.setFont(new Font("Arial", Font.ITALIC, 12));

        progressPanel.add(progressTitle, BorderLayout.NORTH);
        progressPanel.add(budgetBar, BorderLayout.CENTER);
        progressPanel.add(warningLabel, BorderLayout.SOUTH);

        // CHARTS PANEL
        JPanel chartsContainer = new JPanel(new GridLayout(1, 3, 20, 0));
        chartsContainer.setBackground(new Color(245, 247, 250));
        chartsContainer.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        chartsContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

        JPanel piePanel = createChartWrapper(chart = new PieChartPanel(userId, "expense"));
        JPanel barPanel = createChartWrapper(barChart = new BarChartPanel(userId));
        JPanel assetPiePanel = createChartWrapper(assetChart = new PieChartPanel(userId, "asset"));

        chartsContainer.add(piePanel);
        chartsContainer.add(barPanel);
        chartsContainer.add(assetPiePanel);

        // QUICK ACTIONS
        JPanel quickActions = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        quickActions.setBackground(new Color(245, 247, 250));
        JButton addEx = new JButton("Add Expense");
        JButton addIn = new JButton("Add Income");
        JButton addAs = new JButton("Add Asset");
        styleQuickButton(addEx);
        styleQuickButton(addIn);
        styleQuickButton(addAs);
        
        addEx.addActionListener(e -> cardLayout.show(contentArea, "Expense"));
        addIn.addActionListener(e -> cardLayout.show(contentArea, "Income"));
        addAs.addActionListener(e -> cardLayout.show(contentArea, "Assets"));
        
        quickActions.add(addEx);
        quickActions.add(addIn);
        quickActions.add(addAs);

        // ASSEMBLE MAIN CONTENT
        mainContent.add(cardsContainer);
        mainContent.add(progressPanel);
        mainContent.add(chartsContainer);
        mainContent.add(quickActions);

        panel.add(header, BorderLayout.NORTH);
        panel.add(mainContent, BorderLayout.CENTER);

        return panel;
    }

    JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(220, 50));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(30, 41, 59));
        btn.setForeground(new Color(203, 213, 225));
        btn.setFont(new Font("Arial", Font.PLAIN, 15));
        btn.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 10));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(51, 65, 85));
                btn.setForeground(Color.WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(30, 41, 59));
                btn.setForeground(new Color(203, 213, 225));
            }
        });
        return btn;
    }

    JLabel createCard(String title, Color color) {
        JLabel label = new JLabel("<html><center>" + title + "<br><font size='+1'>₹0</font></center></html>", JLabel.CENTER);
        label.setOpaque(true);
        label.setBackground(color);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 15));
        label.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        return label;
    }

    void styleQuickButton(JButton btn) {
        btn.setBackground(new Color(59, 130, 246));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(140, 40));
        btn.setFont(new Font("Arial", Font.BOLD, 12));
    }

    BarChartPanel barChart;
    PieChartPanel assetChart;

    private JPanel createChartWrapper(JPanel chart) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        chart.setPreferredSize(new Dimension(300, 220));
        wrapper.add(chart, BorderLayout.CENTER);
        return wrapper;
    }

    void refreshData() {
        double income = getTotal("income");
        double expense = getTotal("expense");
        double assets = getTotal("asset");
        double balance = income - expense;

        incomeLabel.setText("<html><center>Total Income<br><font size='+1'>₹" + income + "</font></center></html>");
        expenseLabel.setText("<html><center>Total Expense<br><font size='+1'>₹" + expense + "</font></center></html>");
        balanceLabel.setText("<html><center>Current Balance<br><font size='+1'>₹" + balance + "</font></center></html>");
        assetsLabel.setText("<html><center>Total Assets<br><font size='+1'>₹" + assets + "</font></center></html>");

        int percent = (income == 0) ? 0 : (int)((expense / income) * 100);
        budgetBar.setValue(Math.min(percent, 100));
        budgetBar.setString(percent + "% Used");

        if (percent > 80) {
            warningLabel.setText("⚠ Budget Limit Near!");
            warningLabel.setForeground(new Color(220, 38, 38));
        } else {
            warningLabel.setText("✔ Safe Spending.");
            warningLabel.setForeground(new Color(22, 163, 74));
        }

        if (chart != null) {
            chart.loadData();
            chart.repaint();
        }
        if (barChart != null) {
            barChart.loadData();
            barChart.repaint();
        }
        if (assetChart != null) {
            assetChart.loadData();
            assetChart.repaint();
        }
    }

    double getTotal(String table) {
        double total = 0;
        try {
            var con = DBConnection.getConnection();
            var ps = con.prepareStatement("SELECT SUM(amount) FROM " + table + " WHERE user_id=?");
            if (table.equals("asset")) {
                ps = con.prepareStatement("SELECT SUM(value) FROM asset WHERE user_id=?");
            }
            ps.setInt(1, userId);
            var rs = ps.executeQuery();
            if (rs.next()) total = rs.getDouble(1);
        } catch (Exception e) {
            // Silently fail if table doesn't exist yet
        }
        return total;
    }
}
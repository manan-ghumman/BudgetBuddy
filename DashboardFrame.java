import java.awt.*;
import javax.swing.*;

public class DashboardFrame extends JFrame {

    int userId;
    JLabel incomeLabel, expenseLabel, balanceLabel, assetsLabel, warningLabel;
    JProgressBar budgetBar;

    PieChartPanel chart;
    BarChartPanel barChart;
    PieChartPanel assetChart;

    JPanel contentArea;
    CardLayout cardLayout;

    public DashboardFrame(int userId) {
        this.userId = userId;

        setTitle("BudgetBuddy");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        // Sidebar
        JPanel sidebar = new JPanel(new GridLayout(0, 1, 5, 5));
        JLabel title = new JLabel("BudgetBuddy", SwingConstants.CENTER);
        title.setFont(new Font(null, Font.BOLD, 16));
        sidebar.add(title);

        JButton dashboardBtn = new JButton("Dashboard");
        JButton expenseBtn = new JButton("Add Expense");
        JButton incomeBtn = new JButton("Add Income");
        JButton assetBtn = new JButton("Add Assets");

        JButton removeAssetBtn = new JButton("Remove Asset");
        JButton removeIncomeBtn = new JButton("Remove Income");
        JButton removeExpenseBtn = new JButton("Remove Expense");

        sidebar.add(dashboardBtn);
        sidebar.add(expenseBtn);
        sidebar.add(incomeBtn);
        sidebar.add(assetBtn);
        sidebar.add(removeAssetBtn);
        sidebar.add(removeIncomeBtn);
        sidebar.add(removeExpenseBtn);

        // Content Area
        cardLayout = new CardLayout();
        contentArea = new JPanel(cardLayout);

        JPanel dashboardScreen = createDashboardScreen();
        AddExpenseFrame expensePanel = new AddExpenseFrame(userId, () -> {
            refreshData();
            cardLayout.show(contentArea, "Dashboard");
        });
        AddIncomeFrame incomePanel = new AddIncomeFrame(userId, () -> {
            refreshData();
            cardLayout.show(contentArea, "Dashboard");
        });
        AddAssetFrame assetPanel = new AddAssetFrame(userId, () -> {
            refreshData();
            cardLayout.show(contentArea, "Dashboard");
        });
        RemoveAssetFrame removeAssetPanel = new RemoveAssetFrame(userId, () -> {
            refreshData();
            cardLayout.show(contentArea, "Dashboard");
        });
        RemoveIncomeFrame removeIncomePanel = new RemoveIncomeFrame(userId, () -> {
            refreshData();
            cardLayout.show(contentArea, "Dashboard");
        });
        RemoveExpenseFrame removeExpensePanel = new RemoveExpenseFrame(userId, () -> {
            refreshData();
            cardLayout.show(contentArea, "Dashboard");
        });

        contentArea.add(dashboardScreen, "Dashboard");
        contentArea.add(expensePanel, "Expense");
        contentArea.add(incomePanel, "Income");
        contentArea.add(assetPanel, "Assets");
        contentArea.add(removeAssetPanel, "RemoveAsset");
        contentArea.add(removeIncomePanel, "RemoveIncome");
        contentArea.add(removeExpensePanel, "RemoveExpense");

        mainPanel.add(sidebar, BorderLayout.WEST);
        mainPanel.add(contentArea, BorderLayout.CENTER);
        add(mainPanel);

        dashboardBtn.addActionListener(e -> {
            refreshData();
            cardLayout.show(contentArea, "Dashboard");
        });
        expenseBtn.addActionListener(e -> cardLayout.show(contentArea, "Expense"));
        incomeBtn.addActionListener(e -> cardLayout.show(contentArea, "Income"));
        assetBtn.addActionListener(e -> cardLayout.show(contentArea, "Assets"));
        removeAssetBtn.addActionListener(e -> {
            removeAssetPanel.loadData();
            cardLayout.show(contentArea, "RemoveAsset");
        });
        removeIncomeBtn.addActionListener(e -> {
            removeIncomePanel.loadData();
            cardLayout.show(contentArea, "RemoveIncome");
        });
        removeExpenseBtn.addActionListener(e -> {
            removeExpensePanel.loadData();
            cardLayout.show(contentArea, "RemoveExpense");
        });

        refreshData();
        new Timer(5000, e -> refreshData()).start();

        setVisible(true);
    }

    private JPanel createDashboardScreen() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JLabel header = new JLabel("Financial Dashboard", SwingConstants.CENTER);
        header.setFont(new Font(null, Font.BOLD, 18));
        panel.add(header, BorderLayout.NORTH);

        JPanel mainContent = new JPanel(new GridLayout(3, 1, 10, 10));

        // 1. CARDS PANEL
        JPanel cardsContainer = new JPanel(new GridLayout(1, 4, 10, 10));
        incomeLabel = new JLabel("Total Income: 0", SwingConstants.CENTER);
        expenseLabel = new JLabel("Total Expense: 0", SwingConstants.CENTER);
        balanceLabel = new JLabel("Current Balance: 0", SwingConstants.CENTER);
        assetsLabel = new JLabel("Total Assets: 0", SwingConstants.CENTER);
        cardsContainer.add(incomeLabel);
        cardsContainer.add(expenseLabel);
        cardsContainer.add(balanceLabel);
        cardsContainer.add(assetsLabel);
        mainContent.add(cardsContainer);

        // 2. PROGRESS PANEL
        JPanel progressPanel = new JPanel(new BorderLayout(5, 5));
        JLabel progressTitle = new JLabel("Monthly Budget Usage:");
        budgetBar = new JProgressBar(0, 100);
        budgetBar.setStringPainted(true);
        warningLabel = new JLabel("Checking status...", SwingConstants.CENTER);
        progressPanel.add(progressTitle, BorderLayout.NORTH);
        progressPanel.add(budgetBar, BorderLayout.CENTER);
        progressPanel.add(warningLabel, BorderLayout.SOUTH);
        mainContent.add(progressPanel);

        // 3. CHARTS PANEL
        JPanel chartsContainer = new JPanel(new GridLayout(1, 3, 10, 10));
        chart = new PieChartPanel(userId, "expense");
        barChart = new BarChartPanel(userId);
        assetChart = new PieChartPanel(userId, "asset");
        chartsContainer.add(chart);
        chartsContainer.add(barChart);
        chartsContainer.add(assetChart);
        mainContent.add(chartsContainer);

        panel.add(mainContent, BorderLayout.CENTER);
        return panel;
    }

    void refreshData() {
        double income = getTotal("income");
        double expense = getTotal("expense");
        double assets = getTotal("asset");
        double balance = income - expense;

        incomeLabel.setText("Total Income: " + income);
        expenseLabel.setText("Total Expense: " + expense);
        balanceLabel.setText("Current Balance: " + balance);
        assetsLabel.setText("Total Assets: " + assets);

        int percent = (income == 0) ? 0 : (int) ((expense / income) * 100);
        budgetBar.setValue(Math.min(percent, 100));
        budgetBar.setString(percent + "% Used");

        if (percent > 100) {
            warningLabel.setText("You have exhausted your budget!");
        } else if (percent > 80) {
            warningLabel.setText("Budget Limit Near!");
        } else if (percent > 50) {
            warningLabel.setText("Budget limit Approaching");
        } else {
            warningLabel.setText("Safe Spending.");
        }

        if (chart != null)
            chart.loadData();
        if (barChart != null)
            barChart.loadData();
        if (assetChart != null)
            assetChart.loadData();

        revalidate();
        repaint();
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
            if (rs.next()) {
                total = rs.getDouble(1);
            }
        } catch (Exception e) {
            // if table dont exist exception raise
        }
        return total;
    }
}
import java.sql.*;
import java.awt.*;
import javax.swing.*;

public class BarChartPanel extends JPanel {
    int userId;
    double income = 0;
    double expense = 0;

    public BarChartPanel(int userId) {
        this.userId = userId;
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(250, 200));
        loadData();
    }

    public void loadData() {
        income = getTotal("income");
        expense = getTotal("expense");
        revalidate();
        repaint();
    }

    private double getTotal(String table) {
        double total = 0;
        try {
            Connection con = DBConnection.getConnection();
            String col = table.equals("asset") ? "value" : "amount";
            String query = "SELECT SUM(" + col + ") FROM " + table + " WHERE user_id=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) total = rs.getDouble(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int h = getHeight();

        // Draw Title
        g.setColor(Color.BLACK);
        g.drawString("Income vs Expense", 10, 20);

        double maxVal = Math.max(income, expense);
        if (maxVal == 0) {
            g.drawString("No data available", 10, 50);
            return;
        }

        int chartHeight = h - 80;
        int bottomY = h - 30;
        int barWidth = 40;

        // Draw Income Bar (Green)
        int incomeHeight = (int) ((income / maxVal) * chartHeight);
        g.setColor(Color.GREEN);
        g.fillRect(30, bottomY - incomeHeight, barWidth, incomeHeight);
        g.setColor(Color.BLACK);
        g.drawRect(30, bottomY - incomeHeight, barWidth, incomeHeight);
        g.drawString("Inc: " + (int) income, 30, bottomY - incomeHeight - 5);

        // Draw Expense Bar (Red)
        int expenseHeight = (int) ((expense / maxVal) * chartHeight);
        g.setColor(Color.RED);
        g.fillRect(100, bottomY - expenseHeight, barWidth, expenseHeight);
        g.setColor(Color.BLACK);
        g.drawRect(100, bottomY - expenseHeight, barWidth, expenseHeight);
        g.drawString("Exp: " + (int) expense, 100, bottomY - expenseHeight - 5);
    }
}

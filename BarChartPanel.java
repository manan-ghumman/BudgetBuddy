import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class BarChartPanel extends JPanel {
    int userId;
    double income = 0;
    double expense = 0;

    public BarChartPanel(int userId) {
        this.userId = userId;
        setBackground(Color.WHITE);
        loadData();
    }

    public void loadData() {
        income = getTotal("income");
        expense = getTotal("expense");
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
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int margin = 50;
        int barWidth = 60;
        int maxBarHeight = h - 2 * margin - 20;

        double maxVal = Math.max(income, expense);
        if (maxVal == 0) {
            g2.setColor(new Color(148, 163, 184));
            g2.drawString("No financial data to compare", w / 2 - 80, h / 2);
            return;
        }

        int incomeHeight = (int) ((income / maxVal) * maxBarHeight);
        int expenseHeight = (int) ((expense / maxVal) * maxBarHeight);

        // Draw Axes
        g2.setColor(new Color(226, 232, 240));
        g2.drawLine(margin, h - margin, w - margin, h - margin); // X-Axis

        // Draw Bars
        int xStart = (w - 2 * barWidth - 40) / 2;

        // Income Bar
        g2.setColor(new Color(34, 197, 94));
        g2.fillRect(xStart, h - margin - incomeHeight, barWidth, incomeHeight);
        g2.setColor(new Color(30, 41, 59));
        g2.drawString("Income", xStart + 5, h - margin + 20);
        g2.drawString("₹" + (int)income, xStart + 5, h - margin - incomeHeight - 5);

        // Expense Bar
        g2.setColor(new Color(239, 68, 68));
        g2.fillRect(xStart + barWidth + 40, h - margin - expenseHeight, barWidth, expenseHeight);
        g2.setColor(new Color(30, 41, 59));
        g2.drawString("Expense", xStart + barWidth + 45, h - margin + 20);
        g2.drawString("₹" + (int)expense, xStart + barWidth + 45, h - margin - expenseHeight - 5);

        // Title
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.drawString("Income vs Expense", w / 2 - 70, 25);
    }
}

import java.sql.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;

public class PieChartPanel extends JPanel {
    int userId;
    String tableName;
    Map<String, Double> data = new LinkedHashMap<>();

    private static final Color[] COLORS = {
        Color.BLUE, Color.GREEN, Color.ORANGE, Color.PINK, Color.MAGENTA, Color.CYAN, Color.RED
    };

    public PieChartPanel(int userId, String tableName) {
        this.userId = userId;
        this.tableName = tableName;
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(250, 200));
        loadData();
    }

    public void loadData() {
        data.clear();
        try {
            Connection con = DBConnection.getConnection();
            String col = tableName.equals("asset") ? "name" : "category";
            String valCol = tableName.equals("asset") ? "value" : "amount";
            
            String query = "SELECT " + col + ", SUM(" + valCol + ") FROM " + tableName + " WHERE user_id=? GROUP BY " + col;
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                data.put(rs.getString(1), rs.getDouble(2));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getWidth();
        int h = getHeight();

        // Draw Title
        g.setColor(Color.BLACK);
        String title = tableName.equals("asset") ? "Asset Distribution" : "Expense Distribution";
        g.drawString(title, 10, 20);

        if (data.isEmpty()) {
            g.drawString("No data available", 10, 50);
            return;
        }

        double total = 0;
        for (double val : data.values()) {
            total += val;
        }

        int size = Math.min(w - 120, h - 50);
        if (size < 50) size = 50;

        int x = 10;
        int y = 35;

        double startAngle = 0;
        int colorIdx = 0;

        for (Map.Entry<String, Double> entry : data.entrySet()) {
            double val = entry.getValue();
            double angle = (val / total) * 360;
            g.setColor(COLORS[colorIdx % COLORS.length]);
            g.fillArc(x, y, size, size, (int) Math.round(startAngle), (int) Math.round(angle));
            startAngle += angle;
            colorIdx++;
        }

        // Draw simple legend on the right side
        int legendX = x + size + 15;
        int legendY = y + 10;
        colorIdx = 0;
        g.setFont(new Font(null, Font.PLAIN, 10));
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            if (legendY > h - 10) break;
            g.setColor(COLORS[colorIdx % COLORS.length]);
            g.fillRect(legendX, legendY, 8, 8);
            g.setColor(Color.BLACK);
            g.drawString(entry.getKey() + ": " + entry.getValue().intValue(), legendX + 12, legendY + 8);
            legendY += 15;
            colorIdx++;
        }
    }
}
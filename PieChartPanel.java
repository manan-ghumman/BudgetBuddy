import java.awt.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

public class PieChartPanel extends JPanel {
 // CO6: Swing GUI
    int userId;
    String tableName;
    Map<String, Double> data = new HashMap<>(); // CO4: Collections framework (HashMap)



    Color[] colors = {
            new Color(102, 187, 106),  
            new Color(66, 165, 245),   
            new Color(255, 167, 38),   
            new Color(171, 71, 188),   
            new Color(38, 198, 218),   
            new Color(255, 112, 67)    
    };

    public PieChartPanel(int userId, String tableName) {
        this.userId = userId;
        this.tableName = tableName;
        setBackground(Color.WHITE);
        loadData();
    }

    void loadData() {
        data.clear();

        try {
            Connection con = DBConnection.getConnection();  // CO5: JDBC connection

            String col = tableName.equals("asset") ? "name" : "category";
            String valCol = tableName.equals("asset") ? "value" : "amount";
            
            String query = "SELECT " + col + ", SUM(" + valCol + ") FROM " + tableName + " WHERE user_id=? GROUP BY " + col;  // CO5: SQL aggregation
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                data.put(rs.getString(1), rs.getDouble(2));
            }

        } catch (Exception e) {   // CO3: Exception handling
            e.printStackTrace();
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (data.size() == 0) {
            g2.setColor(new Color(148, 163, 184));
            g2.setFont(new Font("Arial", Font.ITALIC, 16));
            String msg = "No data available to display chart";
            FontMetrics fm = g2.getFontMetrics();
            int msgX = (getWidth() - fm.stringWidth(msg)) / 2;
            int msgY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(msg, msgX, msgY);
            return;
        }

        double total = 0;
        for (double val : data.values()) total += val;

        int startAngle = 0;
        int i = 0;

        int size = 220;
        int x = 50;
        int y = 30;

        // 🎯 DRAW PIE WITH SMOOTH EDGES
        for (String key : data.keySet()) {

            double value = data.get(key);
            int angle = (int) ((value / total) * 360);

            g2.setColor(colors[i % colors.length]);
            g2.fillArc(x, y, size, size, startAngle, angle);

            // white border for separation
            g2.setColor(Color.WHITE);
            g2.drawArc(x, y, size, size, startAngle, angle);

            startAngle += angle;
            i++;
        }

        // 🎯 TITLE
        g2.setColor(new Color(30, 41, 59));
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        String title = tableName.equals("asset") ? "Asset Distribution" : "Expense Distribution";
        g2.drawString(title, 120, 20);

        // 🎯 LEGEND (clean)
        int legendY = 60;
        i = 0;

        for (String key : data.keySet()) {
            g2.setColor(colors[i % colors.length]);
            g2.fillRoundRect(300, legendY, 15, 15, 5, 5);

            g2.setColor(Color.DARK_GRAY);
            g2.setFont(new Font("Arial", Font.PLAIN, 13));
            g2.drawString(key + "  ₹" + data.get(key), 325, legendY + 12);

            legendY += 25;
            i++;
        }
    }
}
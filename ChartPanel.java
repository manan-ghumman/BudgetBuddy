import java.awt.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

public class ChartPanel extends JPanel {

    int userId;
    Map<String, Double> data = new HashMap<>();

    public ChartPanel(int userId) {
        this.userId = userId;
        loadData();
    }

    void loadData() {
        data.clear();

        try {
            Connection con = DBConnection.getConnection();

            String query = "SELECT category, SUM(amount) FROM expense WHERE user_id=? GROUP BY category";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                data.put(rs.getString(1), rs.getDouble(2));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (data.size() == 0) return;

        int width = getWidth();
        int height = getHeight();

        int barWidth = width / data.size();
        int x = 10;

        double max = 0;
        for (double val : data.values()) {
            if (val > max) max = val;
        }

        for (String key : data.keySet()) {

            double value = data.get(key);
            int barHeight = (int)((value / max) * (height - 50));

            g.setColor(new Color(33,150,243));
            g.fillRect(x, height - barHeight - 30, barWidth - 20, barHeight);

            g.setColor(Color.BLACK);
            g.drawString(key, x, height - 10);

            g.drawString(String.valueOf(value), x, height - barHeight - 35);

            x += barWidth;
        }
    }
}
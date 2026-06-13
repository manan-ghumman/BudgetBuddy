import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class RemoveAssetFrame extends JPanel {

    int userId;
    Runnable onComplete;
    JTable assetTable;
    DefaultTableModel tableModel;
    JComboBox<AssetItem> assetComboBox;

    static class AssetItem {
        int id;
        String name;
        double value;

        public AssetItem(int id, String name, double value) {
            this.id = id;
            this.name = name;
            this.value = value;
        }

        @Override
        public String toString() {
            return name + " (₹" + value + ")";
        }
    }

    public RemoveAssetFrame(int userId, Runnable onComplete) {
        this.userId = userId;
        this.onComplete = onComplete;

        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Remove Asset", SwingConstants.CENTER);
        titleLabel.setFont(new Font(null, Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Table definition
        tableModel = new DefaultTableModel(new Object[]{"ID", "Asset Name", "Value"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // read-only table
            }
        };
        assetTable = new JTable(tableModel);
        
        // Hide the ID column
        assetTable.getColumnModel().getColumn(0).setMinWidth(0);
        assetTable.getColumnModel().getColumn(0).setMaxWidth(0);
        assetTable.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(assetTable);
        add(scrollPane, BorderLayout.CENTER);

        // Combo box & Buttons Panel
        JPanel controlPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        controlPanel.add(new JLabel("Select Asset to Remove:"), gbc);

        gbc.gridx = 1;
        assetComboBox = new JComboBox<>();
        controlPanel.add(assetComboBox, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton removeBtn = new JButton("Remove");
        JButton cancelBtn = new JButton("Cancel");

        buttonPanel.add(removeBtn);
        buttonPanel.add(cancelBtn);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        controlPanel.add(buttonPanel, gbc);

        add(controlPanel, BorderLayout.SOUTH);

        removeBtn.addActionListener(e -> removeAsset());
        cancelBtn.addActionListener(e -> {
            if (onComplete != null)
                onComplete.run();
        });

        loadData();
    }

    public void loadData() {
        tableModel.setRowCount(0);
        assetComboBox.removeAllItems();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT id, name, value FROM asset WHERE user_id = ?")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    double value = rs.getDouble("value");
                    tableModel.addRow(new Object[]{id, name, value});
                    assetComboBox.addItem(new AssetItem(id, name, value));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading assets: " + e.getMessage());
        }
    }

    void removeAsset() {
        AssetItem selectedAsset = (AssetItem) assetComboBox.getSelectedItem();
        if (selectedAsset == null) {
            JOptionPane.showMessageDialog(this, "Please select an asset to remove.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to remove the asset '" + selectedAsset.name + "'?", 
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement("DELETE FROM asset WHERE id = ? AND user_id = ?")) {
                ps.setInt(1, selectedAsset.id);
                ps.setInt(2, userId);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Asset removed successfully!");
                loadData();

                if (onComplete != null)
                    onComplete.run();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error removing asset: " + e.getMessage());
            }
        }
    }
}

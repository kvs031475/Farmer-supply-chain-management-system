import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class OrderHistoryPage extends JFrame {

    private int customerId;
    private JTable historyTable;
    private DefaultTableModel model;

    public OrderHistoryPage(int customerId) {
        this.customerId = customerId;

        setTitle("Order History - AgriConnect");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[]{
                "Order ID", "Total Price", "Delivered On"
        }, 0);
        historyTable = new JTable(model);
        add(new JScrollPane(historyTable), BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadHistory());

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.add(refreshBtn);
        add(panel, BorderLayout.SOUTH);

        loadHistory();
    }

    private void loadHistory() {
        model.setRowCount(0);
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/agriconnect", "root", "abc123")) {

            String query = "SELECT DISTINCT o.order_id, o.total_price, h.delivered_on " +
                    "FROM orders o " +
                    "JOIN order_history h ON o.order_id = h.order_id " +
                    "WHERE o.customer_id=? AND o.status='Delivered' " +
                    "ORDER BY h.delivered_on DESC";

            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, customerId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("order_id"),
                        rs.getDouble("total_price"),
                        rs.getTimestamp("delivered_on")
                });
            }

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No delivered orders found.");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading order history!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OrderHistoryPage(1).setVisible(true));
    }
}

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class PendingOrdersPage extends JFrame {

    private int customerId;
    private JTable ordersTable;
    private DefaultTableModel model;

    public PendingOrdersPage(int customerId) {
        this.customerId = customerId;

        setTitle("Pending Orders - AgriConnect");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[]{
                "Order ID", "Product Name", "Quantity", "Total Price", "Status"
        }, 0);
        ordersTable = new JTable(model);
        add(new JScrollPane(ordersTable), BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadOrders());
        add(refreshBtn, BorderLayout.SOUTH);

        loadOrders();
    }

    private void loadOrders() {
        model.setRowCount(0);
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/agriconnect", "root", "abc123")) {

            // Step 1: Fetch all non-delivered orders
            String query = "SELECT o.order_id, o.status, oi.product_id, oi.quantity, oi.price, p.product_name " +
                    "FROM orders o " +
                    "JOIN order_items oi ON o.order_id = oi.order_id " +
                    "JOIN farmer_products p ON oi.product_id = p.product_id " +
                    "WHERE o.customer_id = ? AND o.status != 'Delivered' " +
                    "ORDER BY o.order_id DESC";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, customerId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                String productName = rs.getString("product_name");
                int quantity = rs.getInt("quantity");
                double totalPrice = rs.getDouble("price");
                String status = rs.getString("status");

                model.addRow(new Object[]{orderId, productName, quantity, totalPrice, status});
            }

            // Step 2: Move delivered orders to history
            moveDeliveredToHistory(con);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void moveDeliveredToHistory(Connection con) {
        try {
            // Find delivered orders
            String query = "SELECT * FROM orders WHERE customer_id=? AND status='Delivered'";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, customerId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                int orderId = rs.getInt("order_id");

                // Insert each order item into order_history
                String itemsQuery = "SELECT * FROM order_items WHERE order_id=?";
                PreparedStatement pstItems = con.prepareStatement(itemsQuery);
                pstItems.setInt(1, orderId);
                ResultSet itemsRs = pstItems.executeQuery();

                while (itemsRs.next()) {
                    String insert = "INSERT INTO order_history " +
                            "(order_id, customer_id, product_id, quantity, total_price, delivered_on) " +
                            "VALUES (?, ?, ?, ?, ?, NOW())";
                    PreparedStatement pst2 = con.prepareStatement(insert);
                    pst2.setInt(1, orderId);
                    pst2.setInt(2, customerId);
                    pst2.setInt(3, itemsRs.getInt("product_id"));
                    pst2.setInt(4, itemsRs.getInt("quantity"));
                    pst2.setDouble(5, itemsRs.getDouble("price"));
                    pst2.executeUpdate();
                }

                // Remove from current orders
                String deleteOrder = "DELETE FROM orders WHERE order_id=?";
                PreparedStatement pstDel = con.prepareStatement(deleteOrder);
                pstDel.setInt(1, orderId);
                pstDel.executeUpdate();

                // Remove items
                String deleteItems = "DELETE FROM order_items WHERE order_id=?";
                PreparedStatement pstDelItems = con.prepareStatement(deleteItems);
                pstDelItems.setInt(1, orderId);
                pstDelItems.executeUpdate();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PendingOrdersPage(1).setVisible(true));
    }
}

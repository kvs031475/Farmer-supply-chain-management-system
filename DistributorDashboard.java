import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class DistributorDashboard extends JFrame implements ActionListener {

    private int distributorId;
    private JTable ordersTable;
    private DefaultTableModel model;
    private JButton markDeliveredBtn, refreshBtn, logoutBtn;

    public DistributorDashboard(int distributorId) {
        this.distributorId = distributorId;

        setTitle("Distributor Dashboard - AgriConnect");
        setSize(650, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[]{
                "Order ID", "Customer ID", "Total Price", "Status"
        }, 0);

        ordersTable = new JTable(model);
        add(new JScrollPane(ordersTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout());
        markDeliveredBtn = new JButton("Mark as Delivered");
        refreshBtn = new JButton("Refresh");
        logoutBtn = new JButton("Logout");

        bottomPanel.add(markDeliveredBtn);
        bottomPanel.add(refreshBtn);
        bottomPanel.add(logoutBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        markDeliveredBtn.addActionListener(this);
        refreshBtn.addActionListener(this);
        logoutBtn.addActionListener(this);

        loadAssignedOrders();
    }

    private void loadAssignedOrders() {
        model.setRowCount(0);
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/agriconnect", "root", "abc123")) {

            String query = "SELECT order_id, customer_id, total_price, status " +
                    "FROM orders WHERE distributor_id=? AND status IN ('Assigned','Shipped') " +
                    "ORDER BY order_id ASC";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, distributorId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("order_id"),
                        rs.getInt("customer_id"),
                        rs.getDouble("total_price"),
                        rs.getString("status")
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading assigned orders!");
        }
    }

    private void markOrderDelivered() {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order!");
            return;
        }

        int orderId = (int) model.getValueAt(selectedRow, 0);

        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/agriconnect", "root", "abc123")) {

            con.setAutoCommit(false);

            String updateStatus = "UPDATE orders SET status='Delivered' WHERE order_id=?";
            PreparedStatement pstUpdate = con.prepareStatement(updateStatus);
            pstUpdate.setInt(1, orderId);
            pstUpdate.executeUpdate();

            String insertHistory = "INSERT INTO order_history (order_id, customer_id, product_id, quantity, total_price, delivered_on) " +
                    "SELECT o.order_id, o.customer_id, oi.product_id, oi.quantity, oi.price, NOW() " +
                    "FROM orders o JOIN order_items oi ON o.order_id = oi.order_id WHERE o.order_id=?";
            PreparedStatement pstHistory = con.prepareStatement(insertHistory);
            pstHistory.setInt(1, orderId);
            pstHistory.executeUpdate();

            con.commit();
            JOptionPane.showMessageDialog(this, "Order marked as Delivered!");
            loadAssignedOrders();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating delivery status!");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == markDeliveredBtn) markOrderDelivered();
        else if (e.getSource() == refreshBtn) loadAssignedOrders();
        else if (e.getSource() == logoutBtn) {
            new DistributorLogin().setVisible(true);
            dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DistributorDashboard(1).setVisible(true));
    }
}

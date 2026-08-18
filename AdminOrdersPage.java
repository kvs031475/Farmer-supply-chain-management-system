import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AdminOrdersPage extends JFrame implements ActionListener {

    private JTable ordersTable;
    private DefaultTableModel model;
    private JButton assignBtn, backBtn, refreshBtn;
    private JComboBox<String> distributorCombo;

    public AdminOrdersPage() {
        setTitle("Manage Orders - Admin");
        setSize(700, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Columns: Only show Order ID, Customer ID, Total Price, Status
        model = new DefaultTableModel(new String[]{
                "Order ID", "Customer ID", "Total Price", "Status"
        }, 0);
        ordersTable = new JTable(model);
        add(new JScrollPane(ordersTable), BorderLayout.CENTER);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        distributorCombo = new JComboBox<>();
        loadDistributors();

        assignBtn = new JButton("Assign Distributor");
        refreshBtn = new JButton("Refresh");
        backBtn = new JButton("Back");

        bottomPanel.add(new JLabel("Select Distributor:"));
        bottomPanel.add(distributorCombo);
        bottomPanel.add(assignBtn);
        bottomPanel.add(refreshBtn);
        bottomPanel.add(backBtn);

        add(bottomPanel, BorderLayout.SOUTH);

        assignBtn.addActionListener(this);
        refreshBtn.addActionListener(this);
        backBtn.addActionListener(this);

        loadOrders();
    }

    private void loadOrders() {
        model.setRowCount(0);
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/agriconnect", "root", "abc123")) {

            String query = "SELECT order_id, customer_id, total_price, status FROM orders " +
                    "WHERE status IN ('Pending', 'Assigned') ORDER BY order_id ASC";
            PreparedStatement pst = con.prepareStatement(query);
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
            JOptionPane.showMessageDialog(this, "Error loading orders!");
        }
    }

    private void loadDistributors() {
        distributorCombo.removeAllItems();
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/agriconnect", "root", "abc123")) {

            String query = "SELECT distributor_id, name FROM distributors";
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                distributorCombo.addItem(rs.getInt("distributor_id") + " - " + rs.getString("name"));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading distributors!");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == assignBtn) assignDistributor();
        else if (e.getSource() == refreshBtn) loadOrders();
        else if (e.getSource() == backBtn) {
            new AdminDashboard().setVisible(true);
            dispose();
        }
    }

    private void assignDistributor() {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an order to assign!");
            return;
        }

        String distributor = (String) distributorCombo.getSelectedItem();
        if (distributor == null) {
            JOptionPane.showMessageDialog(this, "Select a distributor!");
            return;
        }

        int orderId = (int) model.getValueAt(selectedRow, 0);
        int distributorId = Integer.parseInt(distributor.split(" - ")[0]);

        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/agriconnect", "root", "abc123")) {

            String update = "UPDATE orders SET distributor_id=?, status='Assigned' WHERE order_id=?";
            PreparedStatement pst = con.prepareStatement(update);
            pst.setInt(1, distributorId);
            pst.setInt(2, orderId);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Distributor assigned successfully!");
            loadOrders();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error assigning distributor!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminOrdersPage().setVisible(true));
    }
}

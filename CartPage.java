import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CartPage extends JFrame implements ActionListener {

    private JTable cartTable;
    private DefaultTableModel model;
    private JButton placeOrderBtn, backBtn, removeBtn;
    private int customerId;

    // Labels for cost details
    private JLabel subtotalLbl, gstLbl, platformFeeLbl, totalLbl;

    public CartPage(int customerId) {
        this.customerId = customerId;

        setTitle("My Cart - AgriConnect");
        setSize(750, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // === Initialize labels first ===
        subtotalLbl = new JLabel("₹0.00");
        gstLbl = new JLabel("₹0.00");
        platformFeeLbl = new JLabel("₹5.00");
        totalLbl = new JLabel("<html><b>₹0.00</b></html>");

        // === TABLE SETUP ===
        model = new DefaultTableModel(new String[]{"Product ID", "Name", "Price", "Quantity"}, 0);
        cartTable = new JTable(model);
        add(new JScrollPane(cartTable), BorderLayout.CENTER);

        // === COST DETAILS PANEL ===
        JPanel costPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        costPanel.setBorder(BorderFactory.createTitledBorder("Order Summary"));
        costPanel.add(new JLabel("Subtotal:"));
        costPanel.add(subtotalLbl);
        costPanel.add(new JLabel("GST (5%):"));
        costPanel.add(gstLbl);
        costPanel.add(new JLabel("Platform Fee:"));
        costPanel.add(platformFeeLbl);
        costPanel.add(new JLabel("<html><b>Total:</b></html>"));
        costPanel.add(totalLbl);
        add(costPanel, BorderLayout.EAST);

        // === BUTTONS PANEL ===
        JPanel btnPanel = new JPanel(new FlowLayout());
        removeBtn = new JButton("Remove Selected Product");
        placeOrderBtn = new JButton("Place Order");
        backBtn = new JButton("Back");

        btnPanel.add(removeBtn);
        btnPanel.add(placeOrderBtn);
        btnPanel.add(backBtn);
        add(btnPanel, BorderLayout.SOUTH);

        loadCartItems();
        updateTotals();

        placeOrderBtn.addActionListener(this);
        backBtn.addActionListener(this);
        removeBtn.addActionListener(this);
    }

    private void loadCartItems() {
        model.setRowCount(0);
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/agriconnect", "root", "abc123")) {

            for (Integer productId : CustomerDashboard.cart.keySet()) {
                int qty = CustomerDashboard.cart.get(productId);
                PreparedStatement pst = con.prepareStatement(
                        "SELECT product_id, product_name, price FROM farmer_products WHERE product_id=?");
                pst.setInt(1, productId);
                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("product_id"),
                            rs.getString("product_name"),
                            rs.getDouble("price"),
                            qty
                    });
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updateTotals() {
        double subtotal = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            double price = (double) model.getValueAt(i, 2);
            int qty = (int) model.getValueAt(i, 3);
            subtotal += price * qty;
        }
        double gst = subtotal * 0.05;
        double platformFee = 5.0;
        double total = subtotal + gst + platformFee;

        subtotalLbl.setText(String.format("₹%.2f", subtotal));
        gstLbl.setText(String.format("₹%.2f", gst));
        platformFeeLbl.setText(String.format("₹%.2f", platformFee));
        totalLbl.setText(String.format("<html><b>₹%.2f</b></html>", total));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == placeOrderBtn) {
            placeOrder();
        } else if (e.getSource() == backBtn) {
            dispose();
        } else if (e.getSource() == removeBtn) {
            removeSelectedProduct();
        }
    }

    private void removeSelectedProduct() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to remove.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int productId = (int) model.getValueAt(selectedRow, 0);
        String productName = (String) model.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Remove '" + productName + "' from cart?", "Confirm Removal",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            CustomerDashboard.cart.remove(productId);
            model.removeRow(selectedRow);
            updateTotals();
            JOptionPane.showMessageDialog(this, productName + " removed from cart.");
        }
    }

    private void placeOrder() {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Your cart is empty!", "Empty Cart", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/agriconnect", "root", "abc123")) {

            double subtotal = 0;
            for (int i = 0; i < model.getRowCount(); i++) {
                int quantity = (int) model.getValueAt(i, 3);
                double price = (double) model.getValueAt(i, 2);
                subtotal += price * quantity;
            }

            double gst = subtotal * 0.05;
            double platformFee = 5.0;
            double totalPrice = subtotal + gst + platformFee;

            String insertOrder = "INSERT INTO orders (customer_id, total_price) VALUES (?, ?)";
            PreparedStatement pstOrder = con.prepareStatement(insertOrder, Statement.RETURN_GENERATED_KEYS);
            pstOrder.setInt(1, customerId);
            pstOrder.setDouble(2, totalPrice);
            pstOrder.executeUpdate();

            ResultSet generatedKeys = pstOrder.getGeneratedKeys();
            int orderId = 0;
            if (generatedKeys.next()) {
                orderId = generatedKeys.getInt(1);
            }

            String insertItem = "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
            PreparedStatement pstItem = con.prepareStatement(insertItem);

            for (int i = 0; i < model.getRowCount(); i++) {
                int productId = (int) model.getValueAt(i, 0);
                int quantity = (int) model.getValueAt(i, 3);
                double price = (double) model.getValueAt(i, 2);

                pstItem.setInt(1, orderId);
                pstItem.setInt(2, productId);
                pstItem.setInt(3, quantity);
                pstItem.setDouble(4, price * quantity);
                pstItem.executeUpdate();
            }

            CustomerDashboard.cart.clear();
            JOptionPane.showMessageDialog(this,
                    "Order placed successfully! Redirecting to billing page...");

            // === Open Bill Page ===
            new BillPage(customerId, totalPrice).setVisible(true);
            dispose();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CartPage(1).setVisible(true));
    }
}

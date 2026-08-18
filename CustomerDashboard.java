import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.HashMap;

public class CustomerDashboard extends JFrame implements ActionListener {

    private int customerId;
    public static HashMap<Integer, Integer> cart = new HashMap<>();
    private JButton viewCartBtn, orderHistoryBtn, pendingOrdersBtn, logoutBtn;

    public CustomerDashboard(int customerId) {
        this.customerId = customerId;

        setTitle("Customer Dashboard - AgriConnect");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ======= Top Button Panel =======
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        topPanel.setBackground(new Color(245, 245, 245));

        viewCartBtn = new JButton("View Cart");
        orderHistoryBtn = new JButton("Order History");
        pendingOrdersBtn = new JButton("Pending Orders");
        logoutBtn = new JButton("Logout");

        // Button Styles
        JButton[] buttons = {viewCartBtn, orderHistoryBtn, pendingOrdersBtn, logoutBtn};
        for (JButton btn : buttons) {
            btn.setFocusPainted(false);
            btn.setBackground(new Color(34, 139, 34));
            btn.setForeground(Color.WHITE);
        }
        logoutBtn.setBackground(new Color(178, 34, 34)); // red color for logout

        // Action Listeners
        viewCartBtn.addActionListener(this);
        orderHistoryBtn.addActionListener(this);
        pendingOrdersBtn.addActionListener(this);
        logoutBtn.addActionListener(this);

        // Add Buttons to Top Panel
        topPanel.add(viewCartBtn);
        topPanel.add(orderHistoryBtn);
        topPanel.add(pendingOrdersBtn);
        topPanel.add(logoutBtn);
        add(topPanel, BorderLayout.NORTH);

        // ======= Product Display Panel =======
        JPanel productPanel = new JPanel(new GridLayout(0, 4, 15, 15)); // 4 per row
        productPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(productPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        loadProducts(productPanel);
    }

    private void loadProducts(JPanel productPanel) {
        productPanel.removeAll();

        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/agriconnect", "root", "abc123")) {

            String sql = "SELECT product_id, product_name, description, price, image_path FROM farmer_products";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                int productId = rs.getInt("product_id");
                String name = rs.getString("product_name");
                double price = rs.getDouble("price");
                String imgPath = rs.getString("image_path");

                // ======= Product Card (Taller for Bigger Image) =======
                JPanel card = new JPanel(new BorderLayout(4, 4));
                card.setPreferredSize(new Dimension(200, 200)); // Increased height for bigger image
                card.setMaximumSize(new Dimension(200, 200));
                card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
                card.setBackground(Color.WHITE);

                // ======= Image (Larger and centered) =======
                JLabel imgLabel;
                if (imgPath != null && !imgPath.isEmpty()) {
                    ImageIcon icon = new ImageIcon(imgPath.replace("\"", ""));
                    Image scaled = icon.getImage().getScaledInstance(130, 100, Image.SCALE_SMOOTH);
                    imgLabel = new JLabel(new ImageIcon(scaled));
                } else {
                    imgLabel = new JLabel("No Image", SwingConstants.CENTER);
                }
                imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
                imgLabel.setBorder(new EmptyBorder(5, 5, 0, 5));
                card.add(imgLabel, BorderLayout.NORTH);

                // ======= Product Details =======
                JTextArea details = new JTextArea(name + "\n₹" + price);
                details.setEditable(false);
                details.setWrapStyleWord(true);
                details.setLineWrap(true);
                details.setBackground(Color.WHITE);
                details.setFont(new Font("SansSerif", Font.PLAIN, 12));
                details.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
                details.setRows(2);
                card.add(details, BorderLayout.CENTER);

                // ======= Bottom Add-to-Cart Section =======
                JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 3));
                bottomPanel.setBackground(Color.WHITE);
                JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
                qtySpinner.setPreferredSize(new Dimension(45, 22));
                JButton addBtn = new JButton("Add");
                addBtn.setFont(new Font("SansSerif", Font.PLAIN, 11));
                addBtn.setMargin(new Insets(1, 8, 1, 8));

                addBtn.addActionListener(e -> {
                    int qty = (Integer) qtySpinner.getValue();
                    cart.put(productId, cart.getOrDefault(productId, 0) + qty);
                    JOptionPane.showMessageDialog(this,
                            name + " (x" + qty + ") added to cart!");
                });

                bottomPanel.add(new JLabel("Qty:"));
                bottomPanel.add(qtySpinner);
                bottomPanel.add(addBtn);
                card.add(bottomPanel, BorderLayout.SOUTH);

                productPanel.add(card);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading products!");
        }

        productPanel.revalidate();
        productPanel.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == viewCartBtn) {
            new CartPage(customerId).setVisible(true);
        } else if (e.getSource() == orderHistoryBtn) {
            new OrderHistoryPage(customerId).setVisible(true);
        } else if (e.getSource() == pendingOrdersBtn) {
            new PendingOrdersPage(customerId).setVisible(true);
        } else if (e.getSource() == logoutBtn) {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to log out?",
                    "Logout Confirmation",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                new CustomerLogin().setVisible(true);
                dispose(); // close the dashboard
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CustomerDashboard(1).setVisible(true));
    }
}

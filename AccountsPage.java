import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.time.LocalDate;
import java.util.Date;
import com.toedter.calendar.JDateChooser;

public class AccountsPage extends JFrame {

    private JTable table;
    private JLabel grandTotalLbl, profitLbl;
    private JButton backBtn, loadBtn;
    private JDateChooser dateChooser;

    public AccountsPage() {
        setTitle("Accounts - AgriConnect");
        setSize(950, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(245, 245, 245));

        // Header Panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        headerPanel.setBackground(new Color(245, 245, 245));

        JLabel title = new JLabel("Delivery Summary by Date");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));

        JLabel dateLbl = new JLabel("Select Date:");
        dateLbl.setFont(new Font("SansSerif", Font.PLAIN, 16));

        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        dateChooser.setFont(new Font("SansSerif", Font.PLAIN, 16));
        dateChooser.setDate(java.sql.Date.valueOf(LocalDate.now()));

        loadBtn = new JButton("Load Data");
        loadBtn.setBackground(new Color(34, 139, 34));
        loadBtn.setForeground(Color.WHITE);
        loadBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
        loadBtn.addActionListener(this::loadDataAction);

        headerPanel.add(title);
        headerPanel.add(dateLbl);
        headerPanel.add(dateChooser);
        headerPanel.add(loadBtn);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Table setup
        String[] columns = {"Order ID", "Customer ID", "Product ID", "Quantity", "Total Price (₹)", "Delivered On"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom info panel
        JPanel bottomPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        bottomPanel.setBackground(new Color(245, 245, 245));

        grandTotalLbl = new JLabel("Grand Total: ₹0", SwingConstants.CENTER);
        profitLbl = new JLabel("Profit (₹5 per delivery): ₹0", SwingConstants.CENTER);

        grandTotalLbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        profitLbl.setFont(new Font("SansSerif", Font.BOLD, 16));

        backBtn = new JButton("Back to Dashboard");
        backBtn.setFont(new Font("SansSerif", Font.PLAIN, 15));
        backBtn.setBackground(new Color(34, 139, 34));
        backBtn.setForeground(Color.WHITE);
        backBtn.addActionListener(e -> {
            new AdminDashboard().setVisible(true);
            dispose();
        });

        bottomPanel.add(grandTotalLbl);
        bottomPanel.add(profitLbl);
        bottomPanel.add(backBtn);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Load today's data by default
        loadAccountData(model, LocalDate.now());
    }

    private void loadDataAction(ActionEvent e) {
        Date selectedDate = dateChooser.getDate();
        if (selectedDate == null) {
            JOptionPane.showMessageDialog(this, "Please select a valid date.", "Invalid Date", JOptionPane.WARNING_MESSAGE);
            return;
        }
        LocalDate localDate = new java.sql.Date(selectedDate.getTime()).toLocalDate();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // clear previous rows
        loadAccountData(model, localDate);
    }

    private void loadAccountData(DefaultTableModel model, LocalDate date) {
        double grandTotal = 0;
        int deliveryCount = 0;

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/agriconnect", "root", "abc123");
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT order_id, customer_id, product_id, quantity, total_price, delivered_on " +
                             "FROM order_history WHERE DATE(delivered_on) = ?")) {

            stmt.setDate(1, java.sql.Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                int customerId = rs.getInt("customer_id");
                int productId = rs.getInt("product_id");
                int quantity = rs.getInt("quantity");
                double totalPrice = rs.getDouble("total_price");
                Timestamp deliveredOn = rs.getTimestamp("delivered_on");

                model.addRow(new Object[]{orderId, customerId, productId, quantity, totalPrice, deliveredOn});
                grandTotal += totalPrice;
                deliveryCount++;
            }

            double profit = deliveryCount * 5.0;
            grandTotalLbl.setText("Grand Total: ₹" + String.format("%.2f", grandTotal));
            profitLbl.setText("Profit (₹5 per delivery): ₹" + String.format("%.2f", profit));

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AccountsPage().setVisible(true));
    }
}

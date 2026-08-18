import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AdminDashboard extends JFrame implements ActionListener {

    private JButton manageFarmersBtn, manageCustomersBtn, manageDistributorsBtn,
            manageProductsBtn, manageOrdersBtn, accountsBtn, logoutBtn;

    public AdminDashboard() {
        setTitle("Admin Dashboard - AgriConnect");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));

        JLabel title = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        mainPanel.add(title, BorderLayout.NORTH);

        // Adjusted layout to include Accounts button
        JPanel buttonPanel = new JPanel(new GridLayout(4, 2, 20, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        buttonPanel.setBackground(new Color(245, 245, 245));

        manageFarmersBtn = createStyledButton("Manage Farmers");
        manageCustomersBtn = createStyledButton("Manage Customers");
        manageDistributorsBtn = createStyledButton("Manage Distributors");
        manageProductsBtn = createStyledButton("Manage Products");
        manageOrdersBtn = createStyledButton("Manage Orders");
        accountsBtn = createStyledButton("Accounts");
        logoutBtn = createStyledButton("Logout");

        buttonPanel.add(manageFarmersBtn);
        buttonPanel.add(manageCustomersBtn);
        buttonPanel.add(manageDistributorsBtn);
        buttonPanel.add(manageProductsBtn);
        buttonPanel.add(manageOrdersBtn);
        buttonPanel.add(accountsBtn);
        buttonPanel.add(logoutBtn);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        add(mainPanel);

        // Add action listeners
        manageFarmersBtn.addActionListener(this);
        manageCustomersBtn.addActionListener(this);
        manageDistributorsBtn.addActionListener(this);
        manageProductsBtn.addActionListener(this);
        manageOrdersBtn.addActionListener(this);
        accountsBtn.addActionListener(this);
        logoutBtn.addActionListener(this);
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btn.setBackground(new Color(34, 139, 34));
        btn.setForeground(Color.WHITE);
        return btn;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == manageFarmersBtn) {
            new ManageFarmers().setVisible(true);
            dispose();
        } else if (source == manageCustomersBtn) {
            new ManageCustomers().setVisible(true);
            dispose();
        } else if (source == manageDistributorsBtn) {
            new ManageDistributors().setVisible(true);
            dispose();
        } else if (source == manageProductsBtn) {
            new ManageProducts().setVisible(true);
            dispose();
        } else if (source == manageOrdersBtn) {
            new AdminOrdersPage().setVisible(true);
            dispose();
        } else if (source == accountsBtn) {
            new AccountsPage().setVisible(true);
            dispose();
        } else if (source == logoutBtn) {
            new AdminLogin().setVisible(true);
            dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminDashboard().setVisible(true));
    }
}

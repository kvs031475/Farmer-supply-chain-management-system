import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CustomerLogin extends JFrame implements ActionListener {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton, backButton;

    public CustomerLogin() {
        setTitle("Customer Login - AgriConnect");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Customer Login", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1; gbc.gridy++; gbc.gridx = 0;
        panel.add(new JLabel("Email ID:"), gbc);
        emailField = new JTextField(20);
        gbc.gridx = 1; panel.add(emailField, gbc);

        gbc.gridy++; gbc.gridx = 0;
        panel.add(new JLabel("Password:"), gbc);
        passwordField = new JPasswordField(20);
        gbc.gridx = 1; panel.add(passwordField, gbc);

        loginButton = new JButton("Login");
        backButton = new JButton("Back");
        loginButton.setBackground(new Color(34, 139, 34));
        loginButton.setForeground(Color.WHITE);
        backButton.setBackground(Color.GRAY);
        backButton.setForeground(Color.WHITE);

        gbc.gridy++; gbc.gridx = 0; panel.add(backButton, gbc);
        gbc.gridx = 1; panel.add(loginButton, gbc);

        add(panel);

        loginButton.addActionListener(this);
        backButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            String email = emailField.getText().trim();
            String password = String.valueOf(passwordField.getPassword());

            int customerId = validateLogin(email, password);
            if (customerId != -1) {
                JOptionPane.showMessageDialog(this, "Login Successful!");
                new CustomerDashboard(customerId).setVisible(true); // open dashboard
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Email ID or Password", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == backButton) {
            new LoginPage().setVisible(true);
            dispose();
        }
    }

    private int validateLogin(String email, String password) {
        int customerId = -1;
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/agriconnect", "root", "abc123")) {
            String query = "SELECT customer_id FROM customers WHERE email=? AND password=?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, email);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                customerId = rs.getInt("customer_id");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return customerId;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CustomerLogin().setVisible(true));
    }
}

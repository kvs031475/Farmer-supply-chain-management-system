import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AdminLogin extends JFrame implements ActionListener {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton, backButton;

    public AdminLogin() {
        setTitle("Admin Login - AgriConnect");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // ===== Main Panel =====
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel title = new JLabel("Admin Login", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);

        // Email Label + Field
        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(new JLabel("Email ID:"), gbc);

        emailField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        // Password Label + Field
        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(new JLabel("Password:"), gbc);

        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        // Buttons
        loginButton = new JButton("Login");
        backButton = new JButton("Back");
        loginButton.setBackground(new Color(34, 139, 34));
        loginButton.setForeground(Color.WHITE);
        backButton.setBackground(Color.GRAY);
        backButton.setForeground(Color.WHITE);

        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(backButton, gbc);
        gbc.gridx = 1;
        panel.add(loginButton, gbc);

        add(panel);

        // Action Listeners
        loginButton.addActionListener(this);
        backButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            String email = emailField.getText().trim();
            String password = String.valueOf(passwordField.getPassword());

            if (validateLogin(email, password)) {
                JOptionPane.showMessageDialog(this, "Login Successful!");
                new AdminDashboard().setVisible(true); // Redirect to admin dashboard
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Email ID or Password", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == backButton) {
            new LoginPage().setVisible(true);
            dispose();
        }
    }

    private boolean validateLogin(String email, String password) {
        boolean status = false;
        try {
            // Connect to DB (adjust db name, user, pass)
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/agriconnect", "root", "abc123");

            String query = "SELECT * FROM admins WHERE email=? AND password=?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, email);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                status = true;
            }

            con.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return status;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminLogin().setVisible(true));
    }
}

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPage extends JFrame implements ActionListener {

    private JButton adminButton, farmerButton, customerButton, distributorButton, signupButton;

    public LoginPage() {
        setTitle("AgriConnect - Login");
        setSize(500, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // ===== Main Panel =====
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));

        // ----- Title -----
        JLabel title = new JLabel("Welcome to AgriConnect", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        mainPanel.add(title, BorderLayout.NORTH);

        // ----- Button Panel -----
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 1, 15, 15)); // changed from 4 → 5
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 80, 30, 80));
        buttonPanel.setBackground(new Color(245, 245, 245));

        adminButton = createStyledButton("Admin Login");
        farmerButton = createStyledButton("Farmer Login");
        customerButton = createStyledButton("Customer Login");
        distributorButton = createStyledButton("Distributor Login");
        signupButton = createStyledButton("Sign Up");

        buttonPanel.add(adminButton);
        buttonPanel.add(farmerButton);
        buttonPanel.add(customerButton);
        buttonPanel.add(distributorButton);
        buttonPanel.add(signupButton);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        add(mainPanel);

        // Add action listeners
        adminButton.addActionListener(this);
        farmerButton.addActionListener(this);
        customerButton.addActionListener(this);
        distributorButton.addActionListener(this);
        signupButton.addActionListener(this);
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btn.setBackground(new Color(34, 139, 34));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return btn;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == adminButton) {
            new AdminLogin().setVisible(true); // Open Admin Login page
            dispose(); // Close current LoginPage
        }
        else if (source == farmerButton) {
            new FarmerLogin().setVisible(true);
            dispose();
        } else if (source == customerButton) {
            new CustomerLogin().setVisible(true);
            dispose();
        } else if (source == distributorButton) {
            new DistributorLogin().setVisible(true);
            dispose();
        } else if (source == signupButton) {
            new SignUp().setVisible(true);
            dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
    }
}

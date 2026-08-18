import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class BillPage extends JFrame implements ActionListener {

    private int customerId;
    private double totalAmount;
    private JLabel amountLbl, upiImageLbl;
    private JRadioButton cashBtn, upiBtn, cardBtn;
    private JTextField cardNumField, cvvField;
    private JButton payBtn;
    private ButtonGroup paymentGroup;

    public BillPage(int customerId, double totalAmount) {
        this.customerId = customerId;
        this.totalAmount = totalAmount;

        setTitle("Billing - AgriConnect");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel heading = new JLabel("Complete Your Payment", SwingConstants.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 18));
        add(heading, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        amountLbl = new JLabel("Total Amount to Pay: ₹" + String.format("%.2f", totalAmount));
        amountLbl.setFont(new Font("Arial", Font.BOLD, 16));
        centerPanel.add(amountLbl);
        centerPanel.add(Box.createVerticalStrut(20));

        cashBtn = new JRadioButton("Cash on Delivery");
        upiBtn = new JRadioButton("UPI Payment");
        cardBtn = new JRadioButton("Credit/Debit Card");

        paymentGroup = new ButtonGroup();
        paymentGroup.add(cashBtn);
        paymentGroup.add(upiBtn);
        paymentGroup.add(cardBtn);

        centerPanel.add(cashBtn);
        centerPanel.add(upiBtn);
        centerPanel.add(cardBtn);
        centerPanel.add(Box.createVerticalStrut(20));

        upiImageLbl = new JLabel();
        upiImageLbl.setVisible(false);
        // Fake placeholder path
        upiImageLbl.setIcon(new ImageIcon("\"C:\\Users\\Sudarshan\\OneDrive\\Documents\\miniprojectphotos\\qr-scan-code_444196-36283.jpg\""));
        centerPanel.add(upiImageLbl);

        cardNumField = new JTextField(16);
        cvvField = new JTextField(3);
        cardNumField.setBorder(BorderFactory.createTitledBorder("Card Number"));
        cvvField.setBorder(BorderFactory.createTitledBorder("CVV"));
        cardNumField.setVisible(false);
        cvvField.setVisible(false);

        centerPanel.add(cardNumField);
        centerPanel.add(cvvField);

        payBtn = new JButton("Pay Now");
        payBtn.setEnabled(false);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(payBtn);

        add(centerPanel, BorderLayout.CENTER);

        cashBtn.addActionListener(this);
        upiBtn.addActionListener(this);
        cardBtn.addActionListener(this);
        payBtn.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == cashBtn || src == upiBtn || src == cardBtn) {
            payBtn.setEnabled(true);
            upiImageLbl.setVisible(upiBtn.isSelected());
            cardNumField.setVisible(cardBtn.isSelected());
            cvvField.setVisible(cardBtn.isSelected());
        }

        if (src == payBtn) {
            processPayment();
        }
    }

    private void processPayment() {
        String method = cashBtn.isSelected() ? "Cash on Delivery" :
                upiBtn.isSelected() ? "UPI Payment" : "Card Payment";

        JOptionPane.showMessageDialog(this,
                "Payment successful via " + method +
                        "!\nAmount: ₹" + String.format("%.2f", totalAmount));

        sendConfirmationEmail(method);
        dispose();
    }

    private void sendConfirmationEmail(String method) {
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/agriconnect", "root", "abc123")) {

            PreparedStatement pst = con.prepareStatement("SELECT email FROM customers WHERE customer_id = ?");
            pst.setInt(1, customerId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String to = rs.getString("email");
                String subject = "Order Payment Confirmation - AgriConnect";
                String body = "Dear Customer,\n\nYour payment of ₹" +
                        String.format("%.2f", totalAmount) +
                        " via " + method + " has been received successfully.\n\nThank you for shopping with AgriConnect!\n\n- AgriConnect Team";

                sendEmail(to, subject, body);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void sendEmail(String to, String subject, String text) {
        final String from = "sudarshan02testing@gmail.com";
        final String password = "yowe loaz trmp pjuw"; // App password

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setText(text);
            Transport.send(message);
            System.out.println("✅ Email sent successfully to " + to);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}

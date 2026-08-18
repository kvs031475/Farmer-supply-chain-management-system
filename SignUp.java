import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Date;

public class SignUp extends JFrame implements ActionListener {

    private JTextField nameField, emailField, phoneField, farmSizeField, pincodeField;
    private JTextArea addressArea;
    private JDateChooser dobChooser;
    private JPasswordField passwordField, confirmPasswordField;
    private JComboBox<String> userTypeCombo, incomeCombo;
    private JButton submitBtn, cancelBtn;

    // Farmer-specific
    private JPanel farmerPanel;

    // Gender
    private JRadioButton maleRadio, femaleRadio;
    private ButtonGroup genderGroup;

    // Distributor-specific
    private JPanel distributorPanel, vehiclePanel;
    private JRadioButton yes2Wheeler, no2Wheeler;
    private ButtonGroup vehicleGroup;
    private JTextField vehicleNumberField, licenceNumberField;

    public SignUp() {
        setTitle("User Registration");
        setSize(600, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("User Sign-Up", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // User Type
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Sign-up as:"), gbc);
        gbc.gridx = 1;
        userTypeCombo = new JComboBox<>(new String[]{"Customer", "Farmer", "Distributor"});
        formPanel.add(userTypeCombo, gbc);
        userTypeCombo.addActionListener(this);

        // Full Name
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);

        // Address
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        addressArea = new JTextArea(3, 20);
        JScrollPane scroll = new JScrollPane(addressArea);
        formPanel.add(scroll, gbc);

        // Pincode
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Pincode:"), gbc);
        gbc.gridx = 1;
        pincodeField = new JTextField(6);
        formPanel.add(pincodeField, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Email ID:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);

        // Phone
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Phone Number:"), gbc);
        gbc.gridx = 1;
        phoneField = new JTextField(15);
        formPanel.add(phoneField, gbc);

        // Gender
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1;
        maleRadio = new JRadioButton("Male");
        femaleRadio = new JRadioButton("Female");
        genderGroup = new ButtonGroup();
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);
        JPanel genderPanel = new JPanel();
        genderPanel.add(maleRadio);
        genderPanel.add(femaleRadio);
        formPanel.add(genderPanel, gbc);

        // DOB
        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("Date of Birth:"), gbc);
        gbc.gridx = 1;
        dobChooser = new JDateChooser();
        dobChooser.setDateFormatString("dd/MM/yyyy");
        dobChooser.setMaxSelectableDate(new Date());
        formPanel.add(dobChooser, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 8;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);

        // Confirm Password
        gbc.gridx = 0; gbc.gridy = 9;
        formPanel.add(new JLabel("Re-enter Password:"), gbc);
        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(20);
        formPanel.add(confirmPasswordField, gbc);

        // Farmer Panel
        farmerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints fgbc = new GridBagConstraints();
        fgbc.insets = new Insets(5, 5, 5, 5);

        fgbc.gridx = 0; fgbc.gridy = 0;
        farmerPanel.add(new JLabel("Annual Income:"), fgbc);
        fgbc.gridx = 1;
        incomeCombo = new JComboBox<>(new String[]{
                "Below 1 Lakh", "2 to 3 Lakh", "3 to 4 Lakh", "4 to 5 Lakh", "Above 5 Lakh"});
        farmerPanel.add(incomeCombo, fgbc);

        fgbc.gridx = 0; fgbc.gridy = 1;
        farmerPanel.add(new JLabel("Farm Size (acres):"), fgbc);
        fgbc.gridx = 1;
        farmSizeField = new JTextField(10);
        farmerPanel.add(farmSizeField, fgbc);

        gbc.gridx = 0; gbc.gridy = 10; gbc.gridwidth = 2;
        formPanel.add(farmerPanel, gbc);
        farmerPanel.setVisible(false);

        // Distributor Panel
        distributorPanel = new JPanel(new GridBagLayout());
        GridBagConstraints dgbc = new GridBagConstraints();
        dgbc.insets = new Insets(5, 5, 5, 5);

        dgbc.gridx = 0; dgbc.gridy = 0;
        distributorPanel.add(new JLabel("Do you have 2-wheeler?"), dgbc);
        dgbc.gridx = 1;
        yes2Wheeler = new JRadioButton("Yes");
        no2Wheeler = new JRadioButton("No");
        vehicleGroup = new ButtonGroup();
        vehicleGroup.add(yes2Wheeler);
        vehicleGroup.add(no2Wheeler);
        distributorPanel.add(yes2Wheeler, dgbc);
        distributorPanel.add(no2Wheeler, dgbc);

        vehiclePanel = new JPanel(new GridBagLayout());
        GridBagConstraints vgbc = new GridBagConstraints();
        vgbc.insets = new Insets(5, 5, 5, 5);

        vgbc.gridx = 0; vgbc.gridy = 0;
        vehiclePanel.add(new JLabel("Vehicle Number Plate:"), vgbc);
        vgbc.gridx = 1;
        vehicleNumberField = new JTextField(15);
        vehiclePanel.add(vehicleNumberField, vgbc);

        vgbc.gridx = 0; vgbc.gridy = 1;
        vehiclePanel.add(new JLabel("Driver Licence No:"), vgbc);
        vgbc.gridx = 1;
        licenceNumberField = new JTextField(15);
        vehiclePanel.add(licenceNumberField, vgbc);

        distributorPanel.add(vehiclePanel, dgbc);
        vehiclePanel.setVisible(false);

        gbc.gridx = 0; gbc.gridy = 11; gbc.gridwidth = 2;
        formPanel.add(distributorPanel, gbc);
        distributorPanel.setVisible(false);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        submitBtn = new JButton("Submit");
        cancelBtn = new JButton("Cancel");
        submitBtn.addActionListener(this);
        cancelBtn.addActionListener(this);
        buttonPanel.add(submitBtn);
        buttonPanel.add(cancelBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        yes2Wheeler.addActionListener(e -> vehiclePanel.setVisible(true));
        no2Wheeler.addActionListener(e -> vehiclePanel.setVisible(false));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitBtn) {
            saveToDatabase();
        } else if (e.getSource() == cancelBtn) {
            dispose();
            new LoginPage().setVisible(true);
        } else if (e.getSource() == userTypeCombo) {
            String type = (String) userTypeCombo.getSelectedItem();
            farmerPanel.setVisible("Farmer".equals(type));
            distributorPanel.setVisible("Distributor".equals(type));
            pack();
        }
    }

    private void saveToDatabase() {
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/agriconnect", "root", "abc123")) {

            String userType = (String) userTypeCombo.getSelectedItem();
            String sql = "";

            if ("Customer".equals(userType)) {
                sql = "INSERT INTO customers (name, address, pincode, email, phone, gender, dob, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            } else if ("Farmer".equals(userType)) {
                sql = "INSERT INTO farmers (name, address, pincode, email, phone, gender, dob, password, income, farmSize) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            } else if ("Distributor".equals(userType)) {
                sql = "INSERT INTO distributors (name, address, pincode, email, phone, gender, dob, password, vehicleNo, licenceNo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            }

            PreparedStatement ps = con.prepareStatement(sql);

            // Common fields
            ps.setString(1, nameField.getText());
            ps.setString(2, addressArea.getText());
            ps.setString(3, pincodeField.getText());
            ps.setString(4, emailField.getText());
            ps.setString(5, phoneField.getText());
            ps.setString(6, maleRadio.isSelected() ? "Male" : "Female");
            ps.setDate(7, new java.sql.Date(dobChooser.getDate().getTime()));
            ps.setString(8, new String(passwordField.getPassword()));

            if ("Farmer".equals(userType)) {
                ps.setString(9, (String) incomeCombo.getSelectedItem());
                ps.setString(10, farmSizeField.getText());
            } else if ("Distributor".equals(userType)) {
                ps.setString(9, vehicleNumberField.getText());
                ps.setString(10, licenceNumberField.getText());
            }

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, userType + " registered successfully!");

            new LoginPage().setVisible(true);
            dispose();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
        }
    }

    // ✅ Modified method to set default user type
    public void setDefaultUserType(String defaultType) {
        userTypeCombo.setSelectedItem(defaultType);
        userTypeCombo.setEnabled(false); // Prevent changing
        if ("Farmer".equals(defaultType)) {
            farmerPanel.setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SignUp().setVisible(true));
    }
}

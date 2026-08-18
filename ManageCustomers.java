import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ManageCustomers extends JFrame implements ActionListener {

    private JTable table;
    private DefaultTableModel model;
    private JButton addBtn, editBtn, deleteBtn, refreshBtn, backBtn;

    public ManageCustomers() {
        setTitle("Manage Customers");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[]{"ID", "Name", "Email", "Phone", "Address"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel panel = new JPanel();
        addBtn = new JButton("Add");
        editBtn = new JButton("Edit");
        deleteBtn = new JButton("Delete");
        refreshBtn = new JButton("Refresh");
        backBtn = new JButton("Back");

        panel.add(addBtn);
        panel.add(editBtn);
        panel.add(deleteBtn);
        panel.add(refreshBtn);
        panel.add(backBtn);
        add(panel, BorderLayout.SOUTH);

        addBtn.addActionListener(this);
        editBtn.addActionListener(this);
        deleteBtn.addActionListener(this);
        refreshBtn.addActionListener(this);
        backBtn.addActionListener(this);

        loadData();
    }

    private void loadData() {
        model.setRowCount(0);
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/agriconnect", "root", "abc123")) {
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM customers");
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("customer_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == addBtn) {
                // ✅ Open SignUp page with default user type as "Customer"
                SignUp signUpPage = new SignUp();
                signUpPage.setVisible(true);

                // Force user type to "Customer" and disable the combo box
                signUpPage.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                JComboBox<String> userTypeCombo = getUserTypeCombo(signUpPage);
                if (userTypeCombo != null) {
                    userTypeCombo.setSelectedItem("Customer");
                    userTypeCombo.setEnabled(false);
                }

                // Close current page if you want to switch focus
                // dispose();

            } else if (e.getSource() == editBtn) {
                int row = table.getSelectedRow();
                if (row == -1) return;
                int id = (int) model.getValueAt(row, 0);

                JTextField name = new JTextField((String) model.getValueAt(row, 1));
                JTextField email = new JTextField((String) model.getValueAt(row, 2));
                JTextField phone = new JTextField((String) model.getValueAt(row, 3));
                JTextField address = new JTextField((String) model.getValueAt(row, 4));
                Object[] fields = {"Name:", name, "Email:", email, "Phone:", phone, "Address:", address};
                int option = JOptionPane.showConfirmDialog(this, fields, "Edit Customer", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    try (Connection con = DriverManager.getConnection(
                            "jdbc:mysql://localhost:3306/agriconnect", "root", "abc123")) {
                        PreparedStatement pst = con.prepareStatement(
                                "UPDATE customers SET name=?, email=?, phone=?, address=? WHERE customer_id=?");
                        pst.setString(1, name.getText());
                        pst.setString(2, email.getText());
                        pst.setString(3, phone.getText());
                        pst.setString(4, address.getText());
                        pst.setInt(5, id);
                        pst.executeUpdate();
                        loadData();
                    }
                }

            } else if (e.getSource() == deleteBtn) {
                int row = table.getSelectedRow();
                if (row == -1) return;
                int id = (int) model.getValueAt(row, 0);
                int option = JOptionPane.showConfirmDialog(this, "Delete this customer?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    try (Connection con = DriverManager.getConnection(
                            "jdbc:mysql://localhost:3306/agriconnect", "root", "abc123")) {
                        con.createStatement().executeUpdate("DELETE FROM customers WHERE customer_id=" + id);
                        loadData();
                    }
                }

            } else if (e.getSource() == refreshBtn) {
                loadData();

            } else if (e.getSource() == backBtn) {
                new AdminDashboard().setVisible(true);
                dispose();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ✅ Helper method to access the private userTypeCombo from SignUp class using reflection
    private JComboBox<String> getUserTypeCombo(SignUp signUpPage) {
        try {
            java.lang.reflect.Field field = SignUp.class.getDeclaredField("userTypeCombo");
            field.setAccessible(true);
            return (JComboBox<String>) field.get(signUpPage);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ManageCustomers().setVisible(true));
    }
}

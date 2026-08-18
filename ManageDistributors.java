import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class ManageDistributors extends JFrame implements ActionListener {

    private JTable table;
    private DefaultTableModel model;
    private JButton addBtn, editBtn, deleteBtn, refreshBtn, backBtn;

    public ManageDistributors() {
        setTitle("Manage Distributors");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[]{
                "ID", "Name", "Email", "Phone", "Address", "Pincode", "Gender", "DOB", "Vehicle No", "Licence No"
        }, 0);

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

            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM distributors");
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("distributor_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getString("pincode"),
                        rs.getString("gender"),
                        rs.getDate("dob") != null ? df.format(rs.getDate("dob")) : "",
                        rs.getString("vehicleNo"),
                        rs.getString("licenceNo")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == addBtn) {
                openSignUpAsDistributor();
            } else if (e.getSource() == editBtn) {
                editDistributor();
            } else if (e.getSource() == deleteBtn) {
                deleteDistributor();
            } else if (e.getSource() == refreshBtn) {
                loadData();
            } else if (e.getSource() == backBtn) {
                new AdminDashboard().setVisible(true);
                dispose();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // 🟢 NEW METHOD: open SignUp page with Distributor pre-selected
    private void openSignUpAsDistributor() {
        SignUp signup = new SignUp();
        signup.setVisible(true);

        // Pre-select "Distributor" in combo box
        JComboBox<String> userTypeCombo = getUserTypeCombo(signup);
        if (userTypeCombo != null) {
            userTypeCombo.setSelectedItem("Distributor");
            userTypeCombo.setEnabled(false); // lock user type
        }

        dispose(); // close current window
    }

    // Utility to access private userTypeCombo from SignUp class via reflection
    private JComboBox<String> getUserTypeCombo(SignUp signup) {
        try {
            java.lang.reflect.Field field = SignUp.class.getDeclaredField("userTypeCombo");
            field.setAccessible(true);
            return (JComboBox<String>) field.get(signup);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private void editDistributor() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a distributor to edit!");
            return;
        }

        int id = (int) model.getValueAt(row, 0);

        JTextField name = new JTextField((String) model.getValueAt(row, 1));
        JTextField email = new JTextField((String) model.getValueAt(row, 2));
        JTextField phone = new JTextField((String) model.getValueAt(row, 3));
        JTextField address = new JTextField((String) model.getValueAt(row, 4));
        JTextField pincode = new JTextField((String) model.getValueAt(row, 5));
        JComboBox<String> gender = new JComboBox<>(new String[]{"Male", "Female"});
        gender.setSelectedItem(model.getValueAt(row, 6));
        JTextField dob = new JTextField((String) model.getValueAt(row, 7));
        JTextField vehicle = new JTextField((String) model.getValueAt(row, 8));
        JTextField licence = new JTextField((String) model.getValueAt(row, 9));

        Object[] fields = {
                "Name:", name,
                "Email:", email,
                "Phone:", phone,
                "Address:", address,
                "Pincode:", pincode,
                "Gender:", gender,
                "DOB (yyyy-mm-dd):", dob,
                "Vehicle No:", vehicle,
                "Licence No:", licence
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Edit Distributor", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try (Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/agriconnect", "root", "abc123")) {

                PreparedStatement pst = con.prepareStatement(
                        "UPDATE distributors SET name=?, address=?, pincode=?, email=?, phone=?, gender=?, dob=?, vehicleNo=?, licenceNo=? WHERE distributor_id=?");

                pst.setString(1, name.getText());
                pst.setString(2, address.getText());
                pst.setString(3, pincode.getText());
                pst.setString(4, email.getText());
                pst.setString(5, phone.getText());
                pst.setString(6, gender.getSelectedItem().toString());
                pst.setDate(7, Date.valueOf(dob.getText()));
                pst.setString(8, vehicle.getText());
                pst.setString(9, licence.getText());
                pst.setInt(10, id);

                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Distributor updated successfully!");
                loadData();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error updating distributor: " + ex.getMessage());
            }
        }
    }

    private void deleteDistributor() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a distributor to delete!");
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        int option = JOptionPane.showConfirmDialog(this, "Delete this distributor?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            try (Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/agriconnect", "root", "abc123")) {
                PreparedStatement pst = con.prepareStatement("DELETE FROM distributors WHERE distributor_id=?");
                pst.setInt(1, id);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Distributor deleted successfully!");
                loadData();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting distributor: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ManageDistributors().setVisible(true));
    }
}

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ManageFarmers extends JFrame implements ActionListener {

    private JTable table;
    private DefaultTableModel model;
    private JButton addBtn, editBtn, deleteBtn, refreshBtn, backBtn;

    public ManageFarmers() {
        setTitle("Manage Farmers");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Table setup
        model = new DefaultTableModel(new String[]{
                "ID", "Name", "Email", "Phone", "Address", "Pincode", "Gender", "DOB", "Income", "Farm Size"
        }, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Buttons panel
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

        // Action listeners
        addBtn.addActionListener(this);
        editBtn.addActionListener(this);
        deleteBtn.addActionListener(this);
        refreshBtn.addActionListener(this);
        backBtn.addActionListener(this);

        loadData();
    }

    // Load data from database
    private void loadData() {
        model.setRowCount(0);
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/agriconnect", "root", "abc123")) {

            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM farmers");
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("farmer_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getString("pincode"),
                        rs.getString("gender"),
                        rs.getDate("dob") != null ? rs.getDate("dob").toString() : "",
                        rs.getString("income"),
                        rs.getString("farmSize")
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
                // Open SignUp page with Farmer pre-selected
                SignUp signup = new SignUp();
                signup.setDefaultUserType("Farmer"); // Pre-select user type as Farmer
                signup.setVisible(true);
            } else if (e.getSource() == editBtn) {
                editFarmer();
            } else if (e.getSource() == deleteBtn) {
                deleteFarmer();
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

    // Edit selected farmer
    private void editFarmer() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a farmer to edit!");
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
        JTextField income = new JTextField((String) model.getValueAt(row, 8));
        JTextField farmSize = new JTextField((String) model.getValueAt(row, 9));

        Object[] fields = {
                "Name:", name,
                "Email:", email,
                "Phone:", phone,
                "Address:", address,
                "Pincode:", pincode,
                "Gender:", gender,
                "DOB (yyyy-mm-dd):", dob,
                "Income:", income,
                "Farm Size:", farmSize
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Edit Farmer", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try (Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/agriconnect", "root", "abc123")) {

                PreparedStatement pst = con.prepareStatement(
                        "UPDATE farmers SET name=?, email=?, phone=?, address=?, pincode=?, gender=?, dob=?, income=?, farmSize=? WHERE farmer_id=?");

                pst.setString(1, name.getText());
                pst.setString(2, email.getText());
                pst.setString(3, phone.getText());
                pst.setString(4, address.getText());
                pst.setString(5, pincode.getText());
                pst.setString(6, gender.getSelectedItem().toString());
                pst.setDate(7, Date.valueOf(dob.getText()));
                pst.setString(8, income.getText());
                pst.setString(9, farmSize.getText());
                pst.setInt(10, id);

                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Farmer updated successfully!");
                loadData();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error updating farmer: " + ex.getMessage());
            }
        }
    }

    // Delete selected farmer
    private void deleteFarmer() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a farmer to delete!");
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        int option = JOptionPane.showConfirmDialog(this, "Delete this farmer?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            try (Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/agriconnect", "root", "abc123")) {
                PreparedStatement pst = con.prepareStatement("DELETE FROM farmers WHERE farmer_id=?");
                pst.setInt(1, id);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Farmer deleted successfully!");
                loadData();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting farmer: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ManageFarmers().setVisible(true));
    }
}

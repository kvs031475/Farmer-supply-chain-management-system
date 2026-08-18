import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ManageProducts extends JFrame implements ActionListener {

    private JTable table;
    private DefaultTableModel model;
    private JButton addBtn, editBtn, deleteBtn, refreshBtn, backBtn;

    public ManageProducts() {
        setTitle("Manage Products");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[]{"ID", "Farmer ID", "Name", "Price", "Quantity"}, 0);
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
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM farmer_products");
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("product_id"),
                        rs.getInt("farmer_id"),
                        rs.getString("product_name"),
                        rs.getDouble("price"),
                        rs.getInt("quantity")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == addBtn) {
                JTextField farmerId = new JTextField();
                JTextField name = new JTextField();
                JTextField price = new JTextField();
                JTextField quantity = new JTextField();
                Object[] fields = {"Farmer ID:", farmerId, "Product Name:", name, "Price:", price, "Quantity:", quantity};
                int option = JOptionPane.showConfirmDialog(this, fields, "Add Product", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    try (Connection con = DriverManager.getConnection(
                            "jdbc:mysql://localhost:3306/agriconnect", "root", "abc123")) {
                        PreparedStatement pst = con.prepareStatement(
                                "INSERT INTO farmer_products(farmer_id,product_name,price,quantity) VALUES(?,?,?,?)");
                        pst.setInt(1, Integer.parseInt(farmerId.getText()));
                        pst.setString(2, name.getText());
                        pst.setDouble(3, Double.parseDouble(price.getText()));
                        pst.setInt(4, Integer.parseInt(quantity.getText()));
                        pst.executeUpdate();
                        loadData();
                    }
                }
            } else if (e.getSource() == editBtn) {
                int row = table.getSelectedRow();
                if (row == -1) return;
                int id = (int) model.getValueAt(row, 0);

                JTextField farmerId = new JTextField(String.valueOf(model.getValueAt(row, 1)));
                JTextField name = new JTextField((String) model.getValueAt(row, 2));
                JTextField price = new JTextField(String.valueOf(model.getValueAt(row, 3)));
                JTextField quantity = new JTextField(String.valueOf(model.getValueAt(row, 4)));
                Object[] fields = {"Farmer ID:", farmerId, "Product Name:", name, "Price:", price, "Quantity:", quantity};
                int option = JOptionPane.showConfirmDialog(this, fields, "Edit Product", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    try (Connection con = DriverManager.getConnection(
                            "jdbc:mysql://localhost:3306/agriconnect", "root", "abc123")) {
                        PreparedStatement pst = con.prepareStatement(
                                "UPDATE farmer_products SET farmer_id=?, product_name=?, price=?, quantity=? WHERE product_id=?");
                        pst.setInt(1, Integer.parseInt(farmerId.getText()));
                        pst.setString(2, name.getText());
                        pst.setDouble(3, Double.parseDouble(price.getText()));
                        pst.setInt(4, Integer.parseInt(quantity.getText()));
                        pst.setInt(5, id);
                        pst.executeUpdate();
                        loadData();
                    }
                }
            } else if (e.getSource() == deleteBtn) {
                int row = table.getSelectedRow();
                if (row == -1) return;
                int id = (int) model.getValueAt(row, 0);
                int option = JOptionPane.showConfirmDialog(this, "Delete this product?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    try (Connection con = DriverManager.getConnection(
                            "jdbc:mysql://localhost:3306/agriconnect", "root", "abc123")) {
                        con.createStatement().executeUpdate("DELETE FROM farmer_products WHERE product_id=" + id);
                        loadData();
                    }
                }
            } else if (e.getSource() == refreshBtn) {
                loadData();
            } else if (e.getSource() == backBtn) {
                new AdminDashboard().setVisible(true);
                dispose();
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}

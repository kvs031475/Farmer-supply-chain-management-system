import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FarmerDashboard extends JFrame implements ActionListener {

    private int farmerId;
    private JTable productTable;
    private DefaultTableModel tableModel;

    private JTextField nameField, priceField, quantityField;
    private JTextArea descArea;
    private JLabel imageLabel;
    private JButton addBtn, updateBtn, deleteBtn, chooseImageBtn;

    private String imagePath = "";

    public FarmerDashboard(int farmerId) {
        this.farmerId = farmerId;

        setTitle("Farmer Dashboard - AgriConnect");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Price", "Quantity", "Description", "Image Path"}, 0);
        productTable = new JTable(tableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScroll = new JScrollPane(productTable);
        add(tableScroll, BorderLayout.CENTER);

        productTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = productTable.getSelectedRow();
                if (row >= 0) {
                    nameField.setText(tableModel.getValueAt(row, 1).toString());
                    priceField.setText(tableModel.getValueAt(row, 2).toString());
                    quantityField.setText(tableModel.getValueAt(row, 3).toString());
                    descArea.setText(tableModel.getValueAt(row, 4).toString());
                    imagePath = tableModel.getValueAt(row, 5).toString();
                    imageLabel.setIcon(new ImageIcon(new ImageIcon(imagePath).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
                }
            }
        });

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setPreferredSize(new Dimension(350, getHeight()));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Product Name:"), gbc);
        gbc.gridx = 1; nameField = new JTextField(15); formPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Price per unit:"), gbc);
        gbc.gridx = 1; priceField = new JTextField(10); formPanel.add(priceField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1; quantityField = new JTextField(10); formPanel.add(quantityField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; descArea = new JTextArea(3, 15);
        descArea.setLineWrap(true); descArea.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(descArea), gbc);

        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Image:"), gbc);
        gbc.gridx = 1; imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(100, 100));
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        formPanel.add(imageLabel, gbc);

        gbc.gridy++;
        chooseImageBtn = new JButton("Choose Image");
        chooseImageBtn.addActionListener(this);
        formPanel.add(chooseImageBtn, gbc);

        gbc.gridy++;
        addBtn = new JButton("Add"); updateBtn = new JButton("Update"); deleteBtn = new JButton("Delete");
        addBtn.addActionListener(this); updateBtn.addActionListener(this); deleteBtn.addActionListener(this);
        JPanel btnPanel = new JPanel(); btnPanel.add(addBtn); btnPanel.add(updateBtn); btnPanel.add(deleteBtn);
        gbc.gridwidth = 2; formPanel.add(btnPanel, gbc);

        add(formPanel, BorderLayout.EAST);

        loadProducts();
    }

    private void loadProducts() {
        tableModel.setRowCount(0);
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/agriconnect", "root", "abc123")) {
            String query = "SELECT * FROM farmer_products WHERE farmer_id=?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, farmerId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getDouble("price"),
                        rs.getInt("quantity"),
                        rs.getString("description"),
                        rs.getString("image_path")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == chooseImageBtn) chooseImage();
        else if(e.getSource() == addBtn) addProduct();
        else if(e.getSource() == updateBtn) updateProduct();
        else if(e.getSource() == deleteBtn) deleteProduct();
    }

    private void chooseImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "png", "jpeg"));
        int option = chooser.showOpenDialog(this);
        if(option == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            imagePath = file.getAbsolutePath();
            imageLabel.setIcon(new ImageIcon(new ImageIcon(imagePath).getImage().getScaledInstance(100,100,Image.SCALE_SMOOTH)));
        }
    }

    private void addProduct() {
        try {
            String name = nameField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            int qty = Integer.parseInt(quantityField.getText().trim());
            String desc = descArea.getText().trim();
            if(name.isEmpty() || imagePath.isEmpty()) { JOptionPane.showMessageDialog(this, "Fill all fields"); return; }

            try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/agriconnect", "root", "abc123")) {
                String query = "INSERT INTO farmer_products(farmer_id, product_name, price, quantity, description, image_path) VALUES(?,?,?,?,?,?)";
                PreparedStatement pst = con.prepareStatement(query);
                pst.setInt(1, farmerId); pst.setString(2, name); pst.setDouble(3, price);
                pst.setInt(4, qty); pst.setString(5, desc); pst.setString(6, imagePath);
                pst.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, "Product Added!");
            loadProducts(); clearForm();
        } catch(Exception ex) { ex.printStackTrace(); }
    }

    private void updateProduct() {
        int row = productTable.getSelectedRow();
        if(row < 0) { JOptionPane.showMessageDialog(this,"Select a product"); return; }
        try {
            int productId = (int)tableModel.getValueAt(row, 0);
            String name = nameField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            int qty = Integer.parseInt(quantityField.getText().trim());
            String desc = descArea.getText().trim();

            try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/agriconnect", "root", "abc123")) {
                String query = "UPDATE farmer_products SET product_name=?, price=?, quantity=?, description=?, image_path=? WHERE product_id=?";
                PreparedStatement pst = con.prepareStatement(query);
                pst.setString(1, name); pst.setDouble(2, price); pst.setInt(3, qty);
                pst.setString(4, desc); pst.setString(5, imagePath); pst.setInt(6, productId);
                pst.executeUpdate();
            }
            JOptionPane.showMessageDialog(this,"Product Updated!");
            loadProducts(); clearForm();
        } catch(Exception ex){ ex.printStackTrace(); }
    }

    private void deleteProduct() {
        int row = productTable.getSelectedRow();
        if(row < 0) { JOptionPane.showMessageDialog(this,"Select a product"); return; }
        int productId = (int)tableModel.getValueAt(row,0);
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/agriconnect", "root", "abc123")) {
            String query = "DELETE FROM farmer_products WHERE product_id=?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, productId); pst.executeUpdate();
        } catch(Exception ex){ ex.printStackTrace(); }
        JOptionPane.showMessageDialog(this,"Product Deleted!"); loadProducts(); clearForm();
    }

    private void clearForm() { nameField.setText(""); priceField.setText(""); quantityField.setText(""); descArea.setText(""); imageLabel.setIcon(null); imagePath=""; }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FarmerLogin().setVisible(true));
    }
}

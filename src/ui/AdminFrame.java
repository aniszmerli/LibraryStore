package ui;

import dao.OrderDAO;
import dao.ProductDAO;
import models.Product;
import models.User;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class AdminFrame extends JFrame {

    private final User adminUser;
    private final ProductDAO productDAO = new ProductDAO();
    private final OrderDAO orderDAO = new OrderDAO();

    private JTable productTable, orderTable;
    private DefaultTableModel productModel, orderModel;
    private JTabbedPane tabs;

    // Form fields
    private JTextField fName, fDesc, fPrice, fStock;
    private JComboBox<String> fCategory;

    public AdminFrame(User user) {
        this.adminUser = user;
        setTitle("Library Store — Admin Dashboard");
        setSize(1000, 660);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // ── TOP BAR ──────────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(0x1A252F));
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel lblTitle = new JLabel("📚 Library Store — Admin Panel");
        lblTitle.setFont(new Font("Georgia", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);

        JPanel rightBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightBar.setOpaque(false);

        JLabel lblAdmin = new JLabel("Logged in as: " + adminUser.getUsername() + " (Admin)");
        lblAdmin.setForeground(new Color(0xF39C12));
        lblAdmin.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setBackground(new Color(0xE74C3C));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });

        rightBar.add(lblAdmin);
        rightBar.add(btnLogout);
        topBar.add(lblTitle, BorderLayout.WEST);
        topBar.add(rightBar, BorderLayout.EAST);

        // ── TABS ─────────────────────────────────────────────────
        tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabs.addTab("📦 Products", buildProductsTab());
        tabs.addTab("📋 Orders", buildOrdersTab());

        add(topBar, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    // ── PRODUCTS TAB ─────────────────────────────────────────────
    private JPanel buildProductsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(0xF4F6F7));

        // Table
        String[] cols = {"ID", "Name", "Category", "Price", "Stock", "Description"};
        productModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        productTable = new JTable(productModel);
        productTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        productTable.setRowHeight(28);
        productTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        productTable.getTableHeader().setBackground(new Color(0x1A252F));
        productTable.getTableHeader().setForeground(Color.WHITE);
        productTable.setSelectionBackground(new Color(0xFAD7A0));
        productTable.setGridColor(new Color(0xE5E8E8));

        int[] widths = {40, 200, 100, 80, 70, 260};
        for (int i = 0; i < widths.length; i++)
            productTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JScrollPane scroll = new JScrollPane(productTable);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0xBDC3C7)), "Product Details",
            javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12), new Color(0x2C3E50)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.anchor = GridBagConstraints.WEST;

        String[] lbls = {"Name:", "Description:", "Price:", "Stock:", "Category:"};
        fName     = new JTextField(18);
        fDesc     = new JTextField(18);
        fPrice    = new JTextField(18);
        fStock    = new JTextField(18);
        fCategory = new JComboBox<>(new String[]{"book", "notebook", "pen", "stationery", "other"});
        JComponent[] fields = {fName, fDesc, fPrice, fStock, fCategory};

        for (int i = 0; i < lbls.length; i++) {
            gbc.gridy = i; gbc.gridx = 0;
            formPanel.add(new JLabel(lbls[i]), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            formPanel.add(fields[i], gbc);
            gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        }

        // Action buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        btnRow.setOpaque(false);

        JButton btnRefresh = createAdminBtn("↻ Refresh", new Color(0x5DADE2));
        JButton btnAdd     = createAdminBtn("➕ Add", new Color(0x27AE60));
        JButton btnEdit    = createAdminBtn("✏ Update", new Color(0xF39C12));
        JButton btnDelete  = createAdminBtn("🗑 Delete", new Color(0xE74C3C));
        JButton btnClear   = createAdminBtn("Clear Form", new Color(0x95A5A6));

        btnRow.add(btnRefresh);
        btnRow.add(btnAdd);
        btnRow.add(btnEdit);
        btnRow.add(btnDelete);
        btnRow.add(btnClear);

        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 5, 5, 5);
        formPanel.add(btnRow, gbc);

        // Side panel
        JPanel sidePanel = new JPanel(new BorderLayout());
        sidePanel.setPreferredSize(new Dimension(300, 0));
        sidePanel.add(formPanel, BorderLayout.CENTER);

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(sidePanel, BorderLayout.EAST);

        // Listeners
        refreshProducts();

        productTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) populateFormFromTable();
        });

        btnRefresh.addActionListener(e -> refreshProducts());
        btnClear.addActionListener(e -> clearForm());
        btnAdd.addActionListener(e -> addProduct());
        btnEdit.addActionListener(e -> updateProduct());
        btnDelete.addActionListener(e -> deleteProduct());

        return panel;
    }

    // ── ORDERS TAB ───────────────────────────────────────────────
    private JPanel buildOrdersTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(0xF4F6F7));

        String[] cols = {"Order ID", "Customer", "Total", "Status", "Date"};
        orderModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        orderTable = new JTable(orderModel);
        orderTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        orderTable.setRowHeight(28);
        orderTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        orderTable.getTableHeader().setBackground(new Color(0x1A252F));
        orderTable.getTableHeader().setForeground(Color.WHITE);
        orderTable.setSelectionBackground(new Color(0xD6EAF8));
        orderTable.setGridColor(new Color(0xE5E8E8));

        int[] widths = {80, 150, 100, 100, 200};
        for (int i = 0; i < widths.length; i++)
            orderTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JScrollPane scroll = new JScrollPane(orderTable);

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topRow.setOpaque(false);
        JButton btnRefreshOrders = createAdminBtn("↻ Refresh Orders", new Color(0x5DADE2));
        btnRefreshOrders.addActionListener(e -> refreshOrders());
        topRow.add(btnRefreshOrders);

        panel.add(topRow, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        refreshOrders();
        return panel;
    }

    // ── CRUD OPERATIONS ──────────────────────────────────────────

    private void refreshProducts() {
        productModel.setRowCount(0);
        for (Product p : productDAO.getAllProducts()) {
            productModel.addRow(new Object[]{
                p.getId(), p.getName(), p.getCategory(),
                String.format("$%.2f", p.getPrice()), p.getStock(), p.getDescription()
            });
        }
    }

    private void refreshOrders() {
        orderModel.setRowCount(0);
        for (String[] row : orderDAO.getAllOrders()) {
            orderModel.addRow(row);
        }
    }

    private void populateFormFromTable() {
        int row = productTable.getSelectedRow();
        if (row == -1) return;
        fName.setText((String) productModel.getValueAt(row, 1));
        fCategory.setSelectedItem(productModel.getValueAt(row, 2));
        fPrice.setText(productModel.getValueAt(row, 3).toString().replace("$", ""));
        fStock.setText(productModel.getValueAt(row, 4).toString());
        fDesc.setText((String) productModel.getValueAt(row, 5));
    }

    private void clearForm() {
        fName.setText(""); fDesc.setText(""); fPrice.setText("");
        fStock.setText(""); fCategory.setSelectedIndex(0);
        productTable.clearSelection();
    }

    private void addProduct() {
        Product p = buildProductFromForm();
        if (p == null) return;
        if (productDAO.addProduct(p)) {
            JOptionPane.showMessageDialog(this, "Product added successfully!");
            clearForm(); refreshProducts();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add product.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateProduct() {
        int row = productTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a product to update."); return; }
        Product p = buildProductFromForm();
        if (p == null) return;
        p.setId((int) productModel.getValueAt(row, 0));
        if (productDAO.updateProduct(p)) {
            JOptionPane.showMessageDialog(this, "Product updated successfully!");
            clearForm(); refreshProducts();
        } else {
            JOptionPane.showMessageDialog(this, "Update failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteProduct() {
        int row = productTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a product to delete."); return; }
        int id = (int) productModel.getValueAt(row, 0);
        String name = (String) productModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete \"" + name + "\"?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        if (productDAO.deleteProduct(id)) {
            JOptionPane.showMessageDialog(this, "Product deleted.");
            clearForm(); refreshProducts();
        } else {
            JOptionPane.showMessageDialog(this, "Delete failed. Product may be part of an order.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Product buildProductFromForm() {
        String name = fName.getText().trim();
        String desc = fDesc.getText().trim();
        String priceStr = fPrice.getText().trim();
        String stockStr = fStock.getText().trim();
        String cat = (String) fCategory.getSelectedItem();

        if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name, price, and stock are required.");
            return null;
        }

        double price; int stock;
        try { price = Double.parseDouble(priceStr); }
        catch (NumberFormatException e) { JOptionPane.showMessageDialog(this, "Invalid price."); return null; }
        try { stock = Integer.parseInt(stockStr); }
        catch (NumberFormatException e) { JOptionPane.showMessageDialog(this, "Invalid stock."); return null; }

        Product p = new Product();
        p.setName(name); p.setDescription(desc);
        p.setPrice(price); p.setStock(stock); p.setCategory(cat);
        return p;
    }

    private JButton createAdminBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(bg.darker()); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(bg); }
        });
        return btn;
    }
}

package ui;

import dao.ProductDAO;
import models.OrderItem;
import models.Product;
import models.User;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class HomeFrame extends JFrame {

    private final User currentUser;
    private final List<OrderItem> cart = new ArrayList<>();

    private JTable productTable;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JComboBox<String> cmbCategory;
    private JLabel lblCartCount;

    private final ProductDAO productDAO = new ProductDAO();

    public HomeFrame(User user) {
        this.currentUser = user;
        setTitle("Library Store — Catalog");
        setSize(900, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        loadProducts(productDAO.getAllProducts());
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // ── TOP BAR ──────────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(0x2C3E50));
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel lblLogo = new JLabel("📚 Library Store");
        lblLogo.setFont(new Font("Georgia", Font.BOLD, 20));
        lblLogo.setForeground(Color.WHITE);

        JPanel rightBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightBar.setOpaque(false);

        JLabel lblUser = new JLabel("Hello, " + currentUser.getFullName());
        lblUser.setForeground(new Color(0xBDC3C7));
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JButton btnCart = createTopBtn("🛒 Cart (0)");
        lblCartCount = new JLabel("0");
        btnCart.setText("🛒 Cart (0)");
        btnCart.addActionListener(e -> openCart());

        JButton btnOrders = createTopBtn("📋 My Orders");
        btnOrders.addActionListener(e -> openOrders());

        JButton btnLogout = createTopBtn("Logout");
        btnLogout.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });

        rightBar.add(lblUser);
        rightBar.add(btnCart);
        rightBar.add(btnOrders);
        rightBar.add(btnLogout);

        topBar.add(lblLogo, BorderLayout.WEST);
        topBar.add(rightBar, BorderLayout.EAST);

        // ── SEARCH/FILTER BAR ────────────────────────────────────
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        filterBar.setBackground(new Color(0xECF0F1));
        filterBar.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        txtSearch = new JTextField(20);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xBDC3C7)),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));

        JButton btnSearch = new JButton("🔍 Search");
        btnSearch.setBackground(new Color(0x3498DB));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSearch.setFocusPainted(false);
        btnSearch.setBorderPainted(false);
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));

        String[] cats = {"All Categories", "book", "notebook", "pen", "stationery", "other"};
        cmbCategory = new JComboBox<>(cats);
        cmbCategory.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JButton btnFilter = new JButton("Filter");
        btnFilter.setBackground(new Color(0x8E44AD));
        btnFilter.setForeground(Color.WHITE);
        btnFilter.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnFilter.setFocusPainted(false);
        btnFilter.setBorderPainted(false);
        btnFilter.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnAddToCart = new JButton("➕ Add to Cart");
        btnAddToCart.setBackground(new Color(0x27AE60));
        btnAddToCart.setForeground(Color.WHITE);
        btnAddToCart.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAddToCart.setFocusPainted(false);
        btnAddToCart.setBorderPainted(false);
        btnAddToCart.setCursor(new Cursor(Cursor.HAND_CURSOR));

        filterBar.add(new JLabel("Search:"));
        filterBar.add(txtSearch);
        filterBar.add(btnSearch);
        filterBar.add(Box.createHorizontalStrut(10));
        filterBar.add(new JLabel("Category:"));
        filterBar.add(cmbCategory);
        filterBar.add(btnFilter);
        filterBar.add(Box.createHorizontalStrut(20));
        filterBar.add(btnAddToCart);

        // ── PRODUCT TABLE ────────────────────────────────────────
        String[] cols = {"ID", "Product Name", "Category", "Price", "In Stock", "Description"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        productTable = new JTable(tableModel);
        productTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        productTable.setRowHeight(30);
        productTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        productTable.getTableHeader().setBackground(new Color(0x2C3E50));
        productTable.getTableHeader().setForeground(Color.WHITE);
        productTable.setSelectionBackground(new Color(0xD6EAF8));
        productTable.setSelectionForeground(Color.BLACK);
        productTable.setGridColor(new Color(0xECF0F1));

        // Column widths
        int[] widths = {40, 200, 100, 80, 80, 250};
        for (int i = 0; i < widths.length; i++)
            productTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // ── BOTTOM STATUS BAR ────────────────────────────────────
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(0x2C3E50));
        statusBar.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
        JLabel lblStatus = new JLabel("Browse our collection — select a product and click Add to Cart");
        lblStatus.setForeground(new Color(0xBDC3C7));
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        statusBar.add(lblStatus, BorderLayout.WEST);

        // Assemble
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(filterBar, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(topBar, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);

        // ── LISTENERS ────────────────────────────────────────────
        btnSearch.addActionListener(e -> {
            String kw = txtSearch.getText().trim();
            if (kw.isEmpty()) loadProducts(productDAO.getAllProducts());
            else loadProducts(productDAO.searchProducts(kw));
        });

        btnFilter.addActionListener(e -> {
            String cat = (String) cmbCategory.getSelectedItem();
            if ("All Categories".equals(cat)) loadProducts(productDAO.getAllProducts());
            else loadProducts(productDAO.getByCategory(cat));
        });

        btnAddToCart.addActionListener(e -> addSelectedToCart(btnCart));

        txtSearch.addActionListener(e -> btnSearch.doClick());
    }

    private void loadProducts(List<Product> products) {
        tableModel.setRowCount(0);
        for (Product p : products) {
            tableModel.addRow(new Object[]{
                p.getId(),
                p.getName(),
                p.getCategory(),
                String.format("$%.2f", p.getPrice()),
                p.getStock(),
                p.getDescription()
            });
        }
    }

    private void addSelectedToCart(JButton btnCart) {
        int row = productTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int productId = (int) tableModel.getValueAt(row, 0);
        String name   = (String) tableModel.getValueAt(row, 1);
        int stock     = (int) tableModel.getValueAt(row, 4);
        String priceStr = tableModel.getValueAt(row, 3).toString().replace("$", "");
        double price  = Double.parseDouble(priceStr);

        if (stock == 0) {
            JOptionPane.showMessageDialog(this, "This product is out of stock.", "Out of Stock", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String qtyStr = JOptionPane.showInputDialog(this, "How many copies of \"" + name + "\"?", "1");
        if (qtyStr == null) return;

        int qty;
        try { qty = Integer.parseInt(qtyStr.trim()); }
        catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Invalid quantity."); return; }

        if (qty < 1 || qty > stock) {
            JOptionPane.showMessageDialog(this, "Quantity must be between 1 and " + stock + ".");
            return;
        }

        Product product = new Product(productId, name, "", price, stock, "");

        // Check if already in cart
        for (OrderItem item : cart) {
            if (item.getProduct().getId() == productId) {
                item.setQuantity(item.getQuantity() + qty);
                updateCartButton(btnCart);
                JOptionPane.showMessageDialog(this, "Updated quantity in cart ✓");
                return;
            }
        }

        cart.add(new OrderItem(product, qty));
        updateCartButton(btnCart);
        JOptionPane.showMessageDialog(this, "\"" + name + "\" added to cart! ✓", "Added", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateCartButton(JButton btn) {
        int total = cart.stream().mapToInt(OrderItem::getQuantity).sum();
        btn.setText("🛒 Cart (" + total + ")");
    }

    private void openCart() {
        new CartFrame(currentUser, cart).setVisible(true);
    }

    private void openOrders() {
        new OrderHistoryFrame(currentUser).setVisible(true);
    }

    private JButton createTopBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(new Color(0x3D5166));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        return btn;
    }
}

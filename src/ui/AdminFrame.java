/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ui;

import dao.OrderDAO;
import dao.ProductDAO;
import models.Product;
import models.User;

/**
 *
 * @author Anis
 */
public class AdminFrame extends javax.swing.JFrame {

    private final User adminUser;
    private final ProductDAO productDAO = new ProductDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private javax.swing.table.DefaultTableModel productModel;
    private javax.swing.table.DefaultTableModel orderModel;
    private javax.swing.JTable productTable;
    private javax.swing.JTable orderTable;
    private javax.swing.JTextField fName;
    private javax.swing.JTextField fDesc;
    private javax.swing.JTextField fPrice;
    private javax.swing.JTextField fStock;
    private javax.swing.JComboBox<String> fCategory;

    public AdminFrame(User user) {
        if (user == null || !user.isAdmin()) {
            initComponents();
            javax.swing.JOptionPane.showMessageDialog(null,
                    "Accès refusé. Droits administrateur requis.", "Non autorisé",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            dispose();
            new Loginframe().setVisible(true);
            return;
        }
        this.adminUser = user;
        initComponents();
        buildUI();
        setLocationRelativeTo(null);
        refreshProducts();
        refreshOrders();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
        pack();
    }

    private void buildUI() {

        setTitle("Smart Info — Tableau de bord Administrateur");
        setPreferredSize(new java.awt.Dimension(1000, 660));
        setLayout(new java.awt.BorderLayout());

        javax.swing.JPanel pnlTop = new javax.swing.JPanel(new java.awt.BorderLayout());
        pnlTop.setBackground(new java.awt.Color(26, 37, 47));
        pnlTop.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));

        javax.swing.JLabel lblTitle = new javax.swing.JLabel("Smart Info — Panneau Administrateur");
        lblTitle.setFont(new java.awt.Font("Georgia", 1, 20));
        lblTitle.setForeground(java.awt.Color.WHITE);
        pnlTop.add(lblTitle, java.awt.BorderLayout.WEST);

        javax.swing.JPanel pnlTopRight = new javax.swing.JPanel(
                new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 0));
        pnlTopRight.setOpaque(false);

        javax.swing.JLabel lblAdminInfo = new javax.swing.JLabel(
                "Connecté en tant que : " + (adminUser != null ? adminUser.getUsername() : "admin") + " (Administrateur)");
        lblAdminInfo.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblAdminInfo.setForeground(new java.awt.Color(243, 156, 18));
        pnlTopRight.add(lblAdminInfo);

        javax.swing.JButton btnLogout = makeBtn("Déconnexion", new java.awt.Color(231, 76, 60));
        btnLogout.addActionListener(e -> {
            dispose();
            new Loginframe().setVisible(true);
        });
        pnlTopRight.add(btnLogout);

        pnlTop.add(pnlTopRight, java.awt.BorderLayout.EAST);
        add(pnlTop, java.awt.BorderLayout.NORTH);

        javax.swing.JTabbedPane tabs = new javax.swing.JTabbedPane();
        tabs.setFont(new java.awt.Font("Segoe UI", 1, 13));
        tabs.addTab("Produits", buildProductsTab());
        tabs.addTab("Commandes", buildOrdersTab());
        add(tabs, java.awt.BorderLayout.CENTER);

        pack();
    }

    private javax.swing.JPanel buildProductsTab() {
        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.BorderLayout(10, 10));
        panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new java.awt.Color(244, 246, 247));

        productModel = new javax.swing.table.DefaultTableModel(
                new String[]{"ID", "Nom", "Catégorie", "Prix", "Stock", "Description"}, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        productTable = new javax.swing.JTable(productModel);
        productTable.setFont(new java.awt.Font("Segoe UI", 0, 13));
        productTable.setRowHeight(28);
        productTable.getTableHeader().setFont(new java.awt.Font("Segoe UI", 1, 13));
        productTable.getTableHeader().setBackground(new java.awt.Color(26, 37, 47));
        productTable.getTableHeader().setForeground(java.awt.Color.BLACK);
        productTable.setSelectionBackground(new java.awt.Color(250, 215, 160));
        productTable.setSelectionForeground(java.awt.Color.BLACK);
        productTable.setGridColor(new java.awt.Color(229, 232, 232));
        productTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                populateForm();
            }
        });

        int[] widths = {40, 200, 100, 80, 70, 260};
        for (int i = 0; i < widths.length; i++) {
            productTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        javax.swing.JScrollPane scroll = new javax.swing.JScrollPane(productTable);

        javax.swing.JPanel formPanel = new javax.swing.JPanel(new java.awt.GridBagLayout());
        formPanel.setBackground(java.awt.Color.WHITE);
        formPanel.setPreferredSize(new java.awt.Dimension(290, 0));
        formPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createLineBorder(new java.awt.Color(189, 195, 199)),
                "Détails du produit",
                javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
                new java.awt.Font("Segoe UI", 1, 12), new java.awt.Color(44, 62, 80)));

        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.insets = new java.awt.Insets(5, 8, 4, 8);
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gbc.weightx = 1.0;

        fName = new javax.swing.JTextField();
        fDesc = new javax.swing.JTextField();
        fPrice = new javax.swing.JTextField();
        fStock = new javax.swing.JTextField();
        fCategory = new javax.swing.JComboBox<>(
                new String[]{"livre", "cahier", "stylo", "papeterie", "autre"});

        String[] labels = {"Nom :", "Description :", "Prix :", "Stock :", "Catégorie :"};
        java.awt.Component[] fields = {fName, fDesc, fPrice, fStock, fCategory};

        for (int i = 0; i < labels.length; i++) {
            javax.swing.JLabel lbl = new javax.swing.JLabel(labels[i]);
            lbl.setFont(new java.awt.Font("Segoe UI", 1, 11));
            lbl.setForeground(new java.awt.Color(85, 85, 85));
            gbc.insets = new java.awt.Insets(6, 8, 2, 8);
            formPanel.add(lbl, gbc);
            gbc.insets = new java.awt.Insets(0, 8, 4, 8);
            formPanel.add(fields[i], gbc);
        }

        javax.swing.JPanel btnRow = new javax.swing.JPanel(
                new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 8));
        btnRow.setOpaque(false);

        javax.swing.JButton btnRefresh = makeBtn("↻ Actualiser", new java.awt.Color(93, 173, 226));
        javax.swing.JButton btnAdd = makeBtn("➕ Ajouter", new java.awt.Color(39, 174, 96));
        javax.swing.JButton btnEdit = makeBtn("✏ Modifier", new java.awt.Color(243, 156, 18));
        javax.swing.JButton btnDelete = makeBtn("🗑 Supprimer", new java.awt.Color(231, 76, 60));
        javax.swing.JButton btnClear = makeBtn("Effacer", new java.awt.Color(149, 165, 166));

        btnRefresh.addActionListener(e -> refreshProducts());
        btnClear.addActionListener(e -> clearForm());
        btnAdd.addActionListener(e -> addProduct());
        btnEdit.addActionListener(e -> updateProduct());
        btnDelete.addActionListener(e -> deleteProduct());

        btnRow.add(btnRefresh);
        btnRow.add(btnAdd);
        btnRow.add(btnEdit);
        btnRow.add(btnDelete);
        btnRow.add(btnClear);

        java.awt.GridBagConstraints spacer = new java.awt.GridBagConstraints();
        spacer.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        spacer.fill = java.awt.GridBagConstraints.VERTICAL;
        spacer.weighty = 1.0;
        formPanel.add(new javax.swing.JPanel(), spacer);

        java.awt.GridBagConstraints btnGbc = new java.awt.GridBagConstraints();
        btnGbc.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        btnGbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        formPanel.add(btnRow, btnGbc);

        panel.add(scroll, java.awt.BorderLayout.CENTER);
        panel.add(formPanel, java.awt.BorderLayout.EAST);
        return panel;
    }

    private javax.swing.JPanel buildOrdersTab() {
        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.BorderLayout(10, 10));
        panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new java.awt.Color(244, 246, 247));

        orderModel = new javax.swing.table.DefaultTableModel(
                new String[]{"N° Commande", "Client", "Produit", "Qté", "Total", "Date"}, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        orderTable = new javax.swing.JTable(orderModel);
        orderTable.setFont(new java.awt.Font("Segoe UI", 0, 13));
        orderTable.setRowHeight(28);
        orderTable.getTableHeader().setFont(new java.awt.Font("Segoe UI", 1, 13));
        orderTable.getTableHeader().setBackground(new java.awt.Color(26, 37, 47));
        orderTable.getTableHeader().setForeground(java.awt.Color.BLACK);
        orderTable.setSelectionBackground(new java.awt.Color(214, 234, 248));
        orderTable.setSelectionForeground(java.awt.Color.BLACK);
        orderTable.setGridColor(new java.awt.Color(229, 232, 232));

        int[] widths = {80, 150, 100, 100, 200};
        for (int i = 0; i < widths.length; i++) {
            orderTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        javax.swing.JPanel topRow = new javax.swing.JPanel(
                new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        topRow.setOpaque(false);
        javax.swing.JButton btnRefreshOrders = makeBtn("↻ Actualiser les commandes",
                new java.awt.Color(93, 173, 226));
        btnRefreshOrders.addActionListener(e -> refreshOrders());
        topRow.add(btnRefreshOrders);

        panel.add(topRow, java.awt.BorderLayout.NORTH);
        panel.add(new javax.swing.JScrollPane(orderTable), java.awt.BorderLayout.CENTER);
        return panel;
    }

    private void refreshProducts() {
        productModel.setRowCount(0);
        for (Product p : productDAO.getAllProducts()) {
            productModel.addRow(new Object[]{
                p.getId(), p.getName(), p.getCategory(),
                String.format("%.2f €", p.getPrice()), p.getStock(), p.getDescription()
            });
        }
    }

    private void refreshOrders() {
        orderModel.setRowCount(0);
        for (String[] row : orderDAO.getAllOrders()) {
            orderModel.addRow(row);
        }
    }

    private void populateForm() {
        int row = productTable.getSelectedRow();
        if (row == -1) {
            return;
        }
        fName.setText((String) productModel.getValueAt(row, 1));
        fCategory.setSelectedItem(productModel.getValueAt(row, 2));
        fPrice.setText(productModel.getValueAt(row, 3).toString().replace(" €", ""));
        fStock.setText(productModel.getValueAt(row, 4).toString());
        fDesc.setText((String) productModel.getValueAt(row, 5));
    }

    private void clearForm() {
        fName.setText("");
        fDesc.setText("");
        fPrice.setText("");
        fStock.setText("");
        fCategory.setSelectedIndex(0);
        productTable.clearSelection();
    }

    private void addProduct() {
        Product p = buildProduct();
        if (p == null) {
            return;
        }
        if (productDAO.addProduct(p)) {
            javax.swing.JOptionPane.showMessageDialog(this, "Produit ajouté !");
            clearForm();
            refreshProducts();
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "Échec de l'ajout.",
                    "Erreur", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateProduct() {
        int row = productTable.getSelectedRow();
        if (row == -1) {
            javax.swing.JOptionPane.showMessageDialog(this, "Veuillez sélectionner un produit.");
            return;
        }
        Product p = buildProduct();
        if (p == null) {
            return;
        }
        p.setId((int) productModel.getValueAt(row, 0));
        if (productDAO.updateProduct(p)) {
            javax.swing.JOptionPane.showMessageDialog(this, "Produit mis à jour !");
            clearForm();
            refreshProducts();
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "Échec de la mise à jour.",
                    "Erreur", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteProduct() {
        int row = productTable.getSelectedRow();
        if (row == -1) {
            javax.swing.JOptionPane.showMessageDialog(this, "Veuillez sélectionner un produit.");
            return;
        }
        String name = (String) productModel.getValueAt(row, 1);
        int id = (int) productModel.getValueAt(row, 0);
        int confirm = javax.swing.JOptionPane.showConfirmDialog(this,
                "Supprimer \"" + name + "\" ?", "Confirmation", javax.swing.JOptionPane.YES_NO_OPTION);
        if (confirm != javax.swing.JOptionPane.YES_OPTION) {
            return;
        }
        if (productDAO.deleteProduct(id)) {
            javax.swing.JOptionPane.showMessageDialog(this, "Produit supprimé.");
            clearForm();
            refreshProducts();
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "Échec de la suppression.",
                    "Erreur", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    private Product buildProduct() {
        String name = fName.getText().trim();
        String desc = fDesc.getText().trim();
        String priceStr = fPrice.getText().trim();
        String stockStr = fStock.getText().trim();
        String cat = (String) fCategory.getSelectedItem();
        if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Le nom, le prix et le stock sont obligatoires.");
            return null;
        }
        double price;
        int stock;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Prix invalide.");
            return null;
        }
        try {
            stock = Integer.parseInt(stockStr);
        } catch (NumberFormatException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Stock invalide.");
            return null;
        }
        Product p = new Product();
        p.setName(name);
        p.setDescription(desc);
        p.setPrice(price);
        p.setStock(stock);
        p.setCategory(cat);
        return p;
    }

    private javax.swing.JButton makeBtn(String text, java.awt.Color bg) {
        javax.swing.JButton btn = new javax.swing.JButton(text);
        btn.setFont(new java.awt.Font("Segoe UI", 1, 11));
        btn.setBackground(bg);
        btn.setForeground(java.awt.Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 10, 6, 10));
        return btn;
    }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AdminFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AdminFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AdminFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AdminFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AdminFrame(null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify
    // End of variables declaration
}
package ui;

import dao.OrderDAO;
import models.OrderItem;
import models.User;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class CartFrame extends JFrame {

    private final User currentUser;
    private final List<OrderItem> cart;

    private JTable cartTable;
    private DefaultTableModel tableModel;
    private JLabel lblTotal;

    public CartFrame(User user, List<OrderItem> cart) {
        this.currentUser = user;
        this.cart = cart;
        setTitle("Library Store — Shopping Cart");
        setSize(750, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
        refreshCart();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0x27AE60));
        header.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        JLabel lblTitle = new JLabel("🛒  Your Shopping Cart");
        lblTitle.setFont(new Font("Georgia", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        header.add(lblTitle, BorderLayout.WEST);

        // Table
        String[] cols = {"#", "Product", "Unit Price", "Qty", "Subtotal"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        cartTable = new JTable(tableModel);
        cartTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cartTable.setRowHeight(32);
        cartTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        cartTable.getTableHeader().setBackground(new Color(0x2ECC71));
        cartTable.getTableHeader().setForeground(Color.WHITE);
        cartTable.setSelectionBackground(new Color(0xD5F5E3));
        cartTable.setGridColor(new Color(0xECF0F1));

        int[] widths = {40, 260, 100, 80, 100};
        for (int i = 0; i < widths.length; i++)
            cartTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JScrollPane scroll = new JScrollPane(cartTable);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        // Bottom panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(0xF8F9FA));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 15, 20));

        lblTotal = new JLabel("Total: $0.00");
        lblTotal.setFont(new Font("Georgia", Font.BOLD, 18));
        lblTotal.setForeground(new Color(0x2C3E50));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        JButton btnRemove = createBtn("🗑 Remove Item", new Color(0xE74C3C));
        JButton btnClear  = createBtn("Clear Cart", new Color(0x95A5A6));
        JButton btnCheckout = createBtn("✔ Checkout", new Color(0x27AE60));
        btnCheckout.setFont(new Font("Segoe UI", Font.BOLD, 14));

        btnPanel.add(btnRemove);
        btnPanel.add(btnClear);
        btnPanel.add(btnCheckout);

        bottomPanel.add(lblTotal, BorderLayout.WEST);
        bottomPanel.add(btnPanel, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Listeners
        btnRemove.addActionListener(e -> removeSelected());
        btnClear.addActionListener(e -> {
            cart.clear();
            refreshCart();
        });
        btnCheckout.addActionListener(e -> checkout());
    }

    private void refreshCart() {
        tableModel.setRowCount(0);
        double total = 0;
        int i = 1;
        for (OrderItem item : cart) {
            tableModel.addRow(new Object[]{
                i++,
                item.getProduct().getName(),
                String.format("$%.2f", item.getUnitPrice()),
                item.getQuantity(),
                String.format("$%.2f", item.getSubtotal())
            });
            total += item.getSubtotal();
        }
        lblTotal.setText(String.format("Total: $%.2f", total));

        if (cart.isEmpty()) {
            lblTotal.setText("Your cart is empty.");
        }
    }

    private void removeSelected() {
        int row = cartTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an item to remove.");
            return;
        }
        cart.remove(row);
        refreshCart();
    }

    private void checkout() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty!", "Empty Cart", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double total = cart.stream().mapToDouble(OrderItem::getSubtotal).sum();
        int confirm = JOptionPane.showConfirmDialog(this,
            String.format("Confirm order?\n\nItems: %d\nTotal: $%.2f\n\nProceed to checkout?",
                cart.size(), total),
            "Confirm Order", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        OrderDAO orderDAO = new OrderDAO();
        int orderId = orderDAO.placeOrder(currentUser.getId(), cart, total);

        if (orderId > 0) {
            JOptionPane.showMessageDialog(this,
                "✅ Order placed successfully!\nOrder ID: #" + orderId +
                "\n\nThank you for shopping at Library Store!",
                "Order Confirmed", JOptionPane.INFORMATION_MESSAGE);
            cart.clear();
            refreshCart();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "❌ Order failed. Some items may be out of stock.\nPlease review your cart.",
                "Order Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton createBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(bg.darker()); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(bg); }
        });
        return btn;
    }
}

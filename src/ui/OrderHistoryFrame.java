package ui;

import dao.OrderDAO;
import models.User;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class OrderHistoryFrame extends JFrame {

    private final User currentUser;
    private DefaultTableModel tableModel;

    public OrderHistoryFrame(User user) {
        this.currentUser = user;
        setTitle("Library Store — My Orders");
        setSize(680, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0x8E44AD));
        header.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        JLabel lblTitle = new JLabel("📋  My Order History — " + currentUser.getFullName());
        lblTitle.setFont(new Font("Georgia", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        header.add(lblTitle, BorderLayout.WEST);

        // Table
        String[] cols = {"Order ID", "Total", "Status", "Date"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(0x8E44AD));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(0xE8DAEF));
        table.setGridColor(new Color(0xECF0F1));

        // Color rows by status
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (!sel) {
                    String status = (String) tableModel.getValueAt(r, 2);
                    switch (status) {
                        case "confirmed" -> comp.setBackground(new Color(0xD5F5E3));
                        case "pending"   -> comp.setBackground(new Color(0xFEF9E7));
                        case "cancelled" -> comp.setBackground(new Color(0xFADCDC));
                        default          -> comp.setBackground(Color.WHITE);
                    }
                }
                return comp;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Empty state
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(new Color(0xF4F6F7));
        footer.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JButton btnClose = new JButton("Close");
        btnClose.setBackground(new Color(0x8E44AD));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.setBorderPainted(false);
        btnClose.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnClose.setBorder(BorderFactory.createEmptyBorder(7, 20, 7, 20));
        btnClose.addActionListener(e -> dispose());
        footer.add(btnClose);

        add(header, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        loadOrders();
    }

    private void loadOrders() {
        tableModel.setRowCount(0);
        OrderDAO dao = new OrderDAO();
        java.util.List<String[]> orders = dao.getOrderHistory(currentUser.getId());
        if (orders.isEmpty()) {
            tableModel.addRow(new String[]{"—", "—", "No orders yet", "—"});
        } else {
            for (String[] row : orders) tableModel.addRow(row);
        }
    }
}

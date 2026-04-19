package dao;

import database.DBConnection;
import models.OrderItem;

import java.sql.*;
import java.util.List;

public class OrderDAO {

    /**
     * Place an order: creates order record + all order_items + reduces stock.
     * Returns the new order ID, or -1 on failure.
     */
    public int placeOrder(int userId, List<OrderItem> items, double total) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Insert order
            String orderSql = "INSERT INTO orders (user_id, total_price, status) VALUES (?, ?, 'confirmed')";
            PreparedStatement orderPs = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
            orderPs.setInt(1, userId);
            orderPs.setDouble(2, total);
            orderPs.executeUpdate();

            ResultSet keys = orderPs.getGeneratedKeys();
            if (!keys.next()) { conn.rollback(); return -1; }
            int orderId = keys.getInt(1);

            // 2. Insert order items + reduce stock
            String itemSql = "INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
            String stockSql = "UPDATE products SET stock = stock - ? WHERE id = ? AND stock >= ?";

            for (OrderItem item : items) {
                PreparedStatement itemPs = conn.prepareStatement(itemSql);
                itemPs.setInt(1, orderId);
                itemPs.setInt(2, item.getProduct().getId());
                itemPs.setInt(3, item.getQuantity());
                itemPs.setDouble(4, item.getUnitPrice());
                itemPs.executeUpdate();

                PreparedStatement stockPs = conn.prepareStatement(stockSql);
                stockPs.setInt(1, item.getQuantity());
                stockPs.setInt(2, item.getProduct().getId());
                stockPs.setInt(3, item.getQuantity());
                int updated = stockPs.executeUpdate();

                if (updated == 0) {
                    conn.rollback();
                    return -1; // not enough stock
                }
            }

            conn.commit();
            return orderId;

        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return -1;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    /** Get order history for a user */
    public java.util.List<String[]> getOrderHistory(int userId) {
        java.util.List<String[]> orders = new java.util.ArrayList<>();
        String sql = "SELECT o.id, o.total_price, o.status, o.created_at FROM orders o WHERE o.user_id = ? ORDER BY o.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                orders.add(new String[]{
                    String.valueOf(rs.getInt("id")),
                    String.format("$%.2f", rs.getDouble("total_price")),
                    rs.getString("status"),
                    rs.getString("created_at")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    /** Get all orders (admin view) */
    public java.util.List<String[]> getAllOrders() {
        java.util.List<String[]> orders = new java.util.ArrayList<>();
        String sql = "SELECT o.id, u.username, o.total_price, o.status, o.created_at FROM orders o JOIN users u ON o.user_id=u.id ORDER BY o.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                orders.add(new String[]{
                    String.valueOf(rs.getInt("id")),
                    rs.getString("username"),
                    String.format("$%.2f", rs.getDouble("total_price")),
                    rs.getString("status"),
                    rs.getString("created_at")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
}

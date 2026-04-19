package ui;

import dao.UserDAO;
import models.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnRegister;
    private JLabel lblStatus;

    public LoginFrame() {
        setTitle("Library Store — Login");
        setSize(420, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        // Main panel with gradient-like background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(0x2C3E50), 0, getHeight(), new Color(0x3498DB));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());

        // Title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));
        JLabel lblIcon = new JLabel("📚");
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        JLabel lblTitle = new JLabel("Library Store");
        lblTitle.setFont(new Font("Georgia", Font.BOLD, 26));
        lblTitle.setForeground(Color.WHITE);
        JLabel lblSub = new JLabel("Your destination for books & stationery");
        lblSub.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblSub.setForeground(new Color(0xBDC3C7));

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleBox.add(lblIcon);
        titleBox.add(Box.createVerticalStrut(5));
        titleBox.add(lblTitle);
        titleBox.add(lblSub);
        titlePanel.add(titleBox);

        // Form card
        JPanel card = new JPanel();
        card.setLayout(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;

        // Username
        gbc.gridy = 0;
        JLabel lUser = new JLabel("Username");
        lUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lUser.setForeground(new Color(0x555555));
        card.add(lUser, gbc);

        gbc.gridy = 1;
        txtUsername = new JTextField();
        styleTextField(txtUsername, "Enter your username");
        card.add(txtUsername, gbc);

        // Password
        gbc.gridy = 2;
        JLabel lPass = new JLabel("Password");
        lPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lPass.setForeground(new Color(0x555555));
        card.add(lPass, gbc);

        gbc.gridy = 3;
        txtPassword = new JPasswordField();
        styleTextField(txtPassword, "Enter your password");
        card.add(txtPassword, gbc);

        // Status label
        gbc.gridy = 4;
        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(Color.RED);
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(lblStatus, gbc);

        // Login button
        gbc.gridy = 5;
        btnLogin = createStyledButton("LOGIN", new Color(0x2980B9), Color.WHITE);
        card.add(btnLogin, gbc);

        // Register link
        gbc.gridy = 6;
        btnRegister = new JButton("Don't have an account? Register here");
        btnRegister.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnRegister.setForeground(new Color(0x2980B9));
        btnRegister.setBorderPainted(false);
        btnRegister.setContentAreaFilled(false);
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.add(btnRegister, gbc);

        // Wrap card in padding panel
        JPanel cardWrapper = new JPanel(new GridBagLayout());
        cardWrapper.setOpaque(false);
        cardWrapper.setBorder(BorderFactory.createEmptyBorder(0, 30, 30, 30));
        GridBagConstraints wgbc = new GridBagConstraints();
        wgbc.fill = GridBagConstraints.HORIZONTAL;
        wgbc.weightx = 1.0;
        cardWrapper.add(card, wgbc);

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(cardWrapper, BorderLayout.CENTER);
        setContentPane(mainPanel);

        // Listeners
        btnLogin.addActionListener(e -> handleLogin());
        btnRegister.addActionListener(e -> openRegister());
        txtPassword.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            lblStatus.setText("Please fill in all fields.");
            return;
        }

        UserDAO dao = new UserDAO();
        User user = dao.login(username, password);

        if (user != null) {
            lblStatus.setForeground(new Color(0x27AE60));
            lblStatus.setText("Welcome back, " + user.getFullName() + "!");
            dispose();
            if (user.isAdmin()) {
                new AdminFrame(user).setVisible(true);
            } else {
                new HomeFrame(user).setVisible(true);
            }
        } else {
            lblStatus.setForeground(Color.RED);
            lblStatus.setText("Invalid username or password.");
            txtPassword.setText("");
        }
    }

    private void openRegister() {
        dispose();
        new RegisterFrame().setVisible(true);
    }

    private void styleTextField(JTextField tf, String placeholder) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setPreferredSize(new Dimension(280, 38));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xCCCCCC)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        tf.setForeground(Color.GRAY);
        tf.setText(placeholder);
        tf.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (tf.getText().equals(placeholder)) {
                    tf.setText("");
                    tf.setForeground(Color.BLACK);
                    if (tf instanceof JPasswordField)
                        ((JPasswordField) tf).setEchoChar('•');
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (tf.getText().isEmpty()) {
                    tf.setText(placeholder);
                    tf.setForeground(Color.GRAY);
                    if (tf instanceof JPasswordField)
                        ((JPasswordField) tf).setEchoChar((char) 0);
                }
            }
        });
        if (tf instanceof JPasswordField) ((JPasswordField) tf).setEchoChar((char) 0);
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(280, 42));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btn.setBackground(bg.darker()); }
            @Override
            public void mouseExited(MouseEvent e)  { btn.setBackground(bg); }
        });
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception e) { e.printStackTrace(); }
            new LoginFrame().setVisible(true);
        });
    }
}

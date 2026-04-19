package ui;

import dao.UserDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RegisterFrame extends JFrame {

    private JTextField txtFullName, txtEmail, txtUsername;
    private JPasswordField txtPassword, txtConfirm;
    private JButton btnRegister, btnBack;
    private JLabel lblStatus;

    public RegisterFrame() {
        setTitle("Library Store — Create Account");
        setSize(450, 580);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, new Color(0x1ABC9C), getWidth(), getHeight(), new Color(0x2C3E50)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        // Title
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 10, 0));
        JLabel lbl = new JLabel("📚 Create Account");
        lbl.setFont(new Font("Georgia", Font.BOLD, 22));
        lbl.setForeground(Color.WHITE);
        titlePanel.add(lbl);

        // Card
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 5, 4, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;

        String[] labels   = {"Full Name", "Email Address", "Username", "Password", "Confirm Password"};
        JTextField[] fields = new JTextField[5];
        String[] hints    = {"Your full name", "your@email.com", "Choose a username", "Min. 6 characters", "Repeat password"};

        for (int i = 0; i < labels.length; i++) {
            // Label
            gbc.gridy = i * 2;
            JLabel l = new JLabel(labels[i]);
            l.setFont(new Font("Segoe UI", Font.BOLD, 12));
            l.setForeground(new Color(0x555555));
            card.add(l, gbc);

            // Field
            gbc.gridy = i * 2 + 1;
            if (i >= 3) {
                fields[i] = new JPasswordField();
            } else {
                fields[i] = new JTextField();
            }
            addPlaceholder(fields[i], hints[i]);
            card.add(fields[i], gbc);
        }

        txtFullName = fields[0];
        txtEmail    = fields[1];
        txtUsername = fields[2];
        txtPassword = (JPasswordField) fields[3];
        txtConfirm  = (JPasswordField) fields[4];

        // Status
        gbc.gridy = 10;
        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(lblStatus, gbc);

        // Register button
        gbc.gridy = 11;
        btnRegister = createBtn("CREATE ACCOUNT", new Color(0x1ABC9C));
        card.add(btnRegister, gbc);

        // Back to login
        gbc.gridy = 12;
        btnBack = new JButton("Already have an account? Login");
        btnBack.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnBack.setForeground(new Color(0x16A085));
        btnBack.setBorderPainted(false);
        btnBack.setContentAreaFilled(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.add(btnBack, gbc);

        JPanel cardWrapper = new JPanel(new GridBagLayout());
        cardWrapper.setOpaque(false);
        cardWrapper.setBorder(BorderFactory.createEmptyBorder(0, 30, 25, 30));
        GridBagConstraints wc = new GridBagConstraints();
        wc.fill = GridBagConstraints.HORIZONTAL;
        wc.weightx = 1.0;
        cardWrapper.add(card, wc);

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(cardWrapper, BorderLayout.CENTER);
        setContentPane(mainPanel);

        btnRegister.addActionListener(e -> handleRegister());
        btnBack.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });
    }

    private void handleRegister() {
        String fullName  = txtFullName.getText().trim();
        String email     = txtEmail.getText().trim();
        String username  = txtUsername.getText().trim();
        String password  = new String(txtPassword.getPassword());
        String confirm   = new String(txtConfirm.getPassword());

        // Placeholders treated as empty
        if (fullName.equals("Your full name")) fullName = "";
        if (email.equals("your@email.com"))    email = "";
        if (username.equals("Choose a username")) username = "";

        if (fullName.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showStatus("All fields are required.", Color.RED);
            return;
        }
        if (password.equals("Min. 6 characters") || password.length() < 6) {
            showStatus("Password must be at least 6 characters.", Color.RED);
            return;
        }
        if (!password.equals(confirm)) {
            showStatus("Passwords do not match.", Color.RED);
            return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            showStatus("Enter a valid email address.", Color.RED);
            return;
        }

        UserDAO dao = new UserDAO();
        if (dao.usernameExists(username)) {
            showStatus("Username already taken. Try another.", Color.RED);
            return;
        }

        boolean ok = dao.register(username, password, email, fullName);
        if (ok) {
            JOptionPane.showMessageDialog(this,
                "Account created successfully!\nYou can now log in.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new LoginFrame().setVisible(true);
        } else {
            showStatus("Registration failed. Email may already exist.", Color.RED);
        }
    }

    private void showStatus(String msg, Color c) {
        lblStatus.setText(msg);
        lblStatus.setForeground(c);
    }

    private void addPlaceholder(JTextField tf, String hint) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setPreferredSize(new Dimension(300, 36));
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xCCCCCC)),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        tf.setText(hint);
        tf.setForeground(Color.GRAY);
        if (tf instanceof JPasswordField) ((JPasswordField) tf).setEchoChar((char) 0);

        tf.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (tf.getText().equals(hint)) {
                    tf.setText("");
                    tf.setForeground(Color.BLACK);
                    if (tf instanceof JPasswordField) ((JPasswordField) tf).setEchoChar('•');
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (tf.getText().isEmpty()) {
                    tf.setText(hint);
                    tf.setForeground(Color.GRAY);
                    if (tf instanceof JPasswordField) ((JPasswordField) tf).setEchoChar((char) 0);
                }
            }
        });
    }

    private JButton createBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(300, 40));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(bg.darker()); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(bg); }
        });
        return btn;
    }
}

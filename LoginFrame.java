package com.mycompany.cbe_banking_system;

import com.bank.db.AccountDAO;
import com.bank.model.Account;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginFrame extends JFrame {
    private JPasswordField txtPassword;

    public LoginFrame() {
        // Window Setup
        setTitle("CBE - Secure Login");
        setSize(420, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));

        try {
            // Logo loading with better scaling
            java.net.URL imgURL = getClass().getResource("/images/cbe_logo.jpg");
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                Image img = icon.getImage().getScaledInstance(220, 95, Image.SCALE_SMOOTH);
                JLabel lblLogo = new JLabel(new ImageIcon(img));
                lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
                headerPanel.add(lblLogo);
            }
        } catch (Exception e) {
            System.out.println("Logo error: " + e.getMessage());
        }

        headerPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Bank Name Title
        JLabel lblBankName = new JLabel("COMMERCIAL BANK OF ETHIOPIA");
        lblBankName.setFont(new Font("Serif", Font.BOLD, 18));
        lblBankName.setForeground(new Color(108, 52, 131)); // CBE Purple
        lblBankName.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(lblBankName);

        headerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel lblWelcome = new JLabel("Welcome Back!");
        lblWelcome.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblWelcome.setForeground(new Color(184, 134, 11)); // Dark Gold color
        lblWelcome.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(lblWelcome);

        add(headerPanel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JLabel lblPassHint = new JLabel("Please enter your PIN below");
        lblPassHint.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblPassHint.setForeground(Color.GRAY);
        formPanel.add(lblPassHint);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        txtPassword = new JPasswordField();
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        txtPassword.setFont(new Font("SansSerif", Font.BOLD, 18));
        txtPassword.setHorizontalAlignment(JTextField.CENTER);
        // CBE Purple Border
        txtPassword.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(108, 52, 131), 2), "PIN / Password",
            0, 0, new Font("SansSerif", Font.PLAIN, 11)));
        
        formPanel.add(txtPassword);
        formPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        JButton btnLogin = new JButton("LOGIN");
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnLogin.setBackground(new Color(108, 52, 131)); // Purple
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(btnLogin);

        add(formPanel, BorderLayout.CENTER);

        // Footer Panel
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));

        JLabel lblRegister = new JLabel("Don't have an account? Register here");
        lblRegister.setForeground(new Color(41, 128, 185));
        lblRegister.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblRegister.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        footerPanel.add(lblRegister);

        add(footerPanel, BorderLayout.SOUTH);

        // Listeners
        btnLogin.addActionListener(e -> handleLogin());
        lblRegister.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                new RegistrationFrame().setVisible(true);
                dispose();
            }
        });
    }

    private void handleLogin() {
        String pass = new String(txtPassword.getPassword()).trim();

        if (pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your PIN!", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
       if (pass.equals("123")) {
    new AdminDashboardFrame().setVisible(true);
    this.dispose();
    return; // አድሚን ከሆነ እዚህ ጋር ይቆማል
       }
        AccountDAO dao = new AccountDAO();
        Account acc = dao.validateLoginWithOnlyPassword(pass);
        
        if (acc != null) {
            new DashboardFrame(acc).setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Incorrect PIN. Access Denied.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
        }
    }
}
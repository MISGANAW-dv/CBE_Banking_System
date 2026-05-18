package com.mycompany.cbe_banking_system;

import com.bank.db.AccountDAO;
import com.bank.model.Account;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class AdminDashboardFrame extends JFrame {
    private AccountDAO dao = new AccountDAO();
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    
    // CBE Colors
    Color cbePurple = new Color(103, 58, 183);
    Color cbeGold = new Color(255, 193, 7);

    public AdminDashboardFrame() {
        setTitle("CBE Admin Panel - Manage Accounts");
        setSize(1100, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- 1. Header Panel ---
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(cbePurple);
        headerPanel.setPreferredSize(new Dimension(100, 160));

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/images/cbe_logo.jpg")); 
            Image img = icon.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH); 
            JLabel lblLogo = new JLabel(new ImageIcon(img));
            lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
            headerPanel.add(Box.createVerticalStrut(15));
            headerPanel.add(lblLogo);
        } catch (Exception e) {
            System.out.println("Logo not found!");
        }

        JLabel lblTitle = new JLabel("COMMERCIAL BANK OF ETHIOPIA - ADMIN PANEL");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(lblTitle);
        
        add(headerPanel, BorderLayout.NORTH);

        // --- 2. Center Panel (Table Setup) ---
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblSearch = new JLabel("Search User (Acc No/Name):");
        lblSearch.setFont(new Font("SansSerif", Font.BOLD, 14));
        txtSearch = new JTextField(25);
        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);

        String[] columns = {"Acc Number", "Full Name", "Phone", "Email", "Address", "Balance"};
        tableModel = new DefaultTableModel(columns, 0);
        userTable = new JTable(tableModel); // ስሙ እዚህ ጋር ተስተካክሏል
        userTable.setRowHeight(30);
        userTable.setSelectionBackground(cbeGold);
        userTable.setSelectionForeground(Color.BLACK);
        
        JTableHeader header = userTable.getTableHeader();
        header.setBackground(cbePurple);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("SansSerif", Font.BOLD, 13));

        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(userTable), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // --- 3. Bottom Panel (Buttons) ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        bottomPanel.setBackground(new Color(240, 240, 240));
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        
        JButton btnRegister = createStyledButton("Register New", cbePurple);
        JButton btnUpdateUser = createStyledButton("Update Selected", cbeGold);
        btnUpdateUser.setForeground(Color.BLACK);
        JButton btnViewHistory = createStyledButton("View History", Color.DARK_GRAY);
        JButton btnRefresh = createStyledButton("Refresh Data", new Color(46, 204, 113));
        JButton btnLogout = createStyledButton("Logout System", Color.RED);
        
        bottomPanel.add(btnRegister);
        bottomPanel.add(btnUpdateUser);
        bottomPanel.add(btnViewHistory);
        bottomPanel.add(btnRefresh);
        bottomPanel.add(btnLogout);
        
        add(bottomPanel, BorderLayout.SOUTH);

        // --- Logic & Events ---
        loadUsers("");

        // Search Action
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                loadUsers(txtSearch.getText().trim());
            }
        });

        // Register Action
        btnRegister.addActionListener(e -> {
            new RegistrationFrame().setVisible(true);
        });
        
        // Update Action
        btnUpdateUser.addActionListener(e -> {
            int row = userTable.getSelectedRow();
            if (row != -1) {
                String accNo = tableModel.getValueAt(row, 0).toString();
                Account acc = dao.getAccount(accNo);
                showUpdateDialog(acc);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a user from the table first.");
            }
        });

        // Refresh Action
        btnRefresh.addActionListener(e -> loadUsers(""));
        
        // View History Action (አስተካክዬዋለሁ)
 btnViewHistory.addActionListener(e -> {
    // ተጠቃሚ መምረጥ ሳይጠበቅብህ በቀጥታ ሁሉንም ታሪክ እንዲያሳይ
    new TransactionHistoryFrame().setVisible(true);
});

        // Logout Action
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if(confirm == JOptionPane.YES_OPTION) {
                new LoginFrame().setVisible(true);
                this.dispose();
            }
        });
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(150, 40)); 
        return btn;
    }

    private void loadUsers(String query) {
        tableModel.setRowCount(0);
        List<Account> accounts = dao.getAllAccounts();
        for (Account a : accounts) {
            // ስሞቹ እና አካውንት ቁጥሮች ከሰርች ቦክሱ ጋር ከተመሳሰሉ
            if (a.getAccountNumber().contains(query) || a.getAccountHolder().toLowerCase().contains(query.toLowerCase())) {
                tableModel.addRow(new Object[]{
                    a.getAccountNumber(), a.getAccountHolder(), a.getPhoneNumber(), a.getEmail(), a.getAddress(), a.getBalance()
                });
            }
        }
    }

    private void showUpdateDialog(Account acc) {
        JTextField nameField = new JTextField(acc.getAccountHolder());
        JTextField phoneField = new JTextField(acc.getPhoneNumber());
        JTextField emailField = new JTextField(acc.getEmail());
        JTextField addressField = new JTextField(acc.getAddress());

        Object[] message = {
            "Full Name:", nameField,
            "Phone Number:", phoneField,
            "Email:", emailField,
            "Address:", addressField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Update User: " + acc.getAccountNumber(), JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            acc.setAccountHolder(nameField.getText());
            acc.setPhoneNumber(phoneField.getText());
            acc.setEmail(emailField.getText());
            acc.setAddress(addressField.getText());
            
            if (dao.updateAccountInfo(acc)) {
                JOptionPane.showMessageDialog(this, "Account updated successfully!");
                loadUsers("");
            }
        }
    }
}
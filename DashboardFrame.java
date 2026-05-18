package com.mycompany.cbe_banking_system;

import com.bank.model.Account;
import com.bank.db.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class DashboardFrame extends JFrame {
    private Account currentAccount;
    private JEditorPane displayArea; 
    private JPanel mainContentPanel; 

    public DashboardFrame(Account account) {
        this.currentAccount = account;
        
        setTitle("CBE Digital Banking");
        setSize(500, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(108, 52, 131));

        // --- Header Section ---
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        try {
            java.net.URL imgURL = getClass().getResource("/images/cbe_logo.jpg");
            if (imgURL != null) {
                JLabel lblLogo = new JLabel(new ImageIcon(new ImageIcon(imgURL).getImage().getScaledInstance(140, 60, Image.SCALE_SMOOTH)));
                lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
                header.add(lblLogo);
            }
        } catch (Exception e) {}

        JLabel lblTitle = new JLabel("COMMERCIAL BANK OF ETHIOPIA");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Serif", Font.BOLD, 18));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(lblTitle);
        add(header, BorderLayout.NORTH);

        // --- Middle Section ---
        mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setOpaque(false);
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        displayArea = new JEditorPane(); 
        displayArea.setContentType("text/html");
        displayArea.setEditable(false);
        displayArea.setBackground(Color.WHITE);
        displayArea.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        showWelcomeMessage();

        mainContentPanel.add(new JScrollPane(displayArea), BorderLayout.CENTER);
        add(mainContentPanel, BorderLayout.CENTER);

        // --- Buttons Section ---
        JPanel btnGrid = new JPanel(new GridLayout(3, 2, 10, 10)); 
        btnGrid.setOpaque(false);
        btnGrid.setBorder(BorderFactory.createEmptyBorder(10, 20, 30, 20));

        JButton btnAirtime = createBtn("Airtime", new Color(243, 156, 18));
        // ከላይ ከሌሎች በተኖች ጋር እንዲህ መጻፍ አለበት
        JButton btnOtherBank = createBtn("Other Bank", new Color(44, 62, 80));
        JButton btnCbeAccount = createBtn("Transfer CBE Account", new Color(142, 68, 173));
        JButton btnMyAccount = createBtn("My Account", new Color(52, 73, 94));
        JButton btnLogout = createBtn("Logout", Color.DARK_GRAY);
        JButton btnCBEBirr = createBtn("CBE Birr", new Color(142, 68, 173));
     btnGrid.add(btnCBEBirr);
        btnGrid.add(btnAirtime);       
        btnGrid.add(btnCbeAccount);   btnGrid.add(btnMyAccount);
        btnGrid.add(btnCBEBirr);     btnGrid.add(btnLogout);
        btnGrid.add(btnOtherBank);
        add(btnGrid, BorderLayout.SOUTH);

        // --- Action Listeners ---

        btnMyAccount.addActionListener(e -> refreshDashboardUI());

        // 1. Transfer CBE Account (አሁን የተጨመረው)
        // መስመር 86 ላይ እንዲህ አስተካክለው
     btnCbeAccount.addActionListener(e -> {
    new TransferCBEFrame(currentAccount).setVisible(true);
    this.dispose();
       });

       // 2. Transfer CBE Birr
// CBE Birr በተን ሲነካ የሚሰራው
     btnCBEBirr.addActionListener(e -> {
    new TransferCBEBirrFrame(currentAccount).setVisible(true);
    this.dispose(); 
});
        btnAirtime.addActionListener(e -> {
            new AirtimeFrame(currentAccount).setVisible(true);
            dispose();
        });
        // በ DashboardFrame ውስጥ ባለ በተን ላይ የሚጻፍ
      btnOtherBank.addActionListener(e -> {
    new TransferOtherBankFrame(currentAccount).setVisible(true);
    dispose();
});

        btnLogout.addActionListener(e -> {
            dispose();
            // LoginFrame().setVisible(true);
        });
    }

    public void refreshDashboardUI() {
        mainContentPanel.removeAll();
        mainContentPanel.add(new JScrollPane(displayArea), BorderLayout.CENTER);
        
        displayArea.setText("<html><body style='text-align:center;'><br><br>🔄 Fetching data...</body></html>");
        
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                return getAccountInfoHTML();
            }
            @Override
            protected void done() {
                try { displayArea.setText(get()); } 
                catch (Exception ex) { displayArea.setText("<html><body>Error loading details.</body></html>"); }
            }
        };
        worker.execute();
        
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    private void showWelcomeMessage() {
        displayArea.setText("<html><body style='font-family:monospaced; text-align:center; color:#6C3483;'>"
                + "<br><br><br><b>Welcome to CBE Mobile Banking</b><br>"
                + "Select 'My Account' to view details.</body></html>");
    }

    private String getAccountInfoHTML() {
        if (currentAccount == null) return "<html><body>Error: No account data.</body></html>";
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-family:sans-serif; color:#444;'>");
        sb.append("<h3 style='text-align:center; color:#6C3483;'>👤 ACCOUNT PROFILE</h3><hr>");
        sb.append("<b>Name:</b> ").append(currentAccount.getAccountHolderName()).append("<br>");
        sb.append("<b>Acc No:</b> ").append(currentAccount.getAccountNumber()).append("<br>");
        sb.append("<b>Balance:</b> <span style='color:blue;'>").append(currentAccount.getBalance()).append(" ETB</span><br><br>");
        sb.append("<h3 style='text-align:center; color:#6C3483;'>🕒 RECENT TRANSACTIONS</h3><table width='100%'>");

        try (Connection conn = DatabaseConnection.getConnection()) { 
            String sql = "SELECT TOP 3 SenderAccount, ReceiverAccount, Amount FROM [dbo].[Transactions] " +
                         "WHERE SenderAccount = ? OR ReceiverAccount = ? ORDER BY TransactionDate DESC";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, currentAccount.getAccountNumber());
            pst.setString(2, currentAccount.getAccountNumber());
            ResultSet rs = pst.executeQuery();
            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                String sender = rs.getString("SenderAccount");
                double amt = rs.getDouble("Amount");
                sb.append("<tr><td>");
                if (sender.equals(currentAccount.getAccountNumber())) sb.append("<b style='color:red;'>Sent: -").append(amt).append(" ETB</b>");
                else sb.append("<b style='color:green;'>Received: +").append(amt).append(" ETB</b>");
                sb.append("</td></tr>");
            }
            if (!hasData) sb.append("<tr><td style='text-align:center; color:gray;'>No recent transactions found.</td></tr>");
        } catch (Exception ex) { sb.append("<tr><td style='text-align:center; color:red;'>Database Error!</td></tr>"); }
        
        sb.append("</table></body></html>");
        return sb.toString();
    }

    private JButton createBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        return b;
    }
}
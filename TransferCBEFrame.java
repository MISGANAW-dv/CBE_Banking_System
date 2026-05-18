package com.mycompany.cbe_banking_system;

import com.bank.db.AccountDAO;
import com.bank.model.Account;
import javax.swing.*;
import java.awt.*;

/**
 * Modern CBE to CBE Transfer Frame
 */
public class TransferCBEFrame extends JFrame {
    private Account sender;
    private Account receiver;
    private AccountDAO dao = new AccountDAO();
    private JTextField txtAccNum, txtAmount;
    private JLabel lblReceiverName;
    private JButton btnTransfer, btnVerify;

    public TransferCBEFrame(Account currentAccount) {
        this.sender = currentAccount;
        setTitle("CBE Transfer Service");
        setSize(450, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(245, 246, 250));
        setLayout(new BorderLayout(10, 10));

        // --- Header Panel ---
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(41, 128, 185));
        JLabel lblHeader = new JLabel("CBE to CBE Transfer");
        lblHeader.setForeground(Color.WHITE);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerPanel.add(lblHeader);
        add(headerPanel, BorderLayout.NORTH);

        // --- Center Content Panel ---
        JPanel centerPanel = new JPanel(new GridLayout(7, 1, 5, 5));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        centerPanel.setOpaque(false);

        // 1. Account Input
        centerPanel.add(new JLabel("Receiver Account Number (13 digits):"));
        txtAccNum = new JTextField();
        txtAccNum.setFont(new Font("Consolas", Font.PLAIN, 16));
        centerPanel.add(txtAccNum);

        // 2. Verify Button
        btnVerify = new JButton("Check Account");
        btnVerify.setFocusPainted(false);
        btnVerify.setBackground(new Color(52, 152, 219));
        btnVerify.setForeground(Color.WHITE);
        btnVerify.setFont(new Font("Segoe UI", Font.BOLD, 14));
        centerPanel.add(btnVerify);

        // 3. Name Label
        lblReceiverName = new JLabel("Verification Status: Pending", SwingConstants.CENTER);
        lblReceiverName.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblReceiverName.setForeground(new Color(127, 140, 141));
        centerPanel.add(lblReceiverName);

        // 4. Amount Input
        centerPanel.add(new JLabel("Amount (ETB):"));
        txtAmount = new JTextField();
        txtAmount.setFont(new Font("Consolas", Font.PLAIN, 16));
        centerPanel.add(txtAmount);

        // 5. Transfer Button (Initially Disabled)
        btnTransfer = new JButton("Confirm Transfer");
        btnTransfer.setEnabled(false); // DISABLED UNTIL VERIFIED
        btnTransfer.setFocusPainted(false);
        btnTransfer.setBackground(new Color(44, 62, 80));
        btnTransfer.setForeground(Color.WHITE);
        btnTransfer.setFont(new Font("Segoe UI", Font.BOLD, 14));
        centerPanel.add(btnTransfer);

        add(centerPanel, BorderLayout.CENTER);

        // --- Footer Panel ---
        JPanel footerPanel = new JPanel();
        footerPanel.add(new JLabel("Secure Transaction Powered by CBE"));
        add(footerPanel, BorderLayout.SOUTH);

        // --- Action Listeners ---

        // Verify Logic
        btnVerify.addActionListener(e -> {
            String accNo = txtAccNum.getText().trim();

            if (accNo.length() != 13 || !accNo.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "Error: Account number must be exactly 13 digits!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                resetTransferState();
                return;
            }

            receiver = dao.getAccount(accNo);
            if (receiver != null) {
                if (receiver.getAccountNumber().equals(sender.getAccountNumber())) {
                    JOptionPane.showMessageDialog(this, "Restriction: You cannot transfer money to your own account!", "Self Transfer", JOptionPane.WARNING_MESSAGE);
                    resetTransferState();
                    return;
                }
                
                // If account is found
                lblReceiverName.setText("Receiver: " + receiver.getAccountHolder().toUpperCase());
                lblReceiverName.setForeground(new Color(39, 174, 96)); // Green
                btnTransfer.setEnabled(true); // ENABLE TRANSFER BUTTON
                btnTransfer.setBackground(new Color(41, 128, 185)); // Make it look active
            } else {
                JOptionPane.showMessageDialog(this, "Error: Account not found in our database!", "Not Found", JOptionPane.ERROR_MESSAGE);
                resetTransferState();
            }
        });

        // Transfer Logic
        btnTransfer.addActionListener(e -> {
            String amountStr = txtAmount.getText().trim();
            
            try {
                double amount = Double.parseDouble(amountStr);
                
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Error: Please enter a valid amount greater than 0.", "Invalid Amount", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (amount > sender.getBalance()) {
                    JOptionPane.showMessageDialog(this, "Transaction Failed: Insufficient balance!\nYour current balance is: " + sender.getBalance() + " ETB", "Balance Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Execute Transfer
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to transfer " + amount + " ETB to " + receiver.getAccountHolder() + "?", "Confirm Transaction", JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    if (dao.transferMoney(sender.getAccountNumber(), receiver.getAccountNumber(), amount)) {
                        JOptionPane.showMessageDialog(this, "Success: " + amount + " ETB transferred to " + receiver.getAccountHolder() + " successfully.");
                        
                        // Refresh Dashboard and Close
                        new DashboardFrame(dao.getAccount(sender.getAccountNumber())).setVisible(true);
                        this.dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "System Error: Transaction failed. Please try again later.", "Server Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Error: Please enter a valid numerical amount.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // Helper method to disable transfer if verification fails or changes
    private void resetTransferState() {
        receiver = null;
        lblReceiverName.setText("Verification Status: Failed");
        lblReceiverName.setForeground(Color.RED);
        btnTransfer.setEnabled(false);
        btnTransfer.setBackground(new Color(44, 62, 80));
    }
}
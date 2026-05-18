package com.mycompany.cbe_banking_system;

import com.bank.model.Account;
import com.bank.db.AccountDAO;
import com.bank.db.TransactionDAO; // የታሪክ መመዝገቢያውን ዳኦ መጥራት
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal; // ለትክክለኛ የገንዘብ መጠን

public class TransferOtherBankFrame extends JFrame {
    private Account currentUser;
    private JComboBox<String> cbBanks;
    private JTextField txtReceiverAcc, txtAmount;
    private AccountDAO dao = new AccountDAO();
    private TransactionDAO transDao = new TransactionDAO(); // ታሪክ ለመመዝገብ የተጨመረ

    public TransferOtherBankFrame(Account user) {
        this.currentUser = user;

        setTitle("Transfer to Other Banks");
        setSize(450, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(9, 1, 10, 15));
        getContentPane().setBackground(new Color(108, 52, 131));
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(20, 40, 30, 40));

        JLabel lblTitle = new JLabel("Other Bank Transfer", JLabel.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 20));

        // --- የባንኮች ዝርዝር ---
        String[] banks = {"Abyssinia Bank", "Awash Bank", "Birhan Bank", "Enat Bank", "Dashen Bank", "Hibret Bank"};
        cbBanks = new JComboBox<>(banks);
        cbBanks.setBorder(BorderFactory.createTitledBorder("Select Destination Bank"));

        // --- የአካውንት ቁጥር መቀበያ ---
        txtReceiverAcc = new JTextField();
        txtReceiverAcc.setBorder(BorderFactory.createTitledBorder("Receiver Account Number"));

        // --- የብር መጠን መቀበያ ---
        txtAmount = new JTextField();
        txtAmount.setBorder(BorderFactory.createTitledBorder("Amount (ETB)"));

        JButton btnTransfer = new JButton("Send Money");
        btnTransfer.setBackground(new Color(46, 204, 113));
        btnTransfer.setForeground(Color.WHITE);
        btnTransfer.setFont(new Font("SansSerif", Font.BOLD, 15));

        JButton btnBack = new JButton("Back to Dashboard");
        btnBack.addActionListener(e -> {
            new DashboardFrame(currentUser).setVisible(true);
            dispose();
        });

        add(lblTitle);
        add(cbBanks);
        add(txtReceiverAcc);
        add(txtAmount);
        add(new JLabel("Available: " + currentUser.getBalance() + " ETB", JLabel.RIGHT));
        add(btnTransfer);
        add(btnBack);

        btnTransfer.addActionListener(e -> handleTransfer());
    }

    private void handleTransfer() {
        String bankName = cbBanks.getSelectedItem().toString();
        String receiverAcc = txtReceiverAcc.getText().trim();
        String amountStr = txtAmount.getText().trim();

        if (receiverAcc.isEmpty() || amountStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!");
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            
            if (amount > currentUser.getBalance()) {
                JOptionPane.showMessageDialog(this, "Insufficient balance!");
                return;
            }

            // 1. ትራንዛክሽኑን ማካሄድ (በ AccountDAO በኩል)
            if (dao.processExternalTransfer(currentUser.getAccountNumber(), amount, bankName, receiverAcc)) {
                
                // 2. ታሪክ መዝግብ (በ TransactionDAO በኩል)
                String description = "Transfer to " + bankName + " (" + receiverAcc + ")";
                transDao.recordTransaction(
                    currentUser.getAccountNumber(), // ላኪ
                    receiverAcc,                    // ተቀባይ
                    "Other Bank",                    // አይነት
                    new BigDecimal(amount),          // መጠን
                    description                      // መግለጫ
                );

                JOptionPane.showMessageDialog(this, "Successfully transferred " + amount + " ETB to " + bankName);
                
                currentUser.setBalance(currentUser.getBalance() - amount);
                new DashboardFrame(currentUser).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Transfer failed!");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount!");
        }
    }
}
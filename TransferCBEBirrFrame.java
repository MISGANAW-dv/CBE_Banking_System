package com.mycompany.cbe_banking_system;

import com.bank.model.Account;
import com.bank.db.AccountDAO;
import com.bank.db.TransactionDAO; // አዲሱን DAO ለመጠቀም
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal; // ለገንዘብ መጠን ትክክለኛነት

public class TransferCBEBirrFrame extends JFrame {
    private Account currentUser;
    private JTextField txtPhone, txtAmount;
    private JComboBox<String> cbOptions;
    private AccountDAO dao = new AccountDAO();
    private TransactionDAO transDao = new TransactionDAO(); // ታሪክ ለመመዝገብ የተጨመረ

    public TransferCBEBirrFrame(Account user) {
        this.currentUser = user;

        setTitle("CBE Birr Transfer");
        setSize(450, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(9, 1, 10, 15));
        getContentPane().setBackground(new Color(108, 52, 131)); 
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(20, 40, 30, 40));

        JLabel lblTitle = new JLabel("CBE Birr Transfer", JLabel.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 20));

        cbOptions = new JComboBox<>(new String[]{"Another Account", "My Account"});
        cbOptions.setBorder(BorderFactory.createTitledBorder("Transfer To"));

        txtPhone = new JTextField("+251");
        txtPhone.setFont(new Font("Monospaced", Font.BOLD, 14));
        txtPhone.setBorder(BorderFactory.createTitledBorder("CBE Birr Phone Number"));

        txtAmount = new JTextField();
        txtAmount.setBorder(BorderFactory.createTitledBorder("Amount (ETB)"));

        JButton btnSend = new JButton("Send to CBE Birr");
        btnSend.setBackground(new Color(39, 174, 96)); 
        btnSend.setForeground(Color.WHITE);
        btnSend.setFont(new Font("SansSerif", Font.BOLD, 15));

        JButton btnBack = new JButton("Back to Dashboard");
        btnBack.addActionListener(e -> {
            new DashboardFrame(currentUser).setVisible(true);
            dispose();
        });

        add(lblTitle);
        add(cbOptions);
        add(txtPhone);
        add(txtAmount);
        add(new JLabel("Balance: " + currentUser.getBalance() + " ETB", JLabel.RIGHT));
        add(btnSend);
        add(btnBack);

        cbOptions.addActionListener(e -> {
            if (cbOptions.getSelectedItem().equals("My Account")) {
                txtPhone.setText(currentUser.getPhoneNumber()); 
                txtPhone.setEditable(false);
            } else {
                txtPhone.setText("+251");
                txtPhone.setEditable(true);
            }
        });

        btnSend.addActionListener(e -> handleBirrTransfer());
    }

    private void handleBirrTransfer() {
        String phone = txtPhone.getText().trim();
        String amountStr = txtAmount.getText().trim();

        if (!phone.startsWith("+251") || phone.length() != 13) {
            JOptionPane.showMessageDialog(this, "Phone number must be 10 digits and start with +251");
            return;
        }

        if (!phone.substring(4, 5).equals("9")) {
            JOptionPane.showMessageDialog(this, "CBE Birr numbers must start with 9!");
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0 || amount > currentUser.getBalance()) {
                JOptionPane.showMessageDialog(this, "Check amount or balance!");
                return;
            }

            // --- ዳታቤዝ ላይ መቀነስ እና ታሪክ መመዝገብ ---
            if (dao.updateBalance(currentUser.getAccountNumber(), -amount)) {
                
                // 1. ታሪክ መመዝገብ (Transaction History Table ላይ እንዲገባ)
                String description = "CBE Birr Transfer to " + phone;
                transDao.recordTransaction(
                    currentUser.getAccountNumber(), // ላኪ
                    "CBE Birr Wallet",              // ተቀባይ
                    "CBE Birr",                      // አይነት
                    new BigDecimal(amount),         // መጠን
                    description                      // መግለጫ
                );

                JOptionPane.showMessageDialog(this, "Success! Transferred to " + phone);
                currentUser.setBalance(currentUser.getBalance() - amount);
                
                new DashboardFrame(currentUser).setVisible(true);
                dispose();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount!");
        }
    }
}
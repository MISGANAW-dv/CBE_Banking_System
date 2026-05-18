package com.mycompany.cbe_banking_system;

import com.bank.model.Account;
import com.bank.db.AccountDAO;
import com.bank.db.TransactionDAO; // አዲሱን DAO መጥራት ያስፈልጋል
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal; // ለገንዘብ መጠን

public class AirtimeFrame extends JFrame {
    private Account currentUser;
    private JTextField txtPhone, txtAmount;
    private JRadioButton rbEthio, rbSafaricom;
    private JComboBox<String> cbEthioOptions; 
    private AccountDAO dao = new AccountDAO();
    private TransactionDAO transDao = new TransactionDAO(); // ታሪክ ለመመዝገብ

    public AirtimeFrame(Account user) {
        this.currentUser = user;

        setTitle("CBE Airtime Recharge");
        setSize(450, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(11, 1, 10, 15));
        getContentPane().setBackground(new Color(108, 52, 131));
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(20, 40, 30, 40));

        JLabel lblTitle = new JLabel("Recharge Airtime", JLabel.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 20));

        // --- Operator Selection ---
        JPanel operatorPanel = new JPanel(new GridLayout(1, 2));
        operatorPanel.setOpaque(false);
        rbEthio = new JRadioButton("Ethio Telecom", true);
        rbSafaricom = new JRadioButton("Safaricom");
        rbEthio.setForeground(Color.WHITE); rbEthio.setOpaque(false);
        rbSafaricom.setForeground(Color.WHITE); rbSafaricom.setOpaque(false);
        
        ButtonGroup group = new ButtonGroup();
        group.add(rbEthio); group.add(rbSafaricom);
        operatorPanel.add(rbEthio); operatorPanel.add(rbSafaricom);

        // --- Ethio Options ---
        cbEthioOptions = new JComboBox<>(new String[]{"Another Account", "My Account"});
        cbEthioOptions.setBorder(BorderFactory.createTitledBorder("Option (Ethio Only)"));

        // --- Phone Number Field ---
        txtPhone = new JTextField("+251");
        txtPhone.setFont(new Font("Monospaced", Font.BOLD, 14));
        txtPhone.setBorder(BorderFactory.createTitledBorder("Phone Number (+251...)"));

        // --- Logic for Operator Selection ---
        rbSafaricom.addActionListener(e -> {
            cbEthioOptions.setEnabled(false); 
            txtPhone.setText("+251");
            txtPhone.setEditable(true);
        });
        
        rbEthio.addActionListener(e -> cbEthioOptions.setEnabled(true));

        // --- Logic for "My Account" ---
        cbEthioOptions.addActionListener(e -> {
            if (rbEthio.isSelected() && cbEthioOptions.getSelectedItem().equals("My Account")) {
                txtPhone.setText(currentUser.getPhoneNumber()); 
                txtPhone.setEditable(false);
            } else {
                txtPhone.setText("+251");
                txtPhone.setEditable(true);
            }
        });

        txtAmount = new JTextField();
        txtAmount.setBorder(BorderFactory.createTitledBorder("Amount (ETB)"));

        JButton btnRecharge = new JButton("Confirm Recharge");
        styleButton(btnRecharge, new Color(46, 204, 113));

        JButton btnBack = new JButton("Back to Dashboard");
        styleButton(btnBack, Color.LIGHT_GRAY);
        btnBack.setForeground(Color.BLACK);

        add(lblTitle);
        add(operatorPanel);
        add(cbEthioOptions);
        add(txtPhone);
        add(txtAmount);
        add(btnRecharge);
        add(btnBack);

        btnRecharge.addActionListener(e -> validateAndRecharge());
        btnBack.addActionListener(e -> {
            new DashboardFrame(currentUser).setVisible(true);
            dispose();
        });
    }

    private void validateAndRecharge() {
        String phone = txtPhone.getText().trim();
        String amountStr = txtAmount.getText().trim();
        String operator = rbEthio.isSelected() ? "Ethio Telecom" : "Safaricom";

        if (!phone.startsWith("+251") || phone.length() != 13) {
            JOptionPane.showMessageDialog(this, "Error: Phone number must start with +251 and be 13 characters long");
            return;
        }

        String firstDigitAfterPrefix = phone.substring(4, 5);
        if (rbEthio.isSelected() && !firstDigitAfterPrefix.equals("9")) {
            JOptionPane.showMessageDialog(this, "Ethio Telecom numbers must start with 9!");
            return;
        } else if (rbSafaricom.isSelected() && !firstDigitAfterPrefix.equals("7")) {
            JOptionPane.showMessageDialog(this, "Safaricom numbers must start with 7!");
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Please enter a valid amount!");
                return;
            }
            
            if (amount > currentUser.getBalance()) {
                JOptionPane.showMessageDialog(this, "Insufficient balance!");
                return;
            }

            // --- ዳታቤዝ አፕዴት እና የታሪክ መመዝገቢያ ---
            if (dao.updateBalance(currentUser.getAccountNumber(), -amount)) {
                
                // 1. ታሪክ መዝግብ (recordTransaction ተጠቅመን)
                String description = "Airtime " + operator + " for " + phone;
                transDao.recordTransaction(
                    currentUser.getAccountNumber(), 
                    "N/A", 
                    "Airtime", 
                    new BigDecimal(amount), 
                    description
                );

                // 2. የተሳካ መልዕክት አሳይ
                JOptionPane.showMessageDialog(this, "Recharge Successful for " + phone);
                currentUser.setBalance(currentUser.getBalance() - amount);
                
                new DashboardFrame(currentUser).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Transaction failed. Try again.");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount! Please enter a number.");
        }
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setFocusPainted(false);
    }
}
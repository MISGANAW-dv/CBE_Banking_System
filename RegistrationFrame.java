package com.mycompany.cbe_banking_system;

import com.bank.db.AccountDAO;
import com.bank.model.Account;
import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.ParseException;

public class RegistrationFrame extends JFrame {

    private JTextField txtAccNum, txtName, txtEmail, txtAddress, txtBalance;
    private JFormattedTextField txtPhone;
    private JPasswordField txtPassword;
    private JButton btnRegister, btnBackToLogin;

    public RegistrationFrame() {
        setTitle("CBE - Customer Registration");
        setSize(500, 850); // ለተጨማሪ አዝራር መጠኑ ትንሽ ጨምሯል
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // --- Header Panel ---
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/images/cbe_logo.jpg"));
            Image img = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            JLabel lblLogo = new JLabel(new ImageIcon(img));
            lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
            headerPanel.add(lblLogo);
            headerPanel.add(Box.createVerticalStrut(10));
        } catch (Exception e) {
            System.out.println("Logo not found!");
        }

        JLabel lblHeader = new JLabel("Create New Account");
        lblHeader.setForeground(new Color(108, 52, 131));
        lblHeader.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(lblHeader);
        add(headerPanel, BorderLayout.NORTH);

        // --- Form Panel ---
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(7, 1, 10, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));
        formPanel.setBackground(Color.WHITE);

        txtAccNum = createStyledTextField("Account Number (13 Digits)");
        txtName = createStyledTextField("Full Name (Letters Only)");

        try {
            MaskFormatter phoneMask = new MaskFormatter("+251 9########");
            phoneMask.setPlaceholderCharacter('_');
            txtPhone = new JFormattedTextField(phoneMask);
            txtPhone.setBorder(BorderFactory.createTitledBorder("Phone Number (+251 9)"));
        } catch (ParseException e) {
            txtPhone = new JFormattedTextField();
        }

        txtEmail = createStyledTextField("Email Address");
        txtAddress = createStyledTextField("Residential Address");
        txtBalance = createStyledTextField("Initial Deposit (Min 100 ETB)");
        
        txtPassword = new JPasswordField();
        txtPassword.setBorder(BorderFactory.createTitledBorder("Set 4-Digit PIN"));

        formPanel.add(txtAccNum);
        formPanel.add(txtName);
        formPanel.add(txtPhone);
        formPanel.add(txtEmail);
        formPanel.add(txtAddress);
        formPanel.add(txtBalance);
        formPanel.add(txtPassword);

        add(formPanel, BorderLayout.CENTER);

        // --- Footer Panel (ሁለት አዝራሮች ያሉት) ---
        JPanel footerPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 30, 50));

        btnRegister = new JButton("REGISTER NOW");
        btnRegister.setPreferredSize(new Dimension(300, 45));
        btnRegister.setBackground(new Color(108, 52, 131));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFont(new Font("SansSerif", Font.BOLD, 15));
        btnRegister.addActionListener(this::handleRegistration);

        btnBackToLogin = new JButton("ALREADY HAVE AN ACCOUNT? LOGIN");
        btnBackToLogin.setForeground(new Color(108, 52, 131));
        btnBackToLogin.setContentAreaFilled(false);
        btnBackToLogin.setBorderPainted(false);
        btnBackToLogin.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btnBackToLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBackToLogin.addActionListener(e -> {
            new LoginFrame().setVisible(true); // ወደ LoginFrame መመለስ
            this.dispose();
        });

        footerPanel.add(btnRegister);
        footerPanel.add(btnBackToLogin);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JTextField createStyledTextField(String title) {
        JTextField field = new JTextField();
        field.setBorder(BorderFactory.createTitledBorder(title));
        return field;
    }

    private void handleRegistration(ActionEvent e) {
        String accNum = txtAccNum.getText().trim();
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().replaceAll("\\s+|_", ""); 
        String email = txtEmail.getText().trim();
        String address = txtAddress.getText().trim();
        String balanceStr = txtBalance.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        AccountDAO dao = new AccountDAO();

        // 1. ባዶ ቦታ ቼክ ማድረግ
        if (accNum.isEmpty() || name.isEmpty() || email.isEmpty() || password.isEmpty() || balanceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
// የስልክ ቁጥር ቫሊዴሽን (ከ ፎርማት ቼክ ቁጥር 2 ስር ይግባ)
// phone የሚለው ቫሪያብል "+251 9########" ከሚለው ላይ 9 ቁጥሮቹን ብቻ ነው የሚያወጣው
if (phone.length() != 13) { 
    JOptionPane.showMessageDialog(this, "Phone number must be 10 digits (09xxxxxxxx)!", "Phone Error", JOptionPane.ERROR_MESSAGE);
    return;
}
        // 2. ፎርማት ቼክ
        if (!accNum.matches("\\d{13}")) {
            JOptionPane.showMessageDialog(this, "Account Number must be 13 digits!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!password.matches("\\d{4}")) {
            JOptionPane.showMessageDialog(this, "PIN must be 4 digits!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!name.matches("^[a-zA-Z\\s]+$")) {
            JOptionPane.showMessageDialog(this, "Full Name should contain letters only!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
         }
        if (!password.matches("\\d{4}")) {
            JOptionPane.showMessageDialog(this, "PIN must be 4 digits!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!email.matches(emailRegex)) {
            JOptionPane.showMessageDialog(this, "Invalid Email Format! (e.g., example@gmail.com)", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (dao.isAccountExists(accNum)) {
            JOptionPane.showMessageDialog(this, "Account Number already exists!");
            return;
        }
        if (dao.isPhoneNumberExists(phone)) {
            JOptionPane.showMessageDialog(this, "Phone Number already registered!");
            return;
        }
        if (dao.isEmailExists(email)) {
            JOptionPane.showMessageDialog(this, "Email Address already registered!");
            return;
        }
        // ፒን መኖሩን ቼክ ማድረግ (አዲሱ ክፍል)
        if (dao.isPasswordExists(password)) {
            JOptionPane.showMessageDialog(this, "This PIN is not available! Please choose a different 4-digit PIN.", "PIN Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double balance = Double.parseDouble(balanceStr);
            if (balance < 100) {
                JOptionPane.showMessageDialog(this, "Minimum deposit is 100 ETB!");
                return;
            }

            Account newAcc = new Account(accNum, name, phone, email, address, balance);
            if (dao.saveAccount(newAcc, password)) {
                JOptionPane.showMessageDialog(this, "Registration Successful! Moving to Login...");
                
                // ምዝገባው እንደተጠናቀቀ በቀጥታ ወደ Login መውሰድ
                new LoginFrame().setVisible(true);
                this.dispose();
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid balance amount!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
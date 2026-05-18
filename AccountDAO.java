package com.bank.db;

import java.util.List;
import java.util.ArrayList;
import com.bank.model.Account;
import java.sql.*;

public class AccountDAO {

    // 1. አካውንቱ መኖሩን ቼክ ለማድረግ
    public boolean isAccountExists(String accNum) {
        String sql = "SELECT * FROM Accounts WHERE AccountNumber = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setString(1, accNum);
            ResultSet rs = pst.executeQuery();
            return rs.next();
            
        } catch (SQLException e) {
            System.out.println("Check Error: " + e.getMessage());
            return false;
        }
    }

    // 2. አዲስ አካውንት ለመመዝገብ
    public boolean saveAccount(Account acc, String password) {
        if (isAccountExists(acc.getAccountNumber())) {
            System.out.println("Registration Error: Account Number already exists.");
            return false;
        }

        String sql = "INSERT INTO Accounts (AccountNumber, AccountHolder, PhoneNumber, Email, Address, Balance, Password) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setString(1, acc.getAccountNumber());
            pst.setString(2, acc.getAccountHolder());
            pst.setString(3, acc.getPhoneNumber());
            pst.setString(4, acc.getEmail());
            pst.setString(5, acc.getAddress());
            pst.setDouble(6, acc.getBalance());
            pst.setString(7, password);
            
            return pst.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.out.println("Registration Error: " + e.getMessage());
            return false;
        }
    }

    // 3. ስልክ ቁጥር ቀድሞ መኖሩን ለማረጋገጥ
    public boolean isPhoneNumberExists(String PhoneNumber) {
        String sql = "SELECT count(*) FROM accounts WHERE PhoneNumber = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, PhoneNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 4. ኢሜይል ቀድሞ መኖሩን ለማረጋገጥ
    public boolean isEmailExists(String email) {
        String sql = "SELECT count(*) FROM accounts WHERE Email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 5. ፒን/ፓስወርድ ዳታቤዝ ውስጥ መኖሩን ቼክ ለማድረግ
    public boolean isPasswordExists(String password) {
        String sql = "SELECT Password FROM accounts WHERE Password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) { 
            return false; 
        }
    }

    // 6. በፓስወርድ ብቻ ሎግኢን ለማድረግ
    public Account validateLoginWithOnlyPassword(String password) {
        String sql = "SELECT * FROM Accounts WHERE Password = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setString(1, password);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                return new Account(
                    rs.getString("AccountNumber"),
                    rs.getString("AccountHolder"),
                    rs.getString("PhoneNumber"),
                    rs.getString("Email"),
                    rs.getString("Address"),
                    rs.getDouble("Balance")
                );
            }
        } catch (SQLException e) {
            System.out.println("Login Error: " + e.getMessage());
        }
        return null;
    }

    // 7. የአካውንት መረጃ በቁጥር ለመፈለግ
    public Account getAccount(String accNum) {
        String sql = "SELECT * FROM Accounts WHERE AccountNumber = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setString(1, accNum);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                return new Account(
                    rs.getString("AccountNumber"),
                    rs.getString("AccountHolder"),
                    rs.getString("PhoneNumber"),
                    rs.getString("Email"),
                    rs.getString("Address"),
                    rs.getDouble("Balance")
                );
            }
        } catch (SQLException e) {
            System.out.println("Fetch Error: " + e.getMessage());
        }
        return null;
    }

    // 8. የገንዘብ መጠን ማስተካከያ (Update Balance)
    public boolean updateBalance(String accountNumber, double amount) {
        String sql = "UPDATE Accounts SET Balance = Balance + ? WHERE AccountNumber = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, amount);
            pstmt.setString(2, accountNumber);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 9. ሁሉንም አካውንቶች ለማምጣት
    public List<Account> getAllAccounts() {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT * FROM Accounts";
        try (Connection con = DatabaseConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Account(
                    rs.getString("AccountNumber"),
                    rs.getString("AccountHolder"),
                    rs.getString("PhoneNumber"),
                    rs.getString("Email"),
                    rs.getString("Address"),
                    rs.getDouble("Balance")
                ));
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return list;
    }

    // 10. የአካውንት መረጃ አፕዴት ለማድረግ
    public boolean updateAccountInfo(Account acc) {
        String sql = "UPDATE Accounts SET AccountHolder=?, PhoneNumber=?, Email=?, Address=? WHERE AccountNumber=?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, acc.getAccountHolder());
            pst.setString(2, acc.getPhoneNumber());
            pst.setString(3, acc.getEmail());
            pst.setString(4, acc.getAddress());
            pst.setString(5, acc.getAccountNumber());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) { 
            return false; 
        }
    }

    // 11. *** የታረመው የትራንዛክሽን መመዝገቢያ (ለ Airtime እና CBE Birr) ***
    public boolean saveTransaction(String accNo, String type, double amount, String description) {
        // ከ TransactionDate በኋላ የነበረው ትርፍ ኮማ ተወግዷል
        String sql = "INSERT INTO Transactions (SenderAccount,ReceiverAccount,TransactionType, Amount, Description, TransactionDate) VALUES (?,?, ?, ?, ?, GETDATE())";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accNo);
            pstmt.setString(2, type);
            pstmt.setDouble(3, amount);
            pstmt.setString(4, description);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("History Save Error: " + e.getMessage());
            return false;
        }
    }

public boolean processExternalTransfer(String senderAcc, double amount, String bankName, String receiverAcc) {
    // 1. መጀመሪያ ከአካውንቱ ላይ ብር ይቀንሳል
    if (updateBalance(senderAcc, -amount)) {
        
        // 2. ዳታቤዝ ውስጥ የሚገባው የ SQL ትዕዛዝ (ሁሉንም 5 ኮለምኖች የያዘ)
        String sql = "INSERT INTO Transactions (SenderAccount, ReceiverAccount, TransactionType, Amount, Description, TransactionDate) VALUES (?, ?, ?, ?, ?, GETDATE())";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, senderAcc);      // SenderAccount
            pstmt.setString(2, receiverAcc);    // ReceiverAccount (ይህ ነው NULL የነበረው)
            pstmt.setString(3, "External Transfer"); 
            pstmt.setDouble(4, amount);         // Amount
            pstmt.setString(5, "Transfer to " + bankName); // Description
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    return false;
}

    // 13. የውስጥ ባንክ ትራንስፈር (Internal Transfer)
    public boolean transferMoney(String senderAcc, String receiverAcc, double amount) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); 

            // 1. ከላኪው ቀንሰው
            updateBalance(senderAcc, -amount);

            // 2. ለተቀባዩ ጨምር
            updateBalance(receiverAcc, amount);

            // 3. ታሪክ መዝግብ
           String historySql = "INSERT INTO Transactions (SenderAccount, ReceiverAccount, TransactionType, Amount, Description, TransactionDate) VALUES (?, ?, ?, ?, ?, GETDATE())";

try (PreparedStatement pst3 = conn.prepareStatement(historySql)) {
    // 1. ለላኪው (Sender) የሚመዘገብ ታሪክ
    pst3.setString(1, senderAcc);      // SenderAccount
    pst3.setString(2, receiverAcc);    // ReceiverAccount
    pst3.setString(3, "Transfer Out"); // TransactionType
    pst3.setDouble(4, amount);         // Amount
    pst3.setString(5, "Sent to " + receiverAcc); // Description (ይህ 5ኛው parameter ነው)
    pst3.executeUpdate();
    
    // 2. ለተቀባዩ (Receiver) የሚመዘገብ ታሪክ
    pst3.setString(1, receiverAcc);    // SenderAccount (ለሱ ገቢ ስለሆነ እሱ ጋር ይገባል)
    pst3.setString(2, senderAcc);      // ReceiverAccount (ከማን እንደመጣ)
    pst3.setString(3, "Transfer In");  // TransactionType
    pst3.setDouble(4, amount);         // Amount
    pst3.setString(5, "Received from " + senderAcc); // Description (5ኛው parameter)
    pst3.executeUpdate();
}

            conn.commit(); 
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            System.out.println("Transfer Error: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try { 
                    conn.setAutoCommit(true);
                    conn.close(); 
                } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    // 14. የትራንዛክሽን ታሪክ ማሳያ (History)
    public String getAccountHistory(String accNum) {
        StringBuilder sb = new StringBuilder();
        String sql = "SELECT TOP 5 TransactionType, Amount, TransactionDate FROM Transactions " +
                     "WHERE AccountNumber = ? ORDER BY TransactionDate DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, accNum);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                String type = rs.getString("TransactionType");
                double amt = rs.getDouble("Amount");
                String date = rs.getString("TransactionDate");
                sb.append(String.format("%s: %.2f ETB (%s)\n", type, amt, date));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sb.length() == 0 ? "No recent transactions." : sb.toString();
    }
}
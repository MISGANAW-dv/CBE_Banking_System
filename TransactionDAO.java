package com.bank.db;

import java.sql.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
import java.math.BigDecimal;

 
public class TransactionDAO {

  
    private final String[] adminColumnNames = {
        "ID", "Sender Acc", "Receiver Acc", "Type", "Amount", "Description", "Date"
    };

    
    public boolean recordTransaction(String senderAcc, String receiverAcc, String type, 
                                   BigDecimal amount, String desc) {
        
        String sql = "INSERT INTO [BankDB].[dbo].[Transactions] " +
                     "(SenderAccount, ReceiverAccount, TransactionType, Amount, Description, TransactionDate) " +
                     "VALUES (?, ?, ?, ?, ?, GETDATE())";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, senderAcc);
            pstmt.setString(2, receiverAcc);
            pstmt.setString(3, type);
            pstmt.setBigDecimal(4, amount);
            pstmt.setString(5, desc);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            handleError("ትራንዛክሽን መመዝገብ አልተቻለም", e);
            return false;
        }
    }

     
    public DefaultTableModel getAllTransactionHistory() {
        DefaultTableModel model = new DefaultTableModel(adminColumnNames, 0);
        
        // SenderName እና ReceiverName እዚህ Query ውስጥ የሉም
        String sql = "SELECT ID, SenderAccount, ReceiverAccount, TransactionType, Amount, " +
                     "Description, TransactionDate " +
                     "FROM [BankDB].[dbo].[Transactions] ORDER BY TransactionDate DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                model.addRow(extractRowData(rs));
            }
            System.out.println("Data loaded successfully!"); 
        } catch (SQLException e) {
            handleError("የዳታቤዝ ስህተት (All History)", e);
        }
        return model;
    }

     
    public DefaultTableModel searchTransactions(String accNum) {
        DefaultTableModel model = new DefaultTableModel(adminColumnNames, 0);
        String sql = "SELECT ID, SenderAccount, ReceiverAccount, TransactionType, Amount, Description, TransactionDate " +
                     "FROM [BankDB].[dbo].[Transactions] WHERE SenderAccount LIKE ? OR ReceiverAccount LIKE ? " +
                     "ORDER BY TransactionDate DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + accNum + "%");
            pstmt.setString(2, "%" + accNum + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    model.addRow(extractRowData(rs));
                }
            }
        } catch (SQLException e) {
            handleError("የፍለጋ ስህተት", e);
        }
        return model;
    }

    
    public DefaultTableModel getTransactionHistory(String accNum) {
        // የተጠቃሚው ኮለም ስሞች (Account ቁጥሮችን ብቻ ይይዛል)
        String[] userColumnNames = {"ID", "Sender Acc", "Receiver Acc", "Type", "Amount", "Description", "Date"};
        DefaultTableModel model = new DefaultTableModel(userColumnNames, 0);

        String sql = "SELECT ID, SenderAccount, ReceiverAccount, TransactionType, Amount, Description, TransactionDate " +
                     "FROM [BankDB].[dbo].[Transactions] " +
                     "WHERE SenderAccount = ? OR ReceiverAccount = ? " + 
                     "ORDER BY TransactionDate DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accNum);
            pstmt.setString(2, accNum);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    model.addRow(extractRowData(rs));
                }
            }
        } catch (SQLException e) {
            handleError("የዳታቤዝ ስህተት (User History)", e);
        }
        return model;
    }

     
    private Object[] extractRowData(ResultSet rs) throws SQLException {
        return new Object[]{
            rs.getInt("ID"),
            rs.getString("SenderAccount"),
            rs.getString("ReceiverAccount"),
            rs.getString("TransactionType"),
            rs.getBigDecimal("Amount"),
            rs.getString("Description"),
            rs.getTimestamp("TransactionDate")
        };
    }

    private void handleError(String message, Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, message + "፦ " + e.getMessage());
    }
}
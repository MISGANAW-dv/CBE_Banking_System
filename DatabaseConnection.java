package com.bank.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    
    // ለ SQL Server የሚሆን ኮድ (እንደ ዳታቤዝህ ስም ቀይረው)
   // በ DatabaseConnection.java ውስጥ
private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=BankDB;encrypt=true;trustServerCertificate=true;";
    private static final String USER = "sa"; // ያንተ የ SQL ተጠቃሚ ስም
    private static final String PASS = ""; // ያንተ የ SQL ፓስወርድ

    public static Connection getConnection() throws SQLException {
        try {
            // SQL Driver መጫኑን ያረጋግጣል
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            return DriverManager.getConnection(URL, USER, PASS);
       } catch (ClassNotFoundException e) {
    System.err.println("Driver Error: " + e.getMessage());
    throw new SQLException("SQL Driver Not Found!");
} catch (SQLException e) {
    System.err.println("Connection Error: " + e.getMessage()); // ትክክለኛውን ስህተት እዚህ ያሳየሃል
    throw e;
}}}  

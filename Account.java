package com.bank.model;

/**
 * የባንክ አካውንት መረጃዎችን የሚይዝ ሞዴል ክፍል (Model Class)
 */
public class Account {
    private String accountNumber;
    private String accountHolder;
    private String phoneNumber;
    private String email;
    private String address;
    private double balance;

    // የተሟላ መረጃ የሚቀበል ኮንስትራክተር
    public Account(String accountNumber, String accountHolder, String phoneNumber, 
                   String email, String address, double initialBalance) {
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.balance = (initialBalance >= 0) ? initialBalance : 0;
    }

    // --- Getters ---
    // እነዚህን በ Account.java ውስጥ ጨምር
public void setAccountHolder(String accountHolder) {
    this.accountHolder = accountHolder;
}

public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
}

public void setEmail(String email) {
    this.email = email;
}

public void setAddress(String address) {
    this.address = address;
}
    public String getAccountNumber() { 
        return accountNumber; 
    }

    public String getAccountHolder() { 
        return accountHolder; 
    }

    public String getPhoneNumber() { 
        return phoneNumber; 
    }

    public String getEmail() { 
        return email; 
    }

    public String getAddress() { 
        return address; 
    }

    public double getBalance() { 
        return balance; 
    }

    // --- Setters ---
    
    public void setBalance(double balance) { 
        this.balance = balance; 
    }

    /**
     * Dashboard ላይ ስህተት እንዳይፈጠር የተስተካከለ ሜተድ
     * አሁን 'UnsupportedOperationException' ከመወርወር ይልቅ ስሙን ይመልሳል
     */
    public String getAccountHolderName() {
        return accountHolder; 
    }
}
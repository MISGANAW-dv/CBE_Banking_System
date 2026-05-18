package com.mycompany.cbe_banking_system;

import javax.swing.SwingUtilities;

public class CBE_Banking_System {
    public static void main(String[] args) {
        // ፕሮጀክቱ ሲነሳ ቀጥታ Login ገጽ እንዲከፈት ያደርጋል
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
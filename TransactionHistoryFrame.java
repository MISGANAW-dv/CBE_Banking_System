package com.mycompany.cbe_banking_system;

import com.bank.db.TransactionDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class TransactionHistoryFrame extends JFrame {
    private JTable historyTable;
    private DefaultTableModel mainModel;
    private JTextField txtSearch;
    private int fontSize = 14; 
    private TransactionDAO dao = new TransactionDAO();

    private final String[] columnNames = {
        "ID", "Sender Acc", "Receiver Acc", "Type", 
        "Amount", "Description", "Date", "Sender Name", "Receiver Name"
    };

    public TransactionHistoryFrame() {
        setTitle("CBE System - All Transaction History");
        setSize(1200, 800); 
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- 1. Header Panel (Search & Zoom) ---
        JPanel topPanel = new JPanel(new BorderLayout(15, 15));
        topPanel.setBackground(new Color(103, 58, 183)); 
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlSearch.setOpaque(false);
        JLabel lblSearch = new JLabel("Search by Account No: "); // ፍለጋው በአካውንት መሆኑን ለመግለጽ
        lblSearch.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblSearch.setForeground(Color.WHITE);
        txtSearch = new JTextField(25);
        pnlSearch.add(lblSearch);
        pnlSearch.add(txtSearch);

        JPanel pnlZoom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlZoom.setOpaque(false);
        JButton btnIn = new JButton("+");
        JButton btnOut = new JButton("-");
        styleButton(btnIn, 50, 30);
        styleButton(btnOut, 50, 30);
        
        JLabel lblZoom = new JLabel("Zoom: ");
        lblZoom.setForeground(Color.WHITE);
        pnlZoom.add(lblZoom);
        pnlZoom.add(btnIn);
        pnlZoom.add(btnOut);

        topPanel.add(pnlSearch, BorderLayout.WEST);
        topPanel.add(pnlZoom, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // --- 2. Table Section ---
        mainModel = dao.getAllTransactionHistory(); 
        historyTable = new JTable(mainModel);
        historyTable.setRowHeight(30);
        historyTable.setFont(new Font("SansSerif", Font.PLAIN, fontSize));
        historyTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));

        JScrollPane scrollPane = new JScrollPane(historyTable);
        add(scrollPane, BorderLayout.CENTER);

        // --- 3. Bottom Panel (Back to Dashboard Button) ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton btnBack = new JButton("Back to Dashboard");
        btnBack.setBackground(new Color(255, 193, 7)); // ቢጫ ከለር
        btnBack.setForeground(Color.BLACK);
        styleButton(btnBack, 200, 40);
        
        bottomPanel.add(btnBack);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- 4. Logic Events ---

        // በአካውንት ቁጥር ብቻ የመፈለግ ሎጂክ
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String query = txtSearch.getText().trim();
                filterByAccount(query);
            }
        });

        // ወደ ዳሽቦርድ መመለሻ
        btnBack.addActionListener(e -> {
            this.dispose(); // የአሁኑን ይዘጋዋል
            // እዚህ ጋር የአድሚን ዳሽቦርድህን መጥራት ትችላለህ
            // new AdminDashboardFrame().setVisible(true); 
        });

        btnIn.addActionListener(e -> { if (fontSize < 35) { fontSize += 2; updateTableDisplay(); } });
        btnOut.addActionListener(e -> { if (fontSize > 10) { fontSize -= 2; updateTableDisplay(); } });
    }

    private void styleButton(JButton btn, int w, int h) {
        btn.setPreferredSize(new Dimension(w, h));
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void updateTableDisplay() {
        historyTable.setFont(new Font("SansSerif", Font.PLAIN, fontSize));
        historyTable.setRowHeight(fontSize + 15);
    }

    // በአካውንት ቁጥር ብቻ የሚለይ (Sender Acc ወይም Receiver Acc)
    private void filterByAccount(String accNum) {
        DefaultTableModel filterModel = new DefaultTableModel(columnNames, 0);

        for (int i = 0; i < mainModel.getRowCount(); i++) {
            // Sender Acc ያለው index 1 ላይ ነው፣ Receiver Acc ደግሞ index 2 ላይ ነው
            String senderAcc = mainModel.getValueAt(i, 1).toString();
            String receiverAcc = mainModel.getValueAt(i, 2).toString();

            // የጻፍነው ቁጥር ከሁለቱ አንዱ ውስጥ ካለ
            if (senderAcc.contains(accNum) || receiverAcc.contains(accNum)) {
                Object[] rowData = new Object[mainModel.getColumnCount()];
                for (int j = 0; j < mainModel.getColumnCount(); j++) {
                    rowData[j] = mainModel.getValueAt(i, j);
                }
                filterModel.addRow(rowData);
            }
        }
        historyTable.setModel(filterModel);
        updateTableDisplay();
    }
}
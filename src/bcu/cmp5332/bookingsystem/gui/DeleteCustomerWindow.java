package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.commands.DeleteCustomer;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * Modernized DeleteCustomerWindow with high-visibility warnings.
 * Follows the horizontal side-by-side layout for consistency.
 */
public class DeleteCustomerWindow extends JFrame implements ActionListener {
    
    private MainWindow mw;
    private JComboBox<String> customerCombo;
    private JButton deleteBtn = new JButton("DELETE RECORD");
    private JButton cancelBtn = new JButton("CANCEL");

    // Consistent UI Styles
    private final Color PRIMARY_NAVY = new Color(44, 62, 80);
    private final Color DANGER_RED = new Color(231, 76, 60);
    private final Color TEXT_GRAY = new Color(80, 80, 80);
    
    public DeleteCustomerWindow(MainWindow mw) {
        this.mw = mw;
        initialize();
    }
    
    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) { /* Ignore */ }
        
        setTitle("System Administration: Delete Customer");
        setSize(550, 320); // Balanced size
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // --- HEADER ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_NAVY);
        header.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel brandLabel = new JLabel("REMOVE CUSTOMER RECORD");
        brandLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        brandLabel.setForeground(Color.WHITE);
        header.add(brandLabel, BorderLayout.CENTER);

        // --- FORM AREA ---
        JPanel formContainer = new JPanel(new GridBagLayout());
        formContainer.setOpaque(false);
        formContainer.setBorder(new EmptyBorder(30, 40, 10, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Selection Label
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        JLabel selectLabel = new JLabel("Select Account:");
        selectLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        selectLabel.setForeground(TEXT_GRAY);
        formContainer.add(selectLabel, gbc);

        // Selection Combo
        gbc.gridx = 1; gbc.weightx = 0.7;
        customerCombo = new JComboBox<>(populateOptions());
        customerCombo.setPreferredSize(new Dimension(300, 35));
        formContainer.add(customerCombo, gbc);

        // Warning Hint
        gbc.gridx = 1; gbc.gridy = 1;
        JLabel warningHint = new JLabel("<html><i>Warning: This will permanently remove the customer<br>and all associated booking references.</i></html>");
        warningHint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        warningHint.setForeground(DANGER_RED);
        formContainer.add(warningHint, gbc);

        // --- FOOTER ---
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 25));
        footer.setOpaque(false);
        
        styleButton(deleteBtn, DANGER_RED, Color.BLACK); 
        styleButton(cancelBtn, new Color(235, 235, 235), Color.BLACK);
        
        footer.add(deleteBtn);
        footer.add(cancelBtn);

        deleteBtn.addActionListener(this);
        cancelBtn.addActionListener(this);

        this.add(header, BorderLayout.NORTH);
        this.add(formContainer, BorderLayout.CENTER);
        this.add(footer, BorderLayout.SOUTH);

        setLocationRelativeTo(mw);
        setVisible(true);
    }

    private String[] populateOptions() {
        return mw.getFlightBookingSystem().getCustomers().stream()
                .map(c -> c.getId() + " - " + c.getName() + " (" + c.getEmail() + ")")
                .toArray(String[]::new);
    }

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setPreferredSize(new Dimension(160, 45));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(bg.darker(), 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == deleteBtn) {
            deleteCustomer();
        } else if (ae.getSource() == cancelBtn) {
            this.dispose();
        }
    }

    private void deleteCustomer() {
        try {
            String selected = (String) customerCombo.getSelectedItem();
            if (selected == null) throw new FlightBookingSystemException("No customer selected.");
            
            int customerId = Integer.parseInt(selected.split(" - ")[0]);
            
            int confirm = JOptionPane.showConfirmDialog(this, 
                "CRITICAL: Delete customer ID " + customerId + "?\n\n" +
                "Selected: " + selected + "\n\n" +
                "Are you absolutely sure?", 
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                new DeleteCustomer(customerId).execute(mw.getFlightBookingSystem());
                
                // --- RESTORED & IMPROVED CLI LOGGING ---
                System.out.println("═══════════════════════════════════════");
                System.out.println("CLI: CUSTOMER DELETION SUCCESSFUL");
                System.out.println("ID Removed:   " + customerId);
                System.out.println("Details:      " + selected);
                System.out.println("═══════════════════════════════════════");
                
                this.dispose();
                JOptionPane.showMessageDialog(mw, "Customer record successfully purged.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Deletion Failed: " + ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
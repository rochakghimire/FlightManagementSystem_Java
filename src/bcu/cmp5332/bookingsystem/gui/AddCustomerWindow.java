package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.commands.AddCustomer;
import bcu.cmp5332.bookingsystem.commands.Command;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField; // Imported for password masking
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * Modernized AddCustomerWindow with Password field.
 */
public class AddCustomerWindow extends JFrame implements ActionListener {
    
    private MainWindow mw;
    private JTextField nameText = new JTextField();
    private JTextField phoneText = new JTextField();
    private JTextField emailText = new JTextField();
    private JPasswordField passwordText = new JPasswordField(); // Security: Password field
    
    private JButton addBtn = new JButton("REGISTER");
    private JButton cancelBtn = new JButton("CANCEL");

    // UI Styles
    private final Color PRIMARY_NAVY = new Color(44, 62, 80);
    private final Color SUCCESS_GREEN = new Color(46, 204, 113);
    private final Color DIVIDER_COLOR = new Color(220, 220, 220);
    private final Color TEXT_GRAY = new Color(80, 80, 80);
    
    public AddCustomerWindow(MainWindow mw) {
        this.mw = mw;
        initialize();
    }
    
    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) { /* Ignore */ }
        
        setTitle("Register Customer");
        setSize(400, 560); // Increased height to fit the new field
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // --- HEADER ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_NAVY);
        header.setBorder(new EmptyBorder(25, 20, 25, 20));
        
        JLabel brandLabel = new JLabel("NEW CUSTOMER");
        brandLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        brandLabel.setForeground(Color.WHITE);
        brandLabel.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(brandLabel, BorderLayout.CENTER);
        
        // --- FORM ---
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(20, 40, 10, 40));

        formPanel.add(createFieldSection("Full Name", nameText));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldSection("Phone Number", phoneText));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldSection("Email Address", emailText));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldSection("Password", passwordText)); // Added password section
        
        // --- FOOTER ---
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 25));
        footer.setOpaque(false);
        
        styleButton(addBtn, SUCCESS_GREEN, Color.BLACK);
        styleButton(cancelBtn, new Color(230, 230, 230), Color.BLACK);
        
        footer.add(addBtn);
        footer.add(cancelBtn);
        
        addBtn.addActionListener(this);
        cancelBtn.addActionListener(this);
        
        this.add(header, BorderLayout.NORTH);
        this.add(formPanel, BorderLayout.CENTER);
        this.add(footer, BorderLayout.SOUTH);
        
        setLocationRelativeTo(mw);
        setResizable(false);
        setVisible(true);
    }

    private JPanel createFieldSection(String labelStr, JTextField field) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelStr.toUpperCase());
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(TEXT_GRAY);
        label.setBorder(new EmptyBorder(0, 0, 5, 0));

        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(320, 40));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(DIVIDER_COLOR, 1),
            new EmptyBorder(5, 10, 5, 10)
        ));

        section.add(label);
        section.add(field);
        return section;
    }

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setPreferredSize(new Dimension(135, 45));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
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
        if (ae.getSource() == addBtn) {
            addCustomer();
        } else if (ae.getSource() == cancelBtn) {
            this.dispose();
        }
    }
    
    private void addCustomer() {
        try {
            String name = nameText.getText().trim();
            String phone = phoneText.getText().trim();
            String email = emailText.getText().trim();
            String password = new String(passwordText.getPassword()); // Get password as String
            
            if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
                throw new FlightBookingSystemException("All fields, including password, are required.");
            }
            
            // Passing 4 arguments to the Command (Name, Phone, Email, Password)
            Command addCustomer = new AddCustomer(name, phone, email, password);
            addCustomer.execute(mw.getFlightBookingSystem());
            
            // CLI LOGGING
            System.out.println("═══════════════════════════════════════");
            System.out.println("SUCCESS: Customer Registered");
            System.out.println("Name:     " + name);
            System.out.println("Email:    " + email);
            System.out.println("Security: Password Hidden");
            System.out.println("═══════════════════════════════════════");
            
            this.dispose();
            JOptionPane.showMessageDialog(mw, "Customer registered successfully!", 
                                         "Flight Booking System", JOptionPane.INFORMATION_MESSAGE);
        } catch (FlightBookingSystemException ex) {
            System.out.println("ERROR: Registration failed - " + ex.getMessage());
            JOptionPane.showMessageDialog(this, ex.getMessage(), 
                                         "Registration Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
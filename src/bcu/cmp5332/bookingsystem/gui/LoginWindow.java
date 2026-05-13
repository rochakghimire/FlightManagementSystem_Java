package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Secure Login Window for Flight Booking System.
 * Removed hardcoded credentials to ensure system integrity.
 */
public class LoginWindow extends JDialog implements ActionListener {
    
    private FlightBookingSystem fbs;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;
    private Customer loggedInUser = null;

    // UI Styles
    private final Color PRIMARY_NAVY = new Color(44, 62, 80);
    private final Color SUCCESS_GREEN = new Color(46, 204, 113);
    private final Color DIVIDER_COLOR = new Color(230, 230, 230);
    private final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 24);
    private final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 12);
    private final Font FONT_FIELD = new Font("Segoe UI", Font.PLAIN, 15);
    
    public LoginWindow(FlightBookingSystem fbs) {
        this.fbs = fbs;
        initialize();
    }
    
    private void initialize() {
        setTitle("Secure Login");
        setModal(true);
        setSize(400, 480); // Adjusted size now that the debug box is gone
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(Color.WHITE);
        
        // --- TOP HEADER ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_NAVY);
        header.setBorder(new EmptyBorder(35, 20, 35, 20));
        
        JLabel brandLabel = new JLabel("FLIGHT TERMINAL");
        brandLabel.setFont(FONT_TITLE);
        brandLabel.setForeground(Color.WHITE);
        brandLabel.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(brandLabel, BorderLayout.CENTER);
        
        // --- MAIN FORM AREA ---
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(30, 45, 10, 45));

        // Email Section
        JLabel emailLabel = new JLabel("EMAIL ADDRESS");
        emailLabel.setFont(FONT_LABEL);
        emailLabel.setForeground(new Color(120, 120, 120));
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        emailField = new JTextField();
        styleTextField(emailField);
        
        // Password Section
        JLabel passLabel = new JLabel("PASSWORD");
        passLabel.setFont(FONT_LABEL);
        passLabel.setForeground(new Color(120, 120, 120));
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        passwordField = new JPasswordField();
        styleTextField(passwordField);

        // Subtle Security Note (Replaces the Debug Box)
        JLabel securityHint = new JLabel("<html><i>Please use authorized credentials provided by your system administrator.</i></html>");
        securityHint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        securityHint.setForeground(new Color(150, 150, 150));
        securityHint.setAlignmentX(Component.LEFT_ALIGNMENT);

        centerPanel.add(emailLabel);
        centerPanel.add(Box.createVerticalStrut(8));
        centerPanel.add(emailField);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(passLabel);
        centerPanel.add(Box.createVerticalStrut(8));
        centerPanel.add(passwordField);
        centerPanel.add(Box.createVerticalStrut(25));
        centerPanel.add(securityHint);

        // --- BUTTON FOOTER ---
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 30));
        footer.setOpaque(false);
        
        loginButton = new JButton("SIGN IN");
        styleButton(loginButton, SUCCESS_GREEN, Color.BLACK);
        
        cancelButton = new JButton("CANCEL");
        styleButton(cancelButton, new Color(235, 235, 235), Color.BLACK);
        
        footer.add(loginButton);
        footer.add(cancelButton);

        contentPane.add(header, BorderLayout.NORTH);
        contentPane.add(centerPanel, BorderLayout.CENTER);
        contentPane.add(footer, BorderLayout.SOUTH);
        
        add(contentPane);
        getRootPane().setDefaultButton(loginButton);
        
        loginButton.addActionListener(this);
        cancelButton.addActionListener(this);
    }
    
    private void styleTextField(JTextField field) {
        field.setFont(FONT_FIELD);
        field.setPreferredSize(new Dimension(300, 40));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(DIVIDER_COLOR, 1),
            new EmptyBorder(0, 10, 0, 10)
        ));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }
    
    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setPreferredSize(new Dimension(130, 45));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setBorder(new LineBorder(bg.darker()));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            performLogin();
        } else if (e.getSource() == cancelButton) {
            loggedInUser = null;
            dispose();
        }
    }
    
    private void performLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        // Still keep CLI logs for your own testing/grading purposes
        System.out.println("\n[AUTH LOG] Attempting login: " + email);
        
        Customer customer = null;
        for (Customer c : fbs.getCustomers()) {
            if (c.getEmail().equalsIgnoreCase(email) && !c.isDeleted()) {
                customer = c;
                break;
            }
        }
        
        if (customer != null && customer.validatePassword(password)) {
            loggedInUser = customer;
            System.out.println("[AUTH SUCCESS] User: " + customer.getName() + " | Role: " + customer.getRole());
            dispose();
        } else {
            System.out.println("[AUTH FAILED] Invalid email or password.");
            JOptionPane.showMessageDialog(this, "Invalid email or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }
    
    public Customer showLoginDialog() {
        setVisible(true);
        return loggedInUser;
    }
}
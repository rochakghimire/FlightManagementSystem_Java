package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.commands.CancelBooking;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * Modernized CancelBookingWindow with a clean, horizontal form layout.
 * Features high-contrast buttons and detailed CLI logging for financial tracking.
 */
public class CancelBookingWindow extends JFrame implements ActionListener {
    
    private MainWindow mw;
    private Customer currentUser;
    
    private JComboBox<CustomerComboItem> customerCombo = new JComboBox<>();
    private JComboBox<BookingComboItem> bookingCombo = new JComboBox<>();
    private JButton cancelBookingBtn = new JButton("CANCEL BOOKING");
    private JButton closeBtn = new JButton("CLOSE");

    // UI Styles
    private final Color PRIMARY_NAVY = new Color(44, 62, 80);
    private final Color DANGER_RED = new Color(231, 76, 60);
    private final Color DIVIDER_COLOR = new Color(220, 220, 220);
    private final Color TEXT_GRAY = new Color(80, 80, 80);
    
    public CancelBookingWindow(MainWindow mw) {
        this.mw = mw;
        this.currentUser = mw.getCurrentUser();
        initialize();
    }
    
    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) { /* Ignore */ }
        
        setTitle("Cancel Booking");
        setSize(650, 480);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // --- HEADER ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_NAVY);
        header.setBorder(new EmptyBorder(25, 20, 25, 20));
        
        JLabel brandLabel = new JLabel("CANCELLATION MANAGEMENT");
        brandLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        brandLabel.setForeground(Color.WHITE);
        brandLabel.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(brandLabel, BorderLayout.CENTER);
        
        // --- FORM PANEL (GridBag for Side-by-Side) ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(30, 40, 10, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(12, 10, 12, 10);

        // Row 0: Customer
        addFormRow("Customer:", customerCombo, formPanel, gbc, 0);
        
        // Security Hint (only if not admin)
        if (!currentUser.isAdmin()) {
            gbc.gridx = 1; gbc.gridy = 1;
            JLabel hint = new JLabel("Security: You can only cancel your own bookings.");
            hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            hint.setForeground(TEXT_GRAY);
            formPanel.add(hint, gbc);
        }

        // Row 2: Booking Selection
        addFormRow("Select Booking:", bookingCombo, formPanel, gbc, 2);

        // Warning Label
        gbc.gridx = 1; gbc.gridy = 3;
        JLabel warningLabel = new JLabel("<html><i>Note: A 15% cancellation fee will be deducted from the refund.</i></html>");
        warningLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        warningLabel.setForeground(new Color(192, 57, 43));
        formPanel.add(warningLabel, gbc);

        // --- FOOTER ---
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 25));
        footer.setOpaque(false);
        
        styleButton(cancelBookingBtn, DANGER_RED, Color.BLACK); // High contrast
        styleButton(closeBtn, new Color(230, 230, 230), Color.BLACK);
        
        footer.add(cancelBookingBtn);
        footer.add(closeBtn);
        
        cancelBookingBtn.addActionListener(this);
        closeBtn.addActionListener(this);
        
        customerCombo.addActionListener(e -> updateBookingsList());

        this.add(header, BorderLayout.NORTH);
        this.add(formPanel, BorderLayout.CENTER);
        this.add(footer, BorderLayout.SOUTH);
        
        populateCustomerCombo();
        setLocationRelativeTo(mw);
        setVisible(true);
    }

    private void addFormRow(String labelStr, JComponent comp, JPanel panel, GridBagConstraints gbc, int row) {
        JLabel label = new JLabel(labelStr);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_GRAY);

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(label, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        comp.setPreferredSize(new Dimension(350, 35));
        panel.add(comp, gbc);
    }

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setPreferredSize(new Dimension(160, 45));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(bg.darker(), 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
    }

    private void populateCustomerCombo() {
        customerCombo.removeAllItems();
        if (currentUser.isAdmin()) {
            customerCombo.addItem(new CustomerComboItem(null, "-- Select Customer --"));
            for (Customer customer : mw.getFlightBookingSystem().getCustomers()) {
                if (!customer.isDeleted() && !customer.getBookings().isEmpty()) {
                    customerCombo.addItem(new CustomerComboItem(customer, "ID:" + customer.getId() + " | " + customer.getName()));
                }
            }
        } else {
            customerCombo.addItem(new CustomerComboItem(currentUser, currentUser.getName() + " (You)"));
            customerCombo.setEnabled(false);
            updateBookingsList();
        }
    }

    private void updateBookingsList() {
        bookingCombo.removeAllItems();
        CustomerComboItem selected = (CustomerComboItem) customerCombo.getSelectedItem();
        if (selected == null || selected.getCustomer() == null) return;

        for (Booking b : selected.getCustomer().getBookings()) {
            Flight f = b.getFlight();
            if (!f.isDeleted()) {
                String status = f.hasDeparted() ? "[COMPLETED]" : "[ACTIVE]";
                String text = f.getFlightNumber() + " | " + f.getOrigin() + " -> " + f.getDestination() + " " + status;
                bookingCombo.addItem(new BookingComboItem(b, text));
            }
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == cancelBookingBtn) {
            cancelBooking();
        } else if (ae.getSource() == closeBtn) {
            dispose();
        }
    }
    
    private void cancelBooking() {
        try {
            CustomerComboItem selectedC = (CustomerComboItem) customerCombo.getSelectedItem();
            BookingComboItem selectedB = (BookingComboItem) bookingCombo.getSelectedItem();
            
            if (selectedC == null || selectedB == null || selectedB.getBooking() == null) {
                throw new FlightBookingSystemException("Please select a valid booking.");
            }
            
            Customer customer = selectedC.getCustomer();
            Booking booking = selectedB.getBooking();
            Flight flight = booking.getFlight();
            
            if (flight.hasDeparted()) {
                throw new FlightBookingSystemException("Cannot cancel - flight has departed.");
            }

            double price = booking.getBookingPrice();
            double fee = price * 0.15;
            double refund = price - fee;

            int confirm = JOptionPane.showConfirmDialog(this, 
                "Confirm Cancellation for " + flight.getFlightNumber() + "?\n\n" +
                "Original Price: £" + String.format("%.2f", price) + "\n" +
                "Cancellation Fee (15%): £" + String.format("%.2f", fee) + "\n" +
                "Total Refund: £" + String.format("%.2f", refund),
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                new CancelBooking(customer.getId(), flight.getId()).execute(mw.getFlightBookingSystem());
                
                // CLI Trace for Financials
                System.out.println("═══════════════════════════════════════");
                System.out.println("CLI: CANCELLATION PROCESSED");
                System.out.println("═══════════════════════════════════════");
                System.out.println("Customer: " + customer.getName());
                System.out.println("Flight:   " + flight.getFlightNumber());
                System.out.println("Fee Charged:  £" + String.format("%.2f", fee));
                System.out.println("Refund Issued: £" + String.format("%.2f", refund));
                System.out.println("═══════════════════════════════════════");

                JOptionPane.showMessageDialog(this, "Booking Cancelled Successfully.");
                updateBookingsList();
                mw.displayFlights();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helper classes
    private static class CustomerComboItem {
        private Customer c; private String t;
        public CustomerComboItem(Customer c, String t) { this.c = c; this.t = t; }
        public Customer getCustomer() { return c; }
        @Override public String toString() { return t; }
    }

    private static class BookingComboItem {
        private Booking b; private String t;
        public BookingComboItem(Booking b, String t) { this.b = b; this.t = t; }
        public Booking getBooking() { return b; }
        @Override public String toString() { return t; }
    }
}
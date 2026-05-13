package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.commands.UpdateBooking;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.util.SystemDate;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Modernized UpdateBookingWindow.
 * Security: Locks selection for non-admins.
 * Style: High-contrast buttons with Black text.
 */
public class UpdateBookingWindow extends JFrame implements ActionListener {
    
    private MainWindow mw;
    private Customer currentUser;
    
    private JComboBox<CustomerComboItem> customerCombo;
    private JComboBox<BookingComboItem> currentBookingCombo;
    private JComboBox<FlightComboItem> newFlightCombo;
    private JLabel feeWarningLabel;
    private JButton updateBtn = new JButton("CONFIRM REBOOKING");
    private JButton cancelBtn = new JButton("CANCEL");

    // UI Styles
    private final Color PRIMARY_NAVY = new Color(44, 62, 80);
    private final Color UPDATE_ORANGE = new Color(230, 126, 34);
    private final Color BG_LIGHT = new Color(248, 249, 250);
    
    public UpdateBookingWindow(MainWindow mw) {
        this.mw = mw;
        this.currentUser = mw.getCurrentUser();
        initialize();
    }
    
    private void initialize() {
        setTitle("Booking Terminal: Rebook Flight");
        setSize(850, 450);
        setLocationRelativeTo(mw);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // --- TOP HEADER ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_NAVY);
        header.setBorder(new EmptyBorder(15, 25, 15, 25));
        
        JLabel titleLabel = new JLabel("FLIGHT REBOOKING INTERFACE");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        header.add(titleLabel, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // --- FORM PANEL ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(20, 40, 20, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Customer Row
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Customer Account:"), gbc);
        gbc.gridx = 1;
        customerCombo = new JComboBox<>();
        styleCombo(customerCombo);
        customerCombo.addActionListener(e -> {
            updateCurrentBookingsList();
            clearNewFlightsList();
        });
        formPanel.add(customerCombo, gbc);

        // Current Booking Row
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Active Ticket:"), gbc);
        gbc.gridx = 1;
        currentBookingCombo = new JComboBox<>();
        styleCombo(currentBookingCombo);
        currentBookingCombo.addActionListener(e -> updateNewFlightsList());
        formPanel.add(currentBookingCombo, gbc);

        // New Flight Row
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Transfer to Flight:"), gbc);
        gbc.gridx = 1;
        newFlightCombo = new JComboBox<>();
        styleCombo(newFlightCombo);
        formPanel.add(newFlightCombo, gbc);

        // Fee Information
        gbc.gridx = 1; gbc.gridy = 3;
        feeWarningLabel = new JLabel("ⓘ Rebooking fee may apply based on departure proximity.");
        feeWarningLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        feeWarningLabel.setForeground(UPDATE_ORANGE);
        formPanel.add(feeWarningLabel, gbc);

        add(formPanel, BorderLayout.CENTER);

        // --- FOOTER ---
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        footer.setBackground(BG_LIGHT);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));

        styleButton(updateBtn, UPDATE_ORANGE);
        styleButton(cancelBtn, new Color(220, 220, 220));
        
        updateBtn.addActionListener(this);
        cancelBtn.addActionListener(this);
        
        footer.add(cancelBtn);
        footer.add(updateBtn);
        add(footer, BorderLayout.SOUTH);

        populateCustomerCombo();
        setVisible(true);
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setPreferredSize(new Dimension(200, 40));
        btn.setBackground(bg);
        btn.setForeground(Color.BLACK); // Per request: Black text
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorder(BorderFactory.createLineBorder(bg.darker(), 1));
    }

    private void styleCombo(JComboBox<?> combo) {
        combo.setPreferredSize(new Dimension(450, 35));
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        combo.setBackground(Color.WHITE);
    }

    private void populateCustomerCombo() {
        if (currentUser.isAdmin()) {
            customerCombo.addItem(new CustomerComboItem(null, "-- Select Customer --"));
            for (Customer c : mw.getFlightBookingSystem().getCustomers()) {
                if (!c.isDeleted() && !c.getBookings().isEmpty()) {
                    customerCombo.addItem(new CustomerComboItem(c, "ID: " + c.getId() + " | " + c.getName()));
                }
            }
        } else {
            customerCombo.addItem(new CustomerComboItem(currentUser, currentUser.getName() + " (Logged In)"));
            customerCombo.setEnabled(false);
            updateCurrentBookingsList();
        }
    }

    private void clearNewFlightsList() {
        newFlightCombo.removeAllItems();
        newFlightCombo.addItem(new FlightComboItem(null, "-- Select active ticket first --"));
    }

    private void updateCurrentBookingsList() {
        currentBookingCombo.removeAllItems();
        CustomerComboItem selected = (CustomerComboItem) customerCombo.getSelectedItem();
        if (selected == null || selected.getCustomer() == null) return;

        Customer c = selected.getCustomer();
        for (Booking b : c.getBookings()) {
            Flight f = b.getFlight();
            if (!f.isDeleted() && !f.hasDeparted()) {
                currentBookingCombo.addItem(new BookingComboItem(b, f.getFlightNumber() + " to " + f.getDestination()));
            }
        }
    }

    private void updateNewFlightsList() {
        newFlightCombo.removeAllItems();
        BookingComboItem selectedB = (BookingComboItem) currentBookingCombo.getSelectedItem();
        if (selectedB == null || selectedB.getBooking() == null) return;

        int currentFid = selectedB.getBooking().getFlight().getId();
        for (Flight f : mw.getFlightBookingSystem().getFlights()) {
            if (!f.isDeleted() && !f.hasDeparted() && f.getId() != currentFid && !f.isFull()) {
                newFlightCombo.addItem(new FlightComboItem(f, f.getFlightNumber() + " (" + f.getOrigin() + " to " + f.getDestination() + ")"));
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == updateBtn) updateBooking();
        else if (e.getSource() == cancelBtn) dispose();
    }

    private void updateBooking() {
        try {
            Customer c = ((CustomerComboItem) customerCombo.getSelectedItem()).customer;
            Booking b = ((BookingComboItem) currentBookingCombo.getSelectedItem()).booking;
            Flight newF = ((FlightComboItem) newFlightCombo.getSelectedItem()).flight;

            // Security Check
            if (!currentUser.isAdmin() && c.getId() != currentUser.getId()) {
                throw new FlightBookingSystemException("Access Denied: You cannot modify other users' bookings.");
            }

            UpdateBooking cmd = new UpdateBooking(c.getId(), b.getFlight().getId(), newF.getId());
            cmd.execute(mw.getFlightBookingSystem());

            // --- CLI LOGGING ---
            System.out.println("\n[SYSTEM EVENT: REBOOKING SUCCESSFUL]");
            System.out.println("════════════════════════════════════════");
            System.out.println("  PASSENGER: " + c.getName());
            System.out.println("  OLD FLIGHT: " + b.getFlight().getFlightNumber());
            System.out.println("  NEW FLIGHT: " + newF.getFlightNumber());
            System.out.println("  TIMESTAMP:  " + SystemDate.getCurrentDate());
            System.out.println("════════════════════════════════════════\n");

            JOptionPane.showMessageDialog(this, "Booking successfully updated to " + newF.getFlightNumber());
            mw.displayFlights();
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Update Failed: " + ex.getMessage());
        }
    }

    // Helper items
    private static class CustomerComboItem {
        Customer customer; String text;
        CustomerComboItem(Customer c, String t) { this.customer = c; this.text = t; }
        @Override public String toString() { return text; }
        public Customer getCustomer() { return customer; }
    }
    private static class BookingComboItem {
        Booking booking; String text;
        BookingComboItem(Booking b, String t) { this.booking = b; this.text = t; }
        @Override public String toString() { return text; }
        public Booking getBooking() { return booking; }
    }
    private static class FlightComboItem {
        Flight flight; String text;
        FlightComboItem(Flight f, String t) { this.flight = f; this.text = t; }
        @Override public String toString() { return text; }
        public Flight getFlight() { return flight; }
    }
}
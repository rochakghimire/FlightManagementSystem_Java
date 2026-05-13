package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * Modernized CustomerBookingsWindow for the Flight Booking System.
 * Displays a styled table of a customer's travel history with a financial summary.
 */
public class CustomerBookingsWindow extends JFrame {
    
    private Customer customer;
    private final Color PRIMARY_NAVY = new Color(44, 62, 80);
    private final Color BACKGROUND_WHITE = Color.WHITE;
    private final Color TEXT_GRAY = new Color(80, 80, 80);

    public CustomerBookingsWindow(Customer customer) {
        this.customer = customer;
        initialize();
    }
    
    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) { /* Ignore */ }
        
        setTitle("Booking History - " + customer.getName());
        setSize(850, 500); // Slightly wider for better column spacing
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_WHITE);

        // --- HEADER SECTION ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_NAVY);
        headerPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel titleLabel = new JLabel("CUSTOMER BOOKINGS");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        JLabel customerLabel = new JLabel(customer.getName().toUpperCase() + " (ID: " + customer.getId() + ")");
        customerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        customerLabel.setForeground(new Color(200, 200, 200));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(customerLabel, BorderLayout.EAST);

        // --- DATA PREPARATION ---
        String[] columns = {"Booking Date", "Flight No", "Origin", "Destination", "Departure Date", "Price"};
        Object[][] data = new Object[customer.getBookings().size()][6];
        double totalSpend = 0;

        int i = 0;
        for (Booking booking : customer.getBookings()) {
            data[i][0] = booking.getBookingDate();
            data[i][1] = booking.getFlight().getFlightNumber();
            data[i][2] = booking.getFlight().getOrigin();
            data[i][3] = booking.getFlight().getDestination();
            data[i][4] = booking.getFlight().getDepartureDate();
            
            double price = booking.getFlight().getPrice();
            data[i][5] = String.format("£%.2f", price);
            totalSpend += price;
            i++;
        }

        // --- TABLE STYLING ---
        JTable table = new JTable(data, columns);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(232, 244, 253));
        table.setSelectionForeground(Color.BLACK);
        table.setGridColor(new Color(240, 240, 240));
        table.setShowVerticalLines(false);

        // Header Styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(245, 245, 245));
        header.setForeground(TEXT_GRAY);
        header.setPreferredSize(new Dimension(100, 40));
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        // --- SUMMARY FOOTER ---
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(250, 250, 250));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));
        footer.setBorder(BorderFactory.createCompoundBorder(footer.getBorder(), new EmptyBorder(15, 25, 15, 25)));

        JLabel countLabel = new JLabel("Total Bookings: " + customer.getBookings().size());
        countLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        countLabel.setForeground(TEXT_GRAY);

        JLabel totalLabel = new JLabel("Total Spend: £" + String.format("%.2f", totalSpend));
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        totalLabel.setForeground(PRIMARY_NAVY);

        footer.add(countLabel, BorderLayout.WEST);
        footer.add(totalLabel, BorderLayout.EAST);

        // Add components to Frame
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
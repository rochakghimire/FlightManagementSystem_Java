package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * Modernized Passenger Manifest window.
 * Displays all customers booked on a specific flight with a capacity summary.
 */
public class FlightsPassengersWindow extends JFrame {
    
    private Flight flight;
    private final Color PRIMARY_NAVY = new Color(44, 62, 80);
    private final Color TEXT_GRAY = new Color(80, 80, 80);
    
    public FlightsPassengersWindow(Flight flight) {
        this.flight = flight;
        initialize();
    }
    
    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) { /* Ignore */ }
        
        setTitle("Passenger Manifest: " + flight.getFlightNumber());
        setSize(750, 500);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // --- HEADER SECTION ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_NAVY);
        headerPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel titleLabel = new JLabel("FLIGHT MANIFEST");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        JLabel flightLabel = new JLabel(flight.getFlightNumber() + " | " + flight.getOrigin() + " ➔ " + flight.getDestination());
        flightLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        flightLabel.setForeground(new Color(200, 200, 200));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(flightLabel, BorderLayout.EAST);

        // --- DATA PREPARATION ---
        String[] columns = {"ID", "Passenger Name", "Phone Number", "Email Address"};
        Object[][] data = new Object[flight.getPassengers().size()][4];
        
        int i = 0;
        for (Customer passenger : flight.getPassengers()) {
            data[i][0] = passenger.getId();
            data[i][1] = passenger.getName();
            data[i][2] = passenger.getPhone();
            data[i][3] = passenger.getEmail();
            i++;
        }

        // --- TABLE STYLING ---
        JTable table = new JTable(data, columns);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(232, 244, 253));
        table.setGridColor(new Color(240, 240, 240));
        table.setShowVerticalLines(false);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(245, 245, 245));
        header.setPreferredSize(new Dimension(100, 40));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        // --- FOOTER SUMMARY ---
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(250, 250, 250));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));
        footer.setBorder(BorderFactory.createCompoundBorder(footer.getBorder(), new EmptyBorder(15, 25, 15, 25)));

        int passengerCount = flight.getPassengers().size();
        int capacity = flight.getCapacity();
        double occupancyRate = ((double) passengerCount / capacity) * 100;

        JLabel countLabel = new JLabel("Total Passengers: " + passengerCount);
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        countLabel.setForeground(PRIMARY_NAVY);

        JLabel capacityLabel = new JLabel(String.format("Occupancy: %.1f%% of %d seats", occupancyRate, capacity));
        capacityLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        capacityLabel.setForeground(TEXT_GRAY);

        footer.add(countLabel, BorderLayout.WEST);
        footer.add(capacityLabel, BorderLayout.EAST);

        // --- CLI LOGGING ---
        System.out.println("═══════════════════════════════════════");
        System.out.println("CLI: LOADING MANIFEST FOR " + flight.getFlightNumber());
        System.out.println("Current Load: " + passengerCount + "/" + capacity);
        System.out.println("Status: " + (occupancyRate > 90 ? "NEAR CAPACITY" : "SEATS AVAILABLE"));
        System.out.println("═══════════════════════════════════════");

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
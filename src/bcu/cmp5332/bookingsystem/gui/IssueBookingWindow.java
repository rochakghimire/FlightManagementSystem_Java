package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.util.SystemDate;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

/**
 * Modernized IssueBookingWindow.
 * Security: Strictly locks the selection to the logged-in user if they are not an Admin.
 */
public class IssueBookingWindow extends JFrame implements ActionListener {

    private MainWindow mw;
    private FlightBookingSystem fbs;
    private Customer currentUser;
    
    private JComboBox<CustomerComboItem> customerComboBox;
    private JComboBox<FlightComboItem> flightComboBox;
    private JButton issueButton, cancelButton, previewButton;
    private JTextArea previewArea;

    // UI Styles
    private final Color PRIMARY_NAVY = new Color(44, 62, 80);
    private final Color SUCCESS_GREEN = new Color(46, 204, 113);
    private final Color ACCENT_BLUE = new Color(52, 152, 219);
    private final Color BG_LIGHT = new Color(248, 249, 250);

    public IssueBookingWindow(MainWindow mw) {
        this.mw = mw;
        this.fbs = mw.getFlightBookingSystem();
        // This pulls the actual person logged into the GUI session
        this.currentUser = mw.getCurrentUser(); 
        initialize();
    }

    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) { /* Ignore */ }

        setTitle("Booking Terminal: Issue New Ticket");
        setSize(950, 600);
        setLocationRelativeTo(mw);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // --- TOP HEADER ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_NAVY);
        header.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        JLabel titleLabel = new JLabel("PASSENGER TICKETING SYSTEM");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        header.add(titleLabel, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // --- MAIN CONTENT ---
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        contentPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        contentPanel.setBackground(Color.WHITE);

        // LEFT PANEL: Selection Controls
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // Customer Selection Label
        JLabel custLabel = new JLabel("Passenger Account:");
        custLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 5, 0);
        leftPanel.add(custLabel, gbc);

        // SESSION LOCK LOGIC
        if (currentUser.isAdmin()) {
            // Admin can see everyone
            customerComboBox = new JComboBox<>(populateCustomers());
            customerComboBox.setEnabled(true);
        } else {
            // LOCK to logged-in customer only
            CustomerComboItem currentItem = new CustomerComboItem(currentUser);
            customerComboBox = new JComboBox<>(new CustomerComboItem[]{currentItem});
            customerComboBox.setSelectedItem(currentItem);
            customerComboBox.setEnabled(false); // Locked for security
        }
        
        styleCombo(customerComboBox);
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 20, 0);
        leftPanel.add(customerComboBox, gbc);

        // Flight Selection
        JLabel flightLabel = new JLabel("Select Available Flight:");
        flightLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 5, 0);
        leftPanel.add(flightLabel, gbc);

        flightComboBox = new JComboBox<>(populateFlights());
        styleCombo(flightComboBox);
        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 30, 0);
        leftPanel.add(flightComboBox, gbc);

        previewButton = new JButton("GENERATE PREVIEW");
        styleButton(previewButton, ACCENT_BLUE);
        gbc.gridy = 4;
        leftPanel.add(previewButton, gbc);

        // RIGHT PANEL: Digital Ticket
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(BG_LIGHT);
        TitledBorder tb = BorderFactory.createTitledBorder(" DIGITAL TICKET RECEIPT ");
        tb.setTitleFont(new Font("Segoe UI", Font.BOLD, 12));
        rightPanel.setBorder(BorderFactory.createCompoundBorder(tb, new EmptyBorder(10, 10, 10, 10)));

        previewArea = new JTextArea();
        previewArea.setEditable(false);
        previewArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        previewArea.setBackground(BG_LIGHT);
        previewArea.setText("\n\n   Logged in as: " + currentUser.getName() + "\n   Select a flight to generate a ticket.");
        
        rightPanel.add(new JScrollPane(previewArea), BorderLayout.CENTER);

        contentPanel.add(leftPanel);
        contentPanel.add(rightPanel);
        add(contentPanel, BorderLayout.CENTER);

        // --- FOOTER ---
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 25, 20));
        footer.setBackground(new Color(240, 240, 240));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)));

        issueButton = new JButton("CONFIRM & ISSUE TICKET");
        styleButton(issueButton, SUCCESS_GREEN);
        issueButton.setEnabled(false);
        
        cancelButton = new JButton("CANCEL");
        styleButton(cancelButton, new Color(220, 220, 220));

        footer.add(cancelButton);
        footer.add(issueButton);
        add(footer, BorderLayout.SOUTH);

        previewButton.addActionListener(this);
        issueButton.addActionListener(this);
        cancelButton.addActionListener(this);

        setVisible(true);
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setPreferredSize(new Dimension(220, 45));
        btn.setBackground(bg);
        btn.setForeground(Color.BLACK); 
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorder(BorderFactory.createLineBorder(bg.darker(), 1));
    }

    private void styleCombo(JComboBox<?> combo) {
        combo.setPreferredSize(new Dimension(350, 40));
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setBackground(Color.WHITE);
    }

    private CustomerComboItem[] populateCustomers() {
        return fbs.getCustomers().stream()
                .filter(c -> !c.isDeleted())
                .map(CustomerComboItem::new)
                .toArray(CustomerComboItem[]::new);
    }

    private FlightComboItem[] populateFlights() {
        return fbs.getFlights().stream()
                .filter(f -> !f.isDeleted() && !f.hasDeparted())
                .map(FlightComboItem::new)
                .toArray(FlightComboItem[]::new);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == previewButton) previewBooking();
        else if (e.getSource() == issueButton) issueBooking();
        else if (e.getSource() == cancelButton) dispose();
    }

    private void previewBooking() {
        CustomerComboItem cItem = (CustomerComboItem)customerComboBox.getSelectedItem();
        FlightComboItem fItem = (FlightComboItem)flightComboBox.getSelectedItem();

        if (cItem == null || fItem == null) return;

        Customer cust = cItem.customer;
        Flight fli = fItem.flight;
        double price = fli.calculateDynamicPrice(SystemDate.getCurrentDate());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM yyyy");

        StringBuilder sb = new StringBuilder();
        sb.append(" ╔═══════════════════════════════════════╗\n");
        sb.append(" ║          OFFICIAL BOARDING PASS       ║\n");
        sb.append(" ╚═══════════════════════════════════════╝\n\n");
        sb.append(String.format("   PASSENGER: %-25s\n", cust.getName().toUpperCase()));
        sb.append(String.format("   ACCOUNT ID: %-25s\n\n", cust.getId()));
        sb.append(" ─────────────────────────────────────────\n");
        sb.append(String.format("   FLIGHT:    %-15s\n", fli.getFlightNumber()));
        sb.append(String.format("   ROUTE:     %s -> %s\n", fli.getOrigin(), fli.getDestination()));
        sb.append(String.format("   DEPARTS:   %-15s\n", fli.getDepartureDate().format(dtf)));
        sb.append(" ─────────────────────────────────────────\n\n");
        sb.append(String.format("   FARE TOTAL: GBP %.2f\n", price));
        
        if (fli.isFull()) {
            sb.append("   [!] STATUS: FLIGHT FULL");
            issueButton.setEnabled(false);
        } else {
            sb.append("   [✓] STATUS: READY TO ISSUE");
            issueButton.setEnabled(true);
        }

        previewArea.setText(sb.toString());
    }

    private void issueBooking() {
        try {
            Customer customer = ((CustomerComboItem)customerComboBox.getSelectedItem()).customer;
            Flight flight = ((FlightComboItem)flightComboBox.getSelectedItem()).flight;
            LocalDate today = SystemDate.getCurrentDate();
            double price = flight.calculateDynamicPrice(today);

            if (flight.getPassengers().contains(customer)) {
                throw new FlightBookingSystemException("Passenger is already booked on this flight.");
            }

            Booking b = new Booking(customer, flight, today, price);
            customer.addBooking(b);
            flight.addPassenger(customer);
            
            FlightBookingSystemData.store(fbs);
            
            // CLI LOGGING
            System.out.println("\n[BOOKING CONFIRMED]");
            System.out.println("════════════════════════════════════════");
            System.out.println(" PASSENGER: " + customer.getName());
            System.out.println(" FLIGHT:    " + flight.getFlightNumber());
            System.out.println(" TOTAL:     GBP " + String.format("%.2f", price));
            System.out.println("════════════════════════════════════════\n");
            
            JOptionPane.showMessageDialog(this, "Ticket Issued Successfully!");
            mw.displayFlights();
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private static class CustomerComboItem {
        Customer customer;
        CustomerComboItem(Customer c) { this.customer = c; }
        @Override public String toString() { return "ID: " + customer.getId() + " | " + customer.getName(); }
    }

    private static class FlightComboItem {
        Flight flight;
        FlightComboItem(Flight f) { this.flight = f; }
        @Override public String toString() { return flight.getFlightNumber() + " (" + flight.getOrigin() + ")"; }
    }
}
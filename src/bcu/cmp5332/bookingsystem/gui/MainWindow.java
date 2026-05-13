package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.util.SystemDate;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * Main window of the Flight Booking System.
 * Provides the primary user interface with menu-based navigation.
 * Implements role-based access control for admin and customer users.
 * * @author Flight Booking System Team
 * @version 2.0
 */
public class MainWindow extends JFrame implements ActionListener {

    private JMenuBar menuBar;
    private JMenu adminMenu;
    private JMenu flightsMenu;
    private JMenu bookingsMenu;
    private JMenu customersMenu;

    private JMenuItem adminExit;
    private JMenuItem adminLogout;

    private JMenuItem flightsView;
    private JMenuItem flightsAdd;
    private JMenuItem flightsDel;
    private JMenuItem flightsViewPassengers;
    
    private JMenuItem bookingsIssue;
    private JMenuItem bookingsUpdate;
    private JMenuItem bookingsCancel;

    private JMenuItem custView;
    private JMenuItem custAdd;
    private JMenuItem custDel;

    private FlightBookingSystem fbs;
    private Customer currentUser; 

    // UI Constants
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color ACCENT_COLOR = new Color(52, 152, 219);
    private final Color TEXT_COLOR = new Color(44, 62, 80);
    private final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 22);
    private final Font NORMAL_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    public MainWindow(FlightBookingSystem fbs) {
        this.fbs = fbs;
        authenticateUser();
        
        if (currentUser != null) {
            initialize();
        } else {
            JOptionPane.showMessageDialog(null,
                "Authentication required to access the system.\nApplication will now exit.",
                "Login Required",
                JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        }
    }

    public FlightBookingSystem getFlightBookingSystem() {
        return fbs;
    }
    
    public Customer getCurrentUser() {
        return currentUser;
    }

    private void authenticateUser() {
        LoginWindow loginWindow = new LoginWindow(fbs);
        currentUser = loginWindow.showLoginDialog();
    }

    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // Global UI overrides for a modern feel
            UIManager.put("Button.font", NORMAL_FONT);
            UIManager.put("Menu.font", new Font("Segoe UI", Font.PLAIN, 13));
            UIManager.put("MenuItem.font", new Font("Segoe UI", Font.PLAIN, 13));
        } catch (Exception ex) {
            // Fallback to default
        }

        updateWindowTitle();

        menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        
        // --- SYSTEM MENU ---
        adminMenu = new JMenu("System");
        adminLogout = new JMenuItem("Logout");
        adminLogout.addActionListener(this);
        adminMenu.add(adminLogout);
        adminMenu.addSeparator();
        adminExit = new JMenuItem("Exit");
        adminExit.addActionListener(this);
        adminMenu.add(adminExit);
        menuBar.add(adminMenu);

        // --- FLIGHTS MENU ---
        flightsMenu = new JMenu("Flights");
        flightsView = new JMenuItem("View Available Flights");
        flightsView.addActionListener(this);
        flightsMenu.add(flightsView);
        flightsViewPassengers = new JMenuItem("View Passengers");
        flightsViewPassengers.addActionListener(this);
        flightsMenu.add(flightsViewPassengers);
        flightsMenu.addSeparator();
        flightsAdd = new JMenuItem("Add Flight");
        flightsAdd.addActionListener(this);
        flightsMenu.add(flightsAdd);
        flightsDel = new JMenuItem("Delete Flight");
        flightsDel.addActionListener(this);
        flightsMenu.add(flightsDel);
        menuBar.add(flightsMenu);

        // --- BOOKINGS MENU ---
        bookingsMenu = new JMenu("Bookings");
        bookingsIssue = new JMenuItem("Issue New Booking");
        bookingsIssue.addActionListener(this);
        bookingsMenu.add(bookingsIssue);
        bookingsUpdate = new JMenuItem("Update Booking");
        bookingsUpdate.addActionListener(this);
        bookingsMenu.add(bookingsUpdate);
        bookingsCancel = new JMenuItem("Cancel Booking");
        bookingsCancel.addActionListener(this);
        bookingsMenu.add(bookingsCancel);
        menuBar.add(bookingsMenu);

        // --- CUSTOMERS MENU ---
        customersMenu = new JMenu("Customers");
        custView = new JMenuItem("View Customers");
        custView.addActionListener(this);
        customersMenu.add(custView);
        customersMenu.addSeparator();
        custAdd = new JMenuItem("Add Customer");
        custAdd.addActionListener(this);
        customersMenu.add(custAdd);
        custDel = new JMenuItem("Delete Customer");
        custDel.addActionListener(this);
        customersMenu.add(custDel);
        menuBar.add(customersMenu);

        setJMenuBar(menuBar);
        configureMenusBasedOnRole();

        setLayout(new BorderLayout());
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        displayWelcomeScreen();
        setVisible(true);
    }

    private void updateWindowTitle() {
        String roleText = currentUser.isAdmin() ? "Administrator" : "Customer";
        setTitle("Flight Booking System | " + currentUser.getName() + " (" + roleText + ")");
    }

    private void addStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(Color.WHITE);
        statusBar.setPreferredSize(new Dimension(getWidth(), 30));
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(210, 210, 210)));
        
        String roleText = currentUser.isAdmin() ? "Administrator" : "Customer";
        JLabel statusLabel = new JLabel("  User: " + currentUser.getName() + " (" + roleText + ")");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(100, 100, 100));

        JLabel dateLabel = new JLabel("System Date: " + SystemDate.getCurrentDate() + "  ");
        dateLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        dateLabel.setForeground(PRIMARY_COLOR);
        
        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(dateLabel, BorderLayout.EAST);
        this.add(statusBar, BorderLayout.SOUTH);
    }

    private void configureMenusBasedOnRole() {
        if (!isAdmin()) {
            flightsAdd.setEnabled(false);
            flightsDel.setEnabled(false);
            custAdd.setEnabled(false);
            custDel.setEnabled(false);
            flightsAdd.setText("Add Flight (Admin Only)");
            flightsDel.setText("Delete Flight (Admin Only)");
            custAdd.setText("Add Customer (Admin Only)");
            custDel.setText("Delete Customer (Admin Only)");
        }
    }

    private boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }

    private void showPermissionError() {
        JOptionPane.showMessageDialog(this,
            "You don't have permission to perform this action.\n" +
            "Administrator access is required.",
            "Permission Denied",
            JOptionPane.ERROR_MESSAGE);
    }

    private void displayWelcomeScreen() {
        this.getContentPane().removeAll();
        
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(BACKGROUND_COLOR);
        
        // Header Hero Section
        JPanel heroPanel = new JPanel(new GridBagLayout());
        heroPanel.setBackground(PRIMARY_COLOR);
        heroPanel.setPreferredSize(new Dimension(getWidth(), 150));
        
        JLabel titleLabel = new JLabel("Flight Booking System");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(Color.WHITE);
        heroPanel.add(titleLabel);
        
        // Content area
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        JLabel iconLabel;
        try {
            ImageIcon airplaneIcon = new ImageIcon("resources/data/aeroplane.png");
            if (airplaneIcon.getIconWidth() > 0) {
                iconLabel = new JLabel(airplaneIcon);
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            iconLabel = new JLabel("<html><div style='text-align: center; font-size: 50px;'>✈</div></html>");
        }
        
        centerPanel.add(iconLabel, gbc);
        
        gbc.gridy = 1;
        gbc.insets = new Insets(20, 0, 0, 0);
        JLabel messageLabel = new JLabel("Welcome back, " + currentUser.getName());
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        messageLabel.setForeground(TEXT_COLOR);
        centerPanel.add(messageLabel, gbc);
        
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 0, 0, 0);
        JLabel subLabel = new JLabel("Select an option from the menu to manage bookings and flights.");
        subLabel.setFont(NORMAL_FONT);
        subLabel.setForeground(new Color(127, 140, 141));
        centerPanel.add(subLabel, gbc);
        
        welcomePanel.add(heroPanel, BorderLayout.NORTH);
        welcomePanel.add(centerPanel, BorderLayout.CENTER);
        
        this.add(welcomePanel, BorderLayout.CENTER);
        addStatusBar();
        this.revalidate();
        this.repaint();
    }

    public void displayFlights() {
        List<Flight> allFlights = fbs.getFlights();
        List<Flight> availableFlights = new ArrayList<>();
        for (Flight flight : allFlights) {
            if (!flight.isDeleted() && !flight.hasDeparted()) {
                availableFlights.add(flight);
            }
        }
        
        String[] columns = {"Flight No", "Origin", "Destination", "Departure Date", "Available", "Capacity", "Price"};
        Object[][] data = new Object[availableFlights.size()][7];
        for (int i = 0; i < availableFlights.size(); i++) {
            Flight flight = availableFlights.get(i);
            data[i][0] = flight.getFlightNumber();
            data[i][1] = flight.getOrigin();
            data[i][2] = flight.getDestination();
            data[i][3] = flight.getDepartureDate();
            data[i][4] = flight.getAvailableSeats();
            data[i][5] = flight.getCapacity();
            data[i][6] = String.format("£%.2f", flight.calculateDynamicPrice(SystemDate.getCurrentDate()));
        }

        JTable table = new JTable(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        // Table Styling
        table.setRowHeight(30);
        table.setIntercellSpacing(new Dimension(10, 5));
        table.setSelectionBackground(ACCENT_COLOR);
        table.setSelectionForeground(Color.WHITE);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setGridColor(new Color(230, 230, 230));
        table.setShowVerticalLines(false);
        table.setAutoCreateRowSorter(true);

        JTableHeader header = table.getTableHeader();
        header.setBackground(Color.WHITE);
        header.setForeground(TEXT_COLOR);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(100, 35));
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        this.getContentPane().removeAll();
        
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        headerPanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("Available Flights");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(PRIMARY_COLOR);
        headerPanel.add(titleLabel);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        scrollPane.getViewport().setBackground(Color.WHITE);

        this.add(headerPanel, BorderLayout.NORTH);
        this.add(scrollPane, BorderLayout.CENTER);
        addStatusBar();
        
        this.revalidate();
        this.repaint();
    }

    private void showViewPassengersDialog() {
        List<Flight> allFlights = fbs.getFlights();
        List<Flight> availableFlights = new ArrayList<>();
        for (Flight flight : allFlights) {
            if (!flight.isDeleted() && !flight.hasDeparted()) {
                availableFlights.add(flight);
            }
        }
        
        if (availableFlights.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No available flights found.", "No Flights", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String[] flightOptions = new String[availableFlights.size()];
        for (int i = 0; i < availableFlights.size(); i++) {
            Flight f = availableFlights.get(i);
            flightOptions[i] = f.getFlightNumber() + " (" + f.getOrigin() + " → " + f.getDestination() + ")";
        }
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridy = 0;
        JLabel label = new JLabel("Select Flight to View Passengers:");
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(label, gbc);
        
        gbc.gridy = 1;
        JComboBox<String> flightCombo = new JComboBox<>(flightOptions);
        flightCombo.setPreferredSize(new Dimension(300, 30));
        panel.add(flightCombo, gbc);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Flight Roster", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            int selectedIndex = flightCombo.getSelectedIndex();
            if (selectedIndex >= 0) {
                new FlightsPassengersWindow(availableFlights.get(selectedIndex)).setVisible(true);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        Object source = ae.getSource();
        
        if (source == adminLogout) {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    FlightBookingSystemData.store(fbs);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error saving data: " + ex.getMessage());
                }
                this.dispose();
                new MainWindow(fbs);
            }
        } else if (source == adminExit) {
            int confirm = JOptionPane.showConfirmDialog(this, "Exit application and save changes?", "Confirm Exit", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    FlightBookingSystemData.store(fbs);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error saving data: " + ex.getMessage());
                }
                System.exit(0);
            }
        } else if (source == flightsView) {
            displayFlights();
        } else if (source == flightsViewPassengers) {
            showViewPassengersDialog();
        } else if (source == flightsAdd) {
            if (!isAdmin()) { showPermissionError(); return; }
            new AddFlightWindow(this);
        } else if (source == flightsDel) {
            if (!isAdmin()) { showPermissionError(); return; }
            new DeleteFlightWindow(this).setVisible(true);
        } else if (source == bookingsIssue) {
            new IssueBookingWindow(this).setVisible(true);
        } else if (source == bookingsCancel) {
            new CancelBookingWindow(this).setVisible(true);
        } else if (source == bookingsUpdate) {
            new UpdateBookingWindow(this);
        } else if (source == custView) {
            new CustomersWindow(fbs).setVisible(true);
        } else if (source == custAdd) {
            if (!isAdmin()) { showPermissionError(); return; }
            new AddCustomerWindow(this).setVisible(true);
        } else if (source == custDel) {
            if (!isAdmin()) { showPermissionError(); return; }
            new DeleteCustomerWindow(this).setVisible(true);
        }
    }
}

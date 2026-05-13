package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.commands.AddFlight;
import bcu.cmp5332.bookingsystem.commands.Command;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * Redesigned AddFlightWindow with a horizontal Label-Field layout.
 * Optimized for scannability and prevents vertical crowding.
 */
public class AddFlightWindow extends JFrame implements ActionListener {

    private MainWindow mw;
    private JTextField flightNoText = new JTextField();
    private JTextField originText = new JTextField();
    private JTextField destinationText = new JTextField();
    private JTextField depDateText = new JTextField();
    private JTextField capacityText = new JTextField();
    private JTextField priceText = new JTextField();

    private JButton addBtn = new JButton("ADD FLIGHT");
    private JButton cancelBtn = new JButton("CANCEL");

    private final Color PRIMARY_NAVY = new Color(44, 62, 80);
    private final Color SUCCESS_GREEN = new Color(46, 204, 113);
    private final Color DIVIDER_COLOR = new Color(220, 220, 220);
    private final Color TEXT_GRAY = new Color(80, 80, 80);

    public AddFlightWindow(MainWindow mw) {
        this.mw = mw;
        initialize();
    }

    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) { /* Ignore */ }

        setTitle("Add a New Flight");
        setSize(600, 520); // Shorter height since we are now wider
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // --- HEADER ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_NAVY);
        header.setBorder(new EmptyBorder(25, 20, 25, 20));
        
        JLabel brandLabel = new JLabel("FLIGHT MANAGEMENT");
        brandLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        brandLabel.setForeground(Color.WHITE);
        brandLabel.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(brandLabel, BorderLayout.CENTER);

        // --- FORM AREA (GRIDBAG FOR HORIZONTAL ALIGNMENT) ---
        JPanel formContainer = new JPanel(new GridBagLayout());
        formContainer.setOpaque(false);
        formContainer.setBorder(new EmptyBorder(30, 40, 30, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Column 1: Labels | Column 2: Fields
        addFormField("Flight No:", flightNoText, formContainer, gbc, 0);
        addFormField("Origin:", originText, formContainer, gbc, 1);
        addFormField("Destination:", destinationText, formContainer, gbc, 2);
        addFormField("Dep. Date:", depDateText, formContainer, gbc, 3);
        addFormField("Capacity:", capacityText, formContainer, gbc, 4);
        addFormField("Price (£):", priceText, formContainer, gbc, 5);

        // --- FOOTER ---
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        footer.setOpaque(false);
        styleButton(addBtn, SUCCESS_GREEN, Color.BLACK); 
        styleButton(cancelBtn, new Color(235, 235, 235), Color.BLACK);
        footer.add(addBtn);
        footer.add(cancelBtn);

        addBtn.addActionListener(this);
        cancelBtn.addActionListener(this);

        this.add(header, BorderLayout.NORTH);
        this.add(formContainer, BorderLayout.CENTER);
        this.add(footer, BorderLayout.SOUTH);

        setLocationRelativeTo(mw);
        setResizable(false);
        setVisible(true);
    }

    /**
     * Helper to add a label and field side-by-side
     */
    private void addFormField(String labelStr, JTextField field, JPanel panel, GridBagConstraints gbc, int row) {
        JLabel label = new JLabel(labelStr);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_GRAY);
        
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(250, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(DIVIDER_COLOR, 1),
            new EmptyBorder(5, 10, 5, 10)
        ));

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(field, gbc);
    }

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setPreferredSize(new Dimension(150, 45));
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
            addFlight();
        } else if (ae.getSource() == cancelBtn) {
            this.dispose();
        }
    }

    private void addFlight() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("CLI: INITIATING ADD FLIGHT COMMAND");
        System.out.println("═══════════════════════════════════════");
        
        try {
            String flightNumber = flightNoText.getText().trim();
            String origin = originText.getText().trim();
            String destination = destinationText.getText().trim();
            
            LocalDate departureDate = LocalDate.parse(depDateText.getText());
            int capacity = Integer.parseInt(capacityText.getText());
            double price = Double.parseDouble(priceText.getText());

            if (flightNumber.isEmpty() || origin.isEmpty() || destination.isEmpty()) {
                throw new FlightBookingSystemException("Required text fields cannot be empty");
            }

            Command addFlight = new AddFlight(flightNumber, origin, destination, departureDate, capacity, price);
            addFlight.execute(mw.getFlightBookingSystem());

            // Full CLI Logging
            System.out.println("SUCCESS: New Flight Created");
            System.out.println("Flight No: " + flightNumber);
            System.out.println("Route:     " + origin + " -> " + destination);
            System.out.println("Capacity:  " + capacity + " seats");
            System.out.println("Price:     £" + price);
            System.out.println("═══════════════════════════════════════");

            mw.displayFlights();
            this.dispose();
            JOptionPane.showMessageDialog(mw, "Flight Created Successfully!", "Flight Booking System", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (DateTimeParseException dtpe) {
            System.out.println("ERROR: Invalid Date Format");
            JOptionPane.showMessageDialog(this, "Date must be YYYY-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException nfe) {
            System.out.println("ERROR: Numeric Parse Failure");
            JOptionPane.showMessageDialog(this, "Check Capacity and Price values.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (FlightBookingSystemException ex) {
            System.out.println("ERROR: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
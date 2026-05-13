package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.commands.DeleteFlight;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * Modernized DeleteFlightWindow for the Flight Booking System.
 * Uses a side-by-side layout with high-visibility warnings for destructive actions.
 */
public class DeleteFlightWindow extends JFrame implements ActionListener {
    
    private MainWindow mw;
    private JComboBox<String> flightCombo;
    private JButton deleteBtn = new JButton("DELETE FLIGHT");
    private JButton cancelBtn = new JButton("CANCEL");

    // UI Styles
    private final Color PRIMARY_NAVY = new Color(44, 62, 80);
    private final Color DANGER_RED = new Color(231, 76, 60);
    private final Color TEXT_GRAY = new Color(80, 80, 80);
    
    public DeleteFlightWindow(MainWindow mw) {
        this.mw = mw;
        initialize();
    }
    
    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) { /* Ignore */ }
        
        setTitle("System Administration: Delete Flight");
        setSize(550, 320);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // --- HEADER ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_NAVY);
        header.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel brandLabel = new JLabel("REMOVE FLIGHT RECORD");
        brandLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        brandLabel.setForeground(Color.WHITE);
        header.add(brandLabel, BorderLayout.CENTER);

        // --- FORM PANEL ---
        JPanel formContainer = new JPanel(new GridBagLayout());
        formContainer.setOpaque(false);
        formContainer.setBorder(new EmptyBorder(30, 40, 10, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Row 0: Selection
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        JLabel selectLabel = new JLabel("Select Flight:");
        selectLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        selectLabel.setForeground(TEXT_GRAY);
        formContainer.add(selectLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        flightCombo = new JComboBox<>(populateFlightOptions());
        flightCombo.setPreferredSize(new Dimension(300, 35));
        formContainer.add(flightCombo, gbc);

        // Row 1: Warning
        gbc.gridx = 1; gbc.gridy = 1;
        JLabel warningHint = new JLabel("<html><i>Warning: Deleting a flight will affect all active<br>bookings associated with this flight ID.</i></html>");
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

    private String[] populateFlightOptions() {
        return mw.getFlightBookingSystem().getFlights().stream()
                .filter(f -> !f.isDeleted())
                .map(f -> f.getId() + " - " + f.getFlightNumber() + " (" + f.getOrigin() + " to " + f.getDestination() + ")")
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
            deleteFlight();
        } else if (ae.getSource() == cancelBtn) {
            this.dispose();
        }
    }

    private void deleteFlight() {
        try {
            String selected = (String) flightCombo.getSelectedItem();
            if (selected == null) throw new FlightBookingSystemException("No flight selected.");
            
            int flightId = Integer.parseInt(selected.split(" - ")[0]);
            
            int confirm = JOptionPane.showConfirmDialog(this, 
                "CRITICAL: Delete Flight ID " + flightId + "?\n\n" +
                "Route: " + selected + "\n\n" +
                "This will mark the flight as deleted in the system.", 
                "Confirm Flight Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                new DeleteFlight(flightId).execute(mw.getFlightBookingSystem());
                
                // CLI Feedback
                System.out.println("═══════════════════════════════════════");
                System.out.println("CLI: FLIGHT DELETION EXECUTED");
                System.out.println("ID:      " + flightId);
                System.out.println("Details: " + selected);
                System.out.println("═══════════════════════════════════════");
                
                mw.displayFlights();
                this.dispose();
                JOptionPane.showMessageDialog(mw, "Flight record has been successfully removed.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Operation Failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
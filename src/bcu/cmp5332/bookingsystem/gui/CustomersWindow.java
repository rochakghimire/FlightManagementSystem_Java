package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * Modernized CustomersWindow for the Flight Booking System.
 * Lists all customers in a professional table with double-click drill-down capability.
 */
public class CustomersWindow extends JFrame {
    
    private FlightBookingSystem fbs;
    private final Color PRIMARY_NAVY = new Color(44, 62, 80);
    private final Color TEXT_GRAY = new Color(80, 80, 80);
    private final Color ACCENT_BLUE = new Color(52, 152, 219);

    public CustomersWindow(FlightBookingSystem fbs) {
        this.fbs = fbs;
        initialize();
    }
    
    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) { /* Ignore */ }
        
        setTitle("Customer Directory");
        setSize(850, 600);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // --- CLI LOGGING ---
        System.out.println("═══════════════════════════════════════");
        System.out.println("CLI: ACCESSING CUSTOMER DIRECTORY");
        System.out.println("Total Customers Found: " + fbs.getCustomers().size());
        System.out.println("═══════════════════════════════════════");

        // --- HEADER SECTION ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_NAVY);
        headerPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel titleLabel = new JLabel("CUSTOMER MANAGEMENT");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel countLabel = new JLabel(fbs.getCustomers().size() + " Total Records");
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        countLabel.setForeground(new Color(200, 200, 200));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(countLabel, BorderLayout.EAST);

        // --- TABLE PREPARATION ---
        String[] columns = {"ID", "Full Name", "Phone Number", "Email Address", "Active Bookings"};
        Object[][] data = new Object[fbs.getCustomers().size()][5];
        
        int i = 0;
        for (Customer customer : fbs.getCustomers()) {
            data[i][0] = customer.getId();
            data[i][1] = customer.getName();
            data[i][2] = customer.getPhone();
            data[i][3] = customer.getEmail();
            data[i][4] = customer.getBookings().size();
            i++;
        }

        JTable table = new JTable(data, columns);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(235, 245, 255));
        table.setSelectionForeground(Color.BLACK);
        table.setGridColor(new Color(245, 245, 245));
        table.setShowVerticalLines(false);

        // Header Styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(248, 249, 250));
        header.setForeground(TEXT_GRAY);
        header.setPreferredSize(new Dimension(100, 45));
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        // Center-align the ID and Booking Count columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        // --- FOOTER / INSTRUCTION BAR ---
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 15));
        footer.setBackground(new Color(252, 252, 252));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)));

        JLabel hintLabel = new JLabel("💡 Tip: Double-click any row to view full booking history for that customer.");
        hintLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        hintLabel.setForeground(ACCENT_BLUE);
        footer.add(hintLabel);

        // --- MOUSE LISTENER ---
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        int customerId = (int) table.getValueAt(row, 0);
                        String customerName = (String) table.getValueAt(row, 1);
                        System.out.println("CLI: Opening details for Customer ID " + customerId + " (" + customerName + ")");
                        try {
                            Customer customer = fbs.getCustomerByID(customerId);
                            new CustomerBookingsWindow(customer);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(CustomersWindow.this, "Error: " + ex.getMessage());
                        }
                    }
                }
            }
        });

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
package bcu.cmp5332.bookingsystem.data;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Data manager for customer persistence.
 * Handles loading and saving customer data including authentication details.
 * 
 * File format: id::name::phone::email::password::role::deleted
 * 
 * @author Flight Booking System Team
 * @version 2.0
 */
public class CustomerDataManager implements DataManager {

    private final String RESOURCE = "./resources/data/customers.txt";
    
    /**
     * Loads customer data from file.
     * Supports both old format (without password/role) and new format for backward compatibility.
     * 
     * @param fbs The flight booking system to load data into
     * @throws IOException if file cannot be read
     * @throws FlightBookingSystemException if data is invalid
     */
    @Override
    public void loadData(FlightBookingSystem fbs) throws IOException, FlightBookingSystemException {
        try (Scanner sc = new Scanner(new File(RESOURCE))) {
            int line_idx = 1;
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] properties = line.split(SEPARATOR, -1);
                try {
                    int id = Integer.parseInt(properties[0]);
                    String name = properties[1];
                    String phone = properties[2];
                    String email = properties[3];
                    
                    // Handle both old and new formats for backward compatibility
                    String password;
                    String roleStr;
                    boolean deleted;
                    
                    if (properties.length >= 7) {
                        // NEW FORMAT: id::name::phone::email::password::role::deleted
                        password = properties[4];
                        roleStr = properties[5];
                        deleted = Boolean.parseBoolean(properties[6]);
                    } else if (properties.length >= 5) {
                        // OLD FORMAT: id::name::phone::email::deleted
                        password = "password123"; // Default password for old data
                        roleStr = "CUSTOMER"; // Default role
                        deleted = Boolean.parseBoolean(properties[4]);
                    } else {
                        throw new FlightBookingSystemException("Invalid data format on line " + line_idx);
                    }
                    
                    // Parse role
                    Customer.UserRole role;
                    try {
                        role = Customer.UserRole.valueOf(roleStr);
                    } catch (IllegalArgumentException e) {
                        role = Customer.UserRole.CUSTOMER; // Default to CUSTOMER if invalid
                    }
                    
                    // Create customer with all fields
                    Customer customer = new Customer(id, name, phone, email, password, role);
                    customer.setDeleted(deleted);
                    fbs.addCustomer(customer);
                    
                } catch (NumberFormatException ex) {
                    throw new FlightBookingSystemException("Unable to parse customer id " + properties[0] 
                        + " on line " + line_idx + "\nError: " + ex);
                }
                line_idx++;
            }
        }
    }

    /**
     * Saves customer data to file.
     * Uses new format including password and role.
     * 
     * Format: id::name::phone::email::password::role::deleted
     * 
     * @param fbs The flight booking system to save data from
     * @throws IOException if file cannot be written
     */
    @Override
    public void storeData(FlightBookingSystem fbs) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(RESOURCE))) {
            for (Customer customer : fbs.getCustomers()) {
                out.print(customer.getId() + SEPARATOR);
                out.print(customer.getName() + SEPARATOR);
                out.print(customer.getPhone() + SEPARATOR);
                out.print(customer.getEmail() + SEPARATOR);
                out.print(customer.getPassword() + SEPARATOR);
                out.print(customer.getRole() + SEPARATOR);
                out.print(customer.isDeleted() + SEPARATOR);
                out.println();
            }
        }
    }
}
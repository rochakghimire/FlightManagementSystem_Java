package bcu.cmp5332.bookingsystem.model;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import java.util.ArrayList;
import java.util.List;

public class Customer {
    
    private int id;
    private String name;
    private String phone;
    private String email;
    private boolean deleted = false;
    private final List<Booking> bookings = new ArrayList<>();
    private String password;
    private UserRole role;

    /**
     * Enum for user roles
     */
    public enum UserRole {
        CUSTOMER,
        ADMIN
    }
    
    // Constructor
    public Customer(int id, String name, String phone, String email, String password, UserRole role) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Keep old constructor for compatibility
    public Customer(int id, String name, String phone, String email) {
        this(id, name, phone, email, "password123", UserRole.CUSTOMER);
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
    
    

    public List<Booking> getBookings() {
        return bookings;
    }
    
    // Get short details (one line)
    public String getDetailsShort() {
        return "Customer #" + id + " - " + name + " - " + phone + " - " + email;
    }
    
    // Get detailed information including bookings
    public String getDetailsLong() {
        StringBuilder sb = new StringBuilder();
        sb.append("Customer #").append(id).append("\n");
        sb.append("Name: ").append(name).append("\n");
        sb.append("Phone: ").append(phone).append("\n");
        sb.append("Email: ").append(email).append("\n");
        sb.append("--------------------------\n");
        sb.append("Bookings:\n");
        
        for (Booking booking : bookings) {
            sb.append("* Booking date: ").append(booking.getBookingDate())
              .append(" for ").append(booking.getFlight().getDetailsShort()).append("\n");
        }
        
        sb.append(bookings.size()).append(" booking(s)");
        return sb.toString();
    }
    
    // Add a booking to customer's list
    public void addBooking(Booking booking) throws FlightBookingSystemException {
        // Check if customer already has a booking for this flight
        for (Booking b : bookings) {
            if (b.getFlight().getId() == booking.getFlight().getId()) {
                throw new FlightBookingSystemException("Customer already has a booking for this flight.");
            }
        }
        bookings.add(booking);
    }
    
    // Cancel booking for a specific flight
    public void cancelBookingForFlight(Flight flight) throws FlightBookingSystemException {
        boolean found = false;
        Booking toRemove = null;
        
        for (Booking booking : bookings) {
            if (booking.getFlight().getId() == flight.getId()) {
                toRemove = booking;
                found = true;
                break;
            }
        }
        
        if (!found) {
            throw new FlightBookingSystemException("Customer does not have a booking for this flight.");
        }
        
        bookings.remove(toRemove);
    }
    
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }

    public boolean validatePassword(String inputPassword) {
        if (this.password == null) {
            return inputPassword == null || inputPassword.equals("password123");
        }
        return this.password.equals(inputPassword);
    }
}
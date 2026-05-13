package bcu.cmp5332.bookingsystem.model;

import java.time.LocalDate;

public class Booking {
    
    private Customer customer;
    private Flight flight;
    private LocalDate bookingDate;
    private double bookingPrice;           // Price at time of booking
    private double cancellationFee = 0.0;  // NEW - Cancellation fee field
    /**
     * Enum for booking status
     */
    public enum BookingStatus {
        ACTIVE,
        CANCELLED,
        COMPLETED
    }

    private BookingStatus status;
    
    // Constructor with booking price
    public Booking(Customer customer, Flight flight, LocalDate bookingDate, double bookingPrice) {
        this.customer = customer;
        this.flight = flight;
        this.bookingDate = bookingDate;
        this.bookingPrice = bookingPrice;
        this.status = BookingStatus.ACTIVE; // Default status
    }
    
    // Getters and Setters
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public double getBookingPrice() {
        return bookingPrice;
    }

    public void setBookingPrice(double bookingPrice) {
        this.bookingPrice = bookingPrice;
    }
    
    // NEW - Cancellation fee getter and setter
    public double getCancellationFee() {
        return cancellationFee;
    }

    public void setCancellationFee(double cancellationFee) {
        this.cancellationFee = cancellationFee;
    }
    
    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    /**
     * Checks if booking is active.
     * @return true if status is ACTIVE
     */
    public boolean isActive() {
        return status == BookingStatus.ACTIVE;
    }

    /**
     * Cancels this booking.
     * Sets status to CANCELLED and applies cancellation fee.
     */
    public void cancel() {
        this.status = BookingStatus.CANCELLED;
        // Calculate cancellation fee (15% of booking price)
        this.cancellationFee = this.bookingPrice * 0.15;
    }

    /**
     * Marks booking as completed (flight has departed).
     */
    public void complete() {
        this.status = BookingStatus.COMPLETED;
    }
}
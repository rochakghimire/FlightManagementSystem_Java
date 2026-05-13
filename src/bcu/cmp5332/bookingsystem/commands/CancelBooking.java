package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import java.time.LocalDate;

public class CancelBooking implements Command {
    
    private final int customerId;
    private final int flightId;
    
    // CONSTRUCTOR - Make sure you have this!
    public CancelBooking(int customerId, int flightId) {
        this.customerId = customerId;
        this.flightId = flightId;
    }

    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        Customer customer = flightBookingSystem.getCustomerByID(customerId);
        Flight flight = flightBookingSystem.getFlightByID(flightId);
        
        // Find the booking first to get its price and calculate cancellation fee
        Booking bookingToCancel = null;
        for (Booking booking : customer.getBookings()) {
            if (booking.getFlight().getId() == flightId) {
                bookingToCancel = booking;
                break;
            }
        }
        
        if (bookingToCancel == null) {
            throw new FlightBookingSystemException("Customer does not have a booking for this flight.");
        }
        
        // Calculate cancellation fee based on how close to departure
        LocalDate today = flightBookingSystem.getSystemDate();
        long daysUntilDeparture = java.time.temporal.ChronoUnit.DAYS.between(today, flight.getDepartureDate());
        
        double cancellationFee = 0.0;
        if (daysUntilDeparture < 0) {
            // Flight has already departed
            throw new FlightBookingSystemException("Cannot cancel booking. Flight has already departed.");
        } else if (daysUntilDeparture <= 2) {
            // 50% cancellation fee for cancellations within 2 days
            cancellationFee = bookingToCancel.getBookingPrice() * 0.5;
        } else if (daysUntilDeparture <= 7) {
            // 30% cancellation fee for cancellations within a week
            cancellationFee = bookingToCancel.getBookingPrice() * 0.3;
        } else if (daysUntilDeparture <= 14) {
            // 15% cancellation fee for cancellations within 2 weeks
            cancellationFee = bookingToCancel.getBookingPrice() * 0.15;
        } else {
            // No fee for cancellations more than 2 weeks in advance
            cancellationFee = 0.0;
        }
        
        // Set the cancellation fee
        bookingToCancel.setCancellationFee(cancellationFee);
        
        // Cancel the booking
        customer.cancelBookingForFlight(flight);
        flight.removePassenger(customer);
        
        double refundAmount = bookingToCancel.getBookingPrice() - cancellationFee;
        
        System.out.println("=================================");
        System.out.println("Booking cancelled successfully!");
        System.out.println("Customer: " + customer.getName());
        System.out.println("Flight: " + flight.getFlightNumber());
        System.out.println("Original Price: £" + String.format("%.2f", bookingToCancel.getBookingPrice()));
        System.out.println("Cancellation Fee: £" + String.format("%.2f", cancellationFee));
        System.out.println("Refund Amount: £" + String.format("%.2f", refundAmount));
        System.out.println("=================================");
    }
}
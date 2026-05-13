package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import java.time.LocalDate;

/**
 * Command to update (change) a customer's booking to a different flight
 * This involves cancelling the old booking and creating a new one
 * 
 * @author Rochak & Samyam
 */
public class UpdateBooking implements Command {
    
    private final int customerId;
    private final int oldFlightId;
    private final int newFlightId;
    
    /**
     * Constructor for UpdateBooking command
     * 
     * @param customerId The customer whose booking to update
     * @param oldFlightId The current flight ID
     * @param newFlightId The new flight ID to book
     */
    public UpdateBooking(int customerId, int oldFlightId, int newFlightId) {
        this.customerId = customerId;
        this.oldFlightId = oldFlightId;
        this.newFlightId = newFlightId;
    }

    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        Customer customer = flightBookingSystem.getCustomerByID(customerId);
        Flight oldFlight = flightBookingSystem.getFlightByID(oldFlightId);
        Flight newFlight = flightBookingSystem.getFlightByID(newFlightId);
        
        // Check if new flight is at full capacity
        if (newFlight.getPassengers().size() >= newFlight.getCapacity()) {
            throw new FlightBookingSystemException(
                "Cannot update booking. The new flight is at full capacity.");
        }
        
        // Find the old booking
        Booking oldBooking = null;
        for (Booking booking : customer.getBookings()) {
            if (booking.getFlight().getId() == oldFlightId) {
                oldBooking = booking;
                break;
            }
        }
        
        if (oldBooking == null) {
            throw new FlightBookingSystemException(
                "Customer does not have a booking for the old flight.");
        }
        
        // Calculate rebook fee based on how close to departure
        LocalDate today = flightBookingSystem.getSystemDate();
        long daysUntilDeparture = java.time.temporal.ChronoUnit.DAYS.between(today, oldFlight.getDepartureDate());
        
        double rebookFee = 0.0;
        if (daysUntilDeparture < 0) {
            throw new FlightBookingSystemException("Cannot update booking. Old flight has already departed.");
        } else if (daysUntilDeparture <= 2) {
            // 50% rebook fee for changes within 2 days
            rebookFee = oldBooking.getBookingPrice() * 0.5;
        } else if (daysUntilDeparture <= 7) {
            // 30% rebook fee for changes within a week
            rebookFee = oldBooking.getBookingPrice() * 0.3;
        } else if (daysUntilDeparture <= 14) {
            // 15% rebook fee for changes within 2 weeks
            rebookFee = oldBooking.getBookingPrice() * 0.15;
        } else {
            // 10% rebook fee for early changes
            rebookFee = oldBooking.getBookingPrice() * 0.1;
        }
        
        // Cancel the old booking
        customer.cancelBookingForFlight(oldFlight);
        oldFlight.removePassenger(customer);
        
        // Calculate new price for the new flight
        LocalDate bookingDate = flightBookingSystem.getSystemDate();
        double newPrice = newFlight.calculateDynamicPrice(bookingDate);
        
        // Create new booking
        Booking newBooking = new Booking(customer, newFlight, bookingDate, newPrice);
        customer.addBooking(newBooking);
        newFlight.addPassenger(customer);
        
        // Calculate total cost (new price + rebook fee)
        double totalCost = newPrice + rebookFee;
        double refundFromOld = oldBooking.getBookingPrice() - rebookFee;
        double amountToPay = totalCost - refundFromOld;
        
        System.out.println("=================================");
        System.out.println("Booking updated successfully!");
        System.out.println("Customer: " + customer.getName());
        System.out.println();
        System.out.println("OLD BOOKING:");
        System.out.println("  Flight: " + oldFlight.getFlightNumber());
        System.out.println("  Original Price: £" + String.format("%.2f", oldBooking.getBookingPrice()));
        System.out.println("  Rebook Fee: £" + String.format("%.2f", rebookFee));
        System.out.println("  Refund: £" + String.format("%.2f", refundFromOld));
        System.out.println();
        System.out.println("NEW BOOKING:");
        System.out.println("  Flight: " + newFlight.getFlightNumber() + " (" + newFlight.getOrigin() + " to " + newFlight.getDestination() + ")");
        System.out.println("  Departure: " + newFlight.getDepartureDate());
        System.out.println("  New Price: £" + String.format("%.2f", newPrice));
        System.out.println();
        System.out.println("TOTAL:");
        if (amountToPay > 0) {
            System.out.println("  Amount to Pay: £" + String.format("%.2f", amountToPay));
        } else {
            System.out.println("  Refund Due: £" + String.format("%.2f", Math.abs(amountToPay)));
        }
        System.out.println("=================================");
    }
}
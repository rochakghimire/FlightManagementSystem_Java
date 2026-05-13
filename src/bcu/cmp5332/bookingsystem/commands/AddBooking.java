package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import java.time.LocalDate;
import bcu.cmp5332.bookingsystem.data.DataPersistenceManager;

public class AddBooking implements Command {
    
    private final int customerId;
    private final int flightId;
    
    public AddBooking(int customerId, int flightId) {
        this.customerId = customerId;
        this.flightId = flightId;
    }

    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        Customer customer = flightBookingSystem.getCustomerByID(customerId);
        Flight flight = flightBookingSystem.getFlightByID(flightId);
        
        // Check if flight is at full capacity
        if (flight.getPassengers().size() >= flight.getCapacity()) {
            throw new FlightBookingSystemException(
                "Cannot book flight. The flight is at full capacity (" + flight.getCapacity() + " seats).");
        }
        
        LocalDate bookingDate = flightBookingSystem.getSystemDate();
        
        // Calculate dynamic price based on booking date and current capacity
        double dynamicPrice = flight.calculateDynamicPrice(bookingDate);
        
        // Create booking with the calculated price
        Booking booking = new Booking(customer, flight, bookingDate, dynamicPrice);
        
        customer.addBooking(booking);
        flight.addPassenger(customer);
        
        // Save data after booking (if you implemented DataPersistenceManager)
        DataPersistenceManager.safeStore(flightBookingSystem);
        
        System.out.println("=================================");
        System.out.println("Booking successfully issued!");
        System.out.println("Customer: " + customer.getName());
        System.out.println("Flight: " + flight.getFlightNumber() + " (" + flight.getOrigin() + " to " + flight.getDestination() + ")");
        System.out.println("Departure: " + flight.getDepartureDate());
        System.out.println("Price: £" + String.format("%.2f", dynamicPrice));
        System.out.println("Remaining seats: " + (flight.getCapacity() - flight.getPassengers().size()));
        System.out.println("=================================");
    }
}
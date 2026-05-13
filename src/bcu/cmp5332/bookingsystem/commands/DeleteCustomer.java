package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.data.DataPersistenceManager;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
 * Command to delete (hide) a customer from the system
 * 
 * @author Rochak & Samyam
 * @version 1.0
 */
public class DeleteCustomer implements Command {
    
    private final int customerId;
    
    /**
     * Constructor for DeleteCustomer command
     * 
     * @param customerId The ID of the customer to delete
     */
    public DeleteCustomer(int customerId) {
        this.customerId = customerId;
    }
    
    /**
     * Executes the delete customer command by marking the customer as deleted
     * 
     * @param flightBookingSystem The flight booking system
     * @throws FlightBookingSystemException if customer not found or has active bookings
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        Customer customer = flightBookingSystem.getCustomerByID(customerId);
        
        // Check if customer has bookings
        if (!customer.getBookings().isEmpty()) {
            throw new FlightBookingSystemException(
                "Cannot delete customer with active bookings. Please cancel all bookings first.");
        }
        
        customer.setDeleted(true);
        
        // Save data after deletion
        DataPersistenceManager.safeStore(flightBookingSystem);
        
        System.out.println("Customer #" + customerId + " deleted successfully.");
    }
}
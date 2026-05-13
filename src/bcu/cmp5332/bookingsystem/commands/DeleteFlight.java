package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.data.DataPersistenceManager;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
 * Command to delete (hide) a flight from the system
 * 
 * @author Rochak & Samyam
 * @version 1.0
 */
public class DeleteFlight implements Command {
    
    private final int flightId;
    
    /**
     * Constructor for DeleteFlight command
     * 
     * @param flightId The ID of the flight to delete
     */
    public DeleteFlight(int flightId) {
        this.flightId = flightId;
    }
    
    /**
     * Executes the delete flight command by marking the flight as deleted
     * 
     * @param flightBookingSystem The flight booking system
     * @throws FlightBookingSystemException if flight not found or has active bookings
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        Flight flight = flightBookingSystem.getFlightByID(flightId);
        
        // Check if flight has passengers (bookings)
        if (!flight.getPassengers().isEmpty()) {
            throw new FlightBookingSystemException(
                "Cannot delete flight with active bookings. Please cancel all bookings first.");
        }
        
        flight.setDeleted(true);
        
        // Save data after deletion
        DataPersistenceManager.safeStore(flightBookingSystem);
        
        System.out.println("Flight #" + flightId + " deleted successfully.");
    }
}
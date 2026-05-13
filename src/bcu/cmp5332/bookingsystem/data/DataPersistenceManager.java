package bcu.cmp5332.bookingsystem.data;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import java.io.IOException;

public class DataPersistenceManager {
    
    /**
     * Saves data with rollback capability on failure
     */
    public static void safeStore(FlightBookingSystem fbs) throws FlightBookingSystemException {
        // Create backup before saving
        FlightBookingSystem backup = null;
        
        try {
            // Load current state as backup
            backup = FlightBookingSystemData.load();
            
            // Attempt to store new data
            FlightBookingSystemData.store(fbs);
            
        } catch (IOException e) {
            // If storage fails, attempt to restore from backup
            if (backup != null) {
                try {
                    FlightBookingSystemData.store(backup);
                    throw new FlightBookingSystemException(
                        "Failed to save data. System has been rolled back to previous state. Error: " + e.getMessage());
                } catch (IOException rollbackError) {
                    throw new FlightBookingSystemException(
                        "Critical error: Failed to save data AND failed to rollback. " +
                        "Please check file permissions. Original error: " + e.getMessage() +
                        ", Rollback error: " + rollbackError.getMessage());
                }
            } else {
                throw new FlightBookingSystemException(
                    "Failed to save data and no backup available. Error: " + e.getMessage());
            }
        }
    }
}
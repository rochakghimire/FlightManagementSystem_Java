package bcu.cmp5332.bookingsystem.test;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
//import bcu.cmp5332.bookingsystem.model.Customer;
import java.time.LocalDate;

public class FlightTest {
    
    public static void main(String[] args) {
        try {
            testFlightCreation();
            testCapacityAndPrice();
            System.out.println("All Flight tests passed!");
        } catch (Exception e) {
            System.out.println("Test failed: " + e.getMessage());
        }
    }
    
    private static void testFlightCreation() {
        Flight flight = new Flight(1, "BA123", "London", "Paris", 
                                   LocalDate.now(), 200, 99.99);
        assert flight.getId() == 1;
        assert flight.getCapacity() == 200;
        assert flight.getPrice() == 99.99;
        System.out.println("✓ Flight creation test passed");
    }
    
    private static void testCapacityAndPrice() throws FlightBookingSystemException {
        Flight flight = new Flight(1, "BA123", "London", "Paris", 
                                   LocalDate.now(), 200, 99.99);
        flight.setCapacity(250);
        flight.setPrice(149.99);
        assert flight.getCapacity() == 250;
        assert flight.getPrice() == 149.99;
        System.out.println("✓ Capacity and price modification test passed");
    }
}
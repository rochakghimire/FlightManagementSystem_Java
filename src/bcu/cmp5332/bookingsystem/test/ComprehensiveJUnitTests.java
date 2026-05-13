package bcu.cmp5332.bookingsystem.test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.*;
import bcu.cmp5332.bookingsystem.util.SystemDate;
import java.time.LocalDate;

/**
 * Comprehensive JUnit test suite for Flight Booking System.
 * Tests core functionality of Flight, Customer, Booking, and FlightBookingSystem classes.
 * 
 * @author Rochak Ghimire
 * @author Samyam Chapagain
 * @version 2.0
 */
public class ComprehensiveJUnitTests {
    
    private Flight testFlight;
    private Customer testCustomer;
    private FlightBookingSystem fbs;
    private LocalDate futureDate;
    private LocalDate pastDate;
    
    /**
     * Sets up test fixtures before each test method.
     * Initializes test objects with sample data.
     */
    @Before
    public void setUp() {
        futureDate = LocalDate.now().plusDays(30);
        pastDate = LocalDate.now().minusDays(5);
        
        testFlight = new Flight(1, "BA123", "London", "Paris", futureDate, 150, 100.00);
        testCustomer = new Customer(1, "John Doe", "1234567890", "john@example.com", "password123", Customer.UserRole.CUSTOMER);
        fbs = new FlightBookingSystem();
    }
    
    /**
     * Test 1: Verify flight creation with valid parameters.
     * Ensures all flight properties are correctly initialized.
     */
    @Test
    public void testFlightCreation() {
        assertEquals("Flight ID should be 1", 1, testFlight.getId());
        assertEquals("Flight number should be BA123", "BA123", testFlight.getFlightNumber());
        assertEquals("Origin should be London", "London", testFlight.getOrigin());
        assertEquals("Destination should be Paris", "Paris", testFlight.getDestination());
        assertEquals("Capacity should be 150", 150, testFlight.getCapacity());
        assertEquals("Price should be 100.00", 100.00, testFlight.getPrice(), 0.01);
    }
    
    /**
     * Test 2: Verify customer creation with valid parameters.
     * Ensures all customer properties are correctly initialized.
     */
    @Test
    public void testCustomerCreation() {
        assertEquals("Customer ID should be 1", 1, testCustomer.getId());
        assertEquals("Customer name should be John Doe", "John Doe", testCustomer.getName());
        assertEquals("Phone should be 1234567890", "1234567890", testCustomer.getPhone());
        assertEquals("Email should be john@example.com", "john@example.com", testCustomer.getEmail());
        assertFalse("Customer should not be deleted", testCustomer.isDeleted());
    }
    
    /**
     * Test 3: Test adding passenger to flight successfully.
     * Verifies passenger is added and seat count is updated correctly.
     * 
     * @throws FlightBookingSystemException if passenger cannot be added
     */
    @Test
    public void testAddPassengerToFlight() throws FlightBookingSystemException {
        int initialAvailableSeats = testFlight.getAvailableSeats();
        testFlight.addPassenger(testCustomer);
        
        assertEquals("Available seats should decrease by 1", 
                     initialAvailableSeats - 1, testFlight.getAvailableSeats());
        assertTrue("Flight should contain the passenger", 
                   testFlight.getPassengers().contains(testCustomer));
    }
    
    /**
     * Test 4: Test that adding duplicate passenger throws exception.
     * Ensures the system prevents double-booking for same passenger.
     * 
     * @throws FlightBookingSystemException expected exception when adding duplicate
     */
    @Test(expected = FlightBookingSystemException.class)
    public void testAddDuplicatePassenger() throws FlightBookingSystemException {
        testFlight.addPassenger(testCustomer);
        testFlight.addPassenger(testCustomer); // Should throw exception
    }
    
    /**
     * Test 5: Test removing passenger from flight.
     * Verifies passenger removal updates seat count correctly.
     * 
     * @throws FlightBookingSystemException if passenger operations fail
     */
    @Test
    public void testRemovePassengerFromFlight() throws FlightBookingSystemException {
        testFlight.addPassenger(testCustomer);
        int seatsAfterAdd = testFlight.getAvailableSeats();
        
        testFlight.removePassenger(testCustomer);
        
        assertEquals("Available seats should increase after removal", 
                     seatsAfterAdd + 1, testFlight.getAvailableSeats());
        assertFalse("Flight should not contain the passenger after removal", 
                    testFlight.getPassengers().contains(testCustomer));
    }
    
    /**
     * Test 6: Test that removing non-existent passenger throws exception.
     * Ensures proper error handling for invalid operations.
     * 
     * @throws FlightBookingSystemException expected exception
     */
    @Test(expected = FlightBookingSystemException.class)
    public void testRemoveNonExistentPassenger() throws FlightBookingSystemException {
        testFlight.removePassenger(testCustomer); // Should throw exception
    }
    
    /**
     * Test 7: Test flight capacity check when full.
     * Verifies isFull() method works correctly.
     * 
     * @throws FlightBookingSystemException if passenger operations fail
     */
    @Test
    public void testFlightFullCapacity() throws FlightBookingSystemException {
        Flight smallFlight = new Flight(2, "BA456", "London", "Rome", futureDate, 2, 150.00);
        
        assertFalse("Flight should not be full initially", smallFlight.isFull());
        
        Customer customer1 = new Customer(1, "Alice", "111", "alice@test.com");
        Customer customer2 = new Customer(2, "Bob", "222", "bob@test.com");
        
        smallFlight.addPassenger(customer1);
        assertFalse("Flight should not be full with 1 passenger", smallFlight.isFull());
        
        smallFlight.addPassenger(customer2);
        assertTrue("Flight should be full with 2 passengers", smallFlight.isFull());
    }
    
    /**
     * Test 8: Test that adding passenger to full flight throws exception.
     * Ensures capacity constraints are enforced.
     * 
     * @throws FlightBookingSystemException expected exception when capacity exceeded
     */
    @Test(expected = FlightBookingSystemException.class)
    public void testAddPassengerToFullFlight() throws FlightBookingSystemException {
        Flight smallFlight = new Flight(2, "BA456", "London", "Rome", futureDate, 1, 150.00);
        Customer customer1 = new Customer(1, "Alice", "111", "alice@test.com");
        Customer customer2 = new Customer(2, "Bob", "222", "bob@test.com");
        
        smallFlight.addPassenger(customer1);
        smallFlight.addPassenger(customer2); // Should throw exception - flight is full
    }
    
    /**
     * Test 9: Test booking creation and price calculation.
     * Verifies booking is created with correct price.
     */
    @Test
    public void testBookingCreation() {
        LocalDate bookingDate = LocalDate.now();
        double expectedPrice = testFlight.calculateDynamicPrice(bookingDate);
        
        Booking booking = new Booking(testCustomer, testFlight, bookingDate, expectedPrice);
        
        assertEquals("Customer should match", testCustomer, booking.getCustomer());
        assertEquals("Flight should match", testFlight, booking.getFlight());
        assertEquals("Booking date should match", bookingDate, booking.getBookingDate());
        assertEquals("Booking price should match", expectedPrice, booking.getBookingPrice(), 0.01);
        assertTrue("Booking should be active", booking.isActive());
    }
    
    /**
     * Test 10: Test booking cancellation and fee calculation.
     * Verifies booking status changes and cancellation fee is applied.
     */
    @Test
    public void testBookingCancellation() {
        LocalDate bookingDate = LocalDate.now();
        double bookingPrice = 200.00;
        Booking booking = new Booking(testCustomer, testFlight, bookingDate, bookingPrice);
        
        assertTrue("Booking should be active before cancellation", booking.isActive());
        
        booking.cancel();
        
        assertFalse("Booking should not be active after cancellation", booking.isActive());
        assertEquals("Status should be CANCELLED", 
                     Booking.BookingStatus.CANCELLED, booking.getStatus());
        assertEquals("Cancellation fee should be 15% of booking price", 
                     bookingPrice * 0.15, booking.getCancellationFee(), 0.01);
    }
    
    /**
     * Test 11: Test dynamic pricing based on time until departure.
     * Verifies price increases for last-minute bookings.
     */
    @Test
    public void testDynamicPricingTimeMultiplier() {
        Flight flight = new Flight(1, "BA123", "London", "Paris", 
                                   LocalDate.now().plusDays(2), 150, 100.00);
        
        // Booking 2 days before departure should have 2x multiplier
        double price2Days = flight.calculateDynamicPrice(LocalDate.now());
        assertTrue("Price should be higher for last-minute booking", price2Days > 100.00);
        
        // Booking 40 days before should have base price
        Flight earlyFlight = new Flight(2, "BA456", "London", "Rome", 
                                        LocalDate.now().plusDays(40), 150, 100.00);
        double earlyPrice = earlyFlight.calculateDynamicPrice(LocalDate.now());
        assertEquals("Early booking should have base price", 100.00, earlyPrice, 0.01);
    }
    
    /**
     * Test 12: Test customer authentication with correct password.
     * Verifies password validation works correctly.
     */
    @Test
    public void testCustomerPasswordValidation() {
        assertTrue("Should validate correct password", 
                   testCustomer.validatePassword("password123"));
        assertFalse("Should reject incorrect password", 
                    testCustomer.validatePassword("wrongpassword"));
    }
    
    /**
     * Test 13: Test customer role assignment and admin check.
     * Verifies role-based access control functionality.
     */
    @Test
    public void testCustomerRoleAndAdminCheck() {
        Customer regularCustomer = new Customer(1, "John", "111", "john@test.com", 
                                               "pass", Customer.UserRole.CUSTOMER);
        Customer adminCustomer = new Customer(2, "Admin", "222", "admin@test.com", 
                                             "pass", Customer.UserRole.ADMIN);
        
        assertFalse("Regular customer should not be admin", regularCustomer.isAdmin());
        assertTrue("Admin customer should be admin", adminCustomer.isAdmin());
        assertEquals("Regular customer role should be CUSTOMER", 
                     Customer.UserRole.CUSTOMER, regularCustomer.getRole());
        assertEquals("Admin customer role should be ADMIN", 
                     Customer.UserRole.ADMIN, adminCustomer.getRole());
    }
    
    /**
     * Test 14: Test adding flight to flight booking system.
     * Verifies flight can be added and retrieved from the system.
     * 
     * @throws FlightBookingSystemException if flight operations fail
     */
    @Test
    public void testAddFlightToSystem() throws FlightBookingSystemException {
        int initialSize = fbs.getFlights().size();
        fbs.addFlight(testFlight);
        
        assertEquals("Flight list size should increase by 1", 
                     initialSize + 1, fbs.getFlights().size());
        assertEquals("Should be able to retrieve added flight", 
                     testFlight, fbs.getFlightByID(1));
    }
    
    /**
     * Test 15: Test adding customer to flight booking system.
     * Verifies customer can be added and retrieved from the system.
     * 
     * @throws FlightBookingSystemException if customer operations fail
     */
    @Test
    public void testAddCustomerToSystem() throws FlightBookingSystemException {
        int initialSize = fbs.getCustomers().size();
        fbs.addCustomer(testCustomer);
        
        assertEquals("Customer list size should increase by 1", 
                     initialSize + 1, fbs.getCustomers().size());
        assertEquals("Should be able to retrieve added customer", 
                     testCustomer, fbs.getCustomerByID(1));
    }
    
    /**
     * Test 16: Test that flight has not departed when in future.
     * Verifies hasDeparted() method works correctly.
     */
    @Test
    public void testFlightHasNotDeparted() {
        Flight futureFlight = new Flight(1, "BA123", "London", "Paris", 
                                        LocalDate.now().plusDays(10), 150, 100.00);
        assertFalse("Future flight should not have departed", futureFlight.hasDeparted());
    }
    
    /**
     * Test 17: Test adding booking to customer.
     * Verifies customer's booking list is updated correctly.
     * 
     * @throws FlightBookingSystemException if booking operations fail
     */
    @Test
    public void testAddBookingToCustomer() throws FlightBookingSystemException {
        Booking booking = new Booking(testCustomer, testFlight, LocalDate.now(), 150.00);
        
        int initialBookings = testCustomer.getBookings().size();
        testCustomer.addBooking(booking);
        
        assertEquals("Bookings count should increase by 1", 
                     initialBookings + 1, testCustomer.getBookings().size());
        assertTrue("Customer should have the booking", 
                   testCustomer.getBookings().contains(booking));
    }
    
    /**
     * Test 18: Test that adding duplicate booking for same flight throws exception.
     * Ensures customer cannot book the same flight twice.
     * 
     * @throws FlightBookingSystemException expected exception for duplicate booking
     */
    @Test(expected = FlightBookingSystemException.class)
    public void testAddDuplicateBookingToCustomer() throws FlightBookingSystemException {
        Booking booking1 = new Booking(testCustomer, testFlight, LocalDate.now(), 150.00);
        Booking booking2 = new Booking(testCustomer, testFlight, LocalDate.now().plusDays(1), 160.00);
        
        testCustomer.addBooking(booking1);
        testCustomer.addBooking(booking2); // Should throw exception - same flight
    }
}
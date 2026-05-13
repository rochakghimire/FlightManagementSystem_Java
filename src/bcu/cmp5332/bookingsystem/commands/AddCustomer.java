package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.data.DataPersistenceManager;

public class AddCustomer implements Command {

    private final String name;
    private final String phone;
    private final String email;
    private final String password;

    public AddCustomer(String name, String phone, String email, String password) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
    }

    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        int maxId = 0;
        // Logic to find the highest existing ID
        for (Customer c : flightBookingSystem.getCustomers()) {
            if (c.getId() > maxId) {
                maxId = c.getId();
            }
        }
        
        // Create new customer with the CUSTOMER role by default
        Customer customer = new Customer(
            ++maxId, 
            name, 
            phone, 
            email, 
            password, 
            Customer.UserRole.CUSTOMER
        );
        
        flightBookingSystem.addCustomer(customer);
        
        // Persist the new data
        DataPersistenceManager.safeStore(flightBookingSystem);
        
        System.out.println("Customer #" + customer.getId() + " added [Role: " + customer.getRole() + "]");
    }
}
package bcu.cmp5332.bookingsystem.test;

import bcu.cmp5332.bookingsystem.model.Customer;

public class CustomerTest {
    
    public static void main(String[] args) {
        try {
            testCustomerCreation();
            testEmailProperty();
            System.out.println("All Customer tests passed!");
        } catch (Exception e) {
            System.out.println("Test failed: " + e.getMessage());
        }
    }
    
    private static void testCustomerCreation() {
        Customer customer = new Customer(1, "John Doe", "1234567890", "john@example.com");
        assert customer.getId() == 1;
        assert customer.getEmail().equals("john@example.com");
        System.out.println("✓ Customer creation test passed");
    }
    
    private static void testEmailProperty() {
        Customer customer = new Customer(1, "John Doe", "1234567890", "john@example.com");
        customer.setEmail("newemail@example.com");
        assert customer.getEmail().equals("newemail@example.com");
        System.out.println("✓ Email modification test passed");
    }
}
package bcu.cmp5332.bookingsystem.model;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.util.SystemDate;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Flight {
    
    private int id;
    private String flightNumber;
    private String origin;
    private String destination;
    private LocalDate departureDate;
    private int capacity;
    private double price;
    private boolean deleted = false;

    private final Set<Customer> passengers;

    public Flight(int id, String flightNumber, String origin, String destination, 
            LocalDate departureDate, int capacity, double price) {
    	this.id = id;
    	this.flightNumber = flightNumber;
    	this.origin = origin;
    	this.destination = destination;
    	this.departureDate = departureDate;
    	this.capacity = capacity;
    	this.price = price;
  
    	passengers = new HashSet<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }
    
    public String getOrigin() {
        return origin;
    }
    
    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public List<Customer> getPassengers() {
        return new ArrayList<>(passengers);
    }
    
    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    
    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
	
    public String getDetailsShort() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/YYYY");
        return "Flight #" + id + " - " + flightNumber + " - " + origin + " to " 
                + destination + " on " + departureDate.format(dtf);
    }
    
    /**
     * Checks if the flight has already departed.
     * Uses SystemDate for consistency.
     * @return true if flight has departed
     */
    public boolean hasDeparted() {
        return SystemDate.isPast(this.departureDate);
    }

    /**
     * Checks if the flight is full.
     * @return true if number of passengers equals capacity
     */
    public boolean isFull() {
        return passengers.size() >= capacity;
    }

    /**
     * Gets the number of available seats.
     * @return Number of seats still available
     */
    public int getAvailableSeats() {
        return capacity - passengers.size();
    }

    public String getDetailsLong() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/YYYY");
        StringBuilder sb = new StringBuilder();
        sb.append("Flight #").append(id).append("\n");
        sb.append("Flight No: ").append(flightNumber).append("\n");
        sb.append("Origin: ").append(origin).append("\n");
        sb.append("Destination: ").append(destination).append("\n");
        sb.append("Departure Date: ").append(departureDate.format(dtf)).append("\n");
        sb.append("---------------------------\n");
        sb.append("Passengers:\n");
        
        for (Customer passenger : passengers) {
            sb.append("* Id: ").append(passenger.getId())
              .append(" - ").append(passenger.getName())
              .append(" - ").append(passenger.getPhone()).append("\n");
        }
        
        sb.append(passengers.size()).append(" passenger(s)");
        return sb.toString();
    }
    
    public void addPassenger(Customer passenger) throws FlightBookingSystemException {
        if (passengers.contains(passenger)) {
            throw new FlightBookingSystemException("Passenger is already in the flight's passenger list.");
        }
        
        // Check if flight has departed
        if (hasDeparted()) {
            throw new FlightBookingSystemException("Cannot add passenger - flight has already departed.");
        }
        
        // Check capacity
        if (isFull()) {
            throw new FlightBookingSystemException("Cannot add passenger - flight is full.");
        }
        
        passengers.add(passenger);
    }
    
    public void removePassenger(Customer passenger) throws FlightBookingSystemException {
        if (!passengers.contains(passenger)) {
            throw new FlightBookingSystemException("Passenger is not in the flight's passenger list.");
        }
        passengers.remove(passenger);
    }
    
    /**
     * Calculate dynamic price based on:
     * 1. Days until departure (less time = higher price)
     * 2. Available seats (fewer seats = higher price)
     * 
     * @param bookingDate The date when the booking is being made
     * @return The calculated price for this booking
     */
    public double calculateDynamicPrice(LocalDate bookingDate) {
        double basePrice = this.price;
        
        // Calculate days until departure
        long daysUntilDeparture = java.time.temporal.ChronoUnit.DAYS.between(bookingDate, departureDate);
        
        // Price multiplier based on time until departure
        double timeMultiplier = 1.0;
        if (daysUntilDeparture < 0) {
            // Flight has already departed - shouldn't happen but just in case
            timeMultiplier = 1.0;
        } else if (daysUntilDeparture <= 3) {
            timeMultiplier = 2.0; // 100% increase for last 3 days
        } else if (daysUntilDeparture <= 7) {
            timeMultiplier = 1.5; // 50% increase for last week
        } else if (daysUntilDeparture <= 14) {
            timeMultiplier = 1.3; // 30% increase for last 2 weeks
        } else if (daysUntilDeparture <= 30) {
            timeMultiplier = 1.1; // 10% increase for last month
        } else {
            timeMultiplier = 1.0; // Base price for early bookings
        }
        
        // Price multiplier based on capacity
        double capacityRatio = (double) passengers.size() / capacity;
        double capacityMultiplier = 1.0;
        if (capacityRatio >= 0.9) {
            capacityMultiplier = 1.5; // 50% increase when 90%+ full
        } else if (capacityRatio >= 0.75) {
            capacityMultiplier = 1.3; // 30% increase when 75%+ full
        } else if (capacityRatio >= 0.5) {
            capacityMultiplier = 1.15; // 15% increase when 50%+ full
        } else {
            capacityMultiplier = 1.0; // Base price when less than 50% full
        }
        
        // Calculate final price
        double finalPrice = basePrice * timeMultiplier * capacityMultiplier;
        
        return Math.round(finalPrice * 100.0) / 100.0; // Round to 2 decimal places
    }
}

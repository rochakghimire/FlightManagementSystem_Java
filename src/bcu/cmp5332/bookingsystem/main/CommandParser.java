package bcu.cmp5332.bookingsystem.main;

import bcu.cmp5332.bookingsystem.commands.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class CommandParser {
    
    public static Command parse(String line) throws IOException, FlightBookingSystemException {
        try {
            // Trim and split the input to handle accidental spaces
            String[] parts = line.trim().split(" ", 4);
            String cmd = parts[0].toLowerCase(); // Convert to lowercase for easier matching

            // --- 1. INTERACTIVE COMMANDS (Multiple Steps) ---
            if (cmd.equals("addflight")) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("Flight Number: ");
                String flightNumber = reader.readLine();
                System.out.print("Origin: ");
                String origin = reader.readLine();
                System.out.print("Destination: ");
                String destination = reader.readLine();

                LocalDate departureDate = parseDateWithAttempts(reader);

                System.out.print("Capacity: ");
                int capacity = Integer.parseInt(reader.readLine());
                System.out.print("Price: ");
                double price = Double.parseDouble(reader.readLine());

                return new AddFlight(flightNumber, origin, destination, departureDate, capacity, price);

            } else if (cmd.equals("addcustomer")) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("Customer Name: ");
                String name = reader.readLine();
                System.out.print("Phone Number: ");
                String phone = reader.readLine();
                System.out.print("Email: ");
                String email = reader.readLine();
                
                // NEW: Requirement for Password in CLI
                System.out.print("Password: ");
                String password = reader.readLine();
                
                return new AddCustomer(name, phone, email, password);

            // --- 2. SINGLE WORD COMMANDS ---
            } else if (cmd.equals("loadgui")) {
                return new LoadGUI();
            } else if (cmd.equals("listflights")) {
                return new ListFlights();
            } else if (cmd.equals("listcustomers")) {
                return new ListCustomers();
            } else if (cmd.equals("help")) {
                return new Help();

            // --- 3. COMMANDS WITH ARGUMENTS (ID BASED) ---
            } else if (parts.length == 2) {
                int id = Integer.parseInt(parts[1]);

                if (cmd.equals("showflight")) {
                    return new ShowFlight(id);
                } else if (cmd.equals("showcustomer")) {
                    return new ShowCustomer(id);
                } else if (cmd.equals("deleteflight")) {
                    return new DeleteFlight(id);
                } else if (cmd.equals("deletecustomer")) {
                    return new DeleteCustomer(id);
                }
                
            } else if (parts.length == 3) {
                int customerId = Integer.parseInt(parts[1]);
                int flightId = Integer.parseInt(parts[2]);

                if (cmd.equals("addbooking")) {
                    return new AddBooking(customerId, flightId);
                } else if (cmd.equals("cancelbooking")) {
                    return new CancelBooking(customerId, flightId);
                }

            } else if (parts.length == 4) {
                int customerId = Integer.parseInt(parts[1]);
                int oldFlightId = Integer.parseInt(parts[2]);
                int newFlightId = Integer.parseInt(parts[3]);
                
                if (cmd.equals("updatebooking")) {
                    return new UpdateBooking(customerId, oldFlightId, newFlightId);
                }
            }
        } catch (NumberFormatException ex) {
            throw new FlightBookingSystemException("Invalid numeric ID provided. Please enter a valid integer.");
        }

        throw new FlightBookingSystemException("Invalid command. Type 'help' for a list of commands.");
    }
    
    private static LocalDate parseDateWithAttempts(BufferedReader br, int attempts) throws IOException, FlightBookingSystemException {
        if (attempts < 1) {
            throw new IllegalArgumentException("Number of attempts should be higher than 0");
        }
        while (attempts > 0) {
            attempts--;
            System.out.print("Departure Date (\"YYYY-MM-DD\" format): ");
            try {
                return LocalDate.parse(br.readLine());
            } catch (DateTimeParseException dtpe) {
                System.out.println("Date must be in YYYY-MM-DD format. " + attempts + " attempts remaining...");
            }
        }
        throw new FlightBookingSystemException("Incorrect departure date provided. Cannot create flight.");
    }
    
    private static LocalDate parseDateWithAttempts(BufferedReader br) throws IOException, FlightBookingSystemException {
        return parseDateWithAttempts(br, 3);
    }
}
package bcu.cmp5332.bookingsystem.util;

import java.time.LocalDate;

/**
 * Utility class for managing system date.
 * Provides a single source of truth for current date across the application.
 * This allows for easier testing and future extensions (e.g., date simulation).
 */
public class SystemDate {
    
    /**
     * Gets the current system date.
     * @return Current date as LocalDate
     */
    public static LocalDate getCurrentDate() {
        return LocalDate.now();
    }
    
    /**
     * Checks if a given date is in the past.
     * @param date Date to check
     * @return true if date is before current date
     */
    public static boolean isPast(LocalDate date) {
        return date.isBefore(getCurrentDate());
    }
    
    /**
     * Checks if a given date is in the future.
     * @param date Date to check
     * @return true if date is after current date
     */
    public static boolean isFuture(LocalDate date) {
        return date.isAfter(getCurrentDate());
    }
    
    /**
     * Checks if a given date is today.
     * @param date Date to check
     * @return true if date equals current date
     */
    public static boolean isToday(LocalDate date) {
        return date.isEqual(getCurrentDate());
    }

	public static long daysUntil(LocalDate departureDate) {
		// TODO Auto-generated method stub
		return 0;
	}
}
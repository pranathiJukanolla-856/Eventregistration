package eventregistration;
        
        import java.sql.*;
		import java.time.LocalDate;
		import java.util.Scanner;

		public class Eventregistrationsystem {

		    private static final Scanner sc = new Scanner(System.in);

		    public static void main(String[] args) {
		   

		        while (true) {
		            printMenu();
		            int choice = readInt("Enter your choice: ");

		            try {
		            	switch (choice) {
		                case 1:
		                    addEvent();
		                    break;

		                case 2:
		                    viewAllEvents();
		                    break;

		                case 3:
		                    searchEvent();
		                    break;

		                case 4:
		                    addParticipant();
		                    break;

		                case 5:
		                    registerParticipantForEvent();
		                    break;

		                case 6:
		                    viewAllParticipants();
		                    break;

		                case 7:
		                    viewEventParticipants();
		                    break;

		                case 8:
		                    updateEvent();
		                    break;

		                case 9:
		                    cancelEvent();
		                    break;

		                case 10:
		                    cancelRegistration();
		                    break;

		                case 11:
		                    countParticipants();
		                    break;

		                case 12:
		                    System.out.println("Thank you for using Event Registration Management System.");
		                    sc.close();
		                    return;

		                default:
		                    System.out.println("Invalid choice.");
		            }
		            } catch (SQLException e) {
		                System.out.println("Database error: " + e.getMessage());
		            } catch (Exception e) {
		                System.out.println("Error: " + e.getMessage());
		            }

		            System.out.println();
		        }
		    }

		    private static void printMenu() {
		        System.out.println("\n==============================================");
		        System.out.println("   EVENT REGISTRATION MANAGEMENT SYSTEM");
		        System.out.println("==============================================");
		        System.out.println("1. Add New Event");
		        System.out.println("2. View All Events");
		        System.out.println("3. Search Event by Name");
		        System.out.println("4. Register/Add Participant");
		        System.out.println("5. Register Participant for an Event");
		        System.out.println("6. View All Participants");
		        System.out.println("7. View Participants for an Event");
		        System.out.println("8. Update Event");
		        System.out.println("9. Cancel Event");
		        System.out.println("10. Cancel Participant Registration");
		        System.out.println("11. View Total Number of Participants");
		        System.out.println("12. Exit");
		        System.out.println("==============================================");
		    }

		    private static int readInt(String message) {
		        while (true) {
		            try {
		                System.out.print(message);
		                return Integer.parseInt(sc.nextLine().trim());
		            } catch (NumberFormatException e) {
		                System.out.println("Please enter a valid number.");
		            }
		        }
		    }

		    private static void addEvent() throws SQLException {
		        System.out.println("\n--- Add New Event ---");
		        String name = readNonEmpty("Event name: ");
		        String date = readDate("Event date (YYYY-MM-DD): ");
		        String location = readNonEmpty("Location: ");
		        int capacity = readPositiveInt("Maximum capacity: ");

		        String sql = "INSERT INTO events " +
		                "(event_name, event_date, location, maximum_capacity, status) " +
		                "VALUES (?, ?, ?, ?, 'Open')";

		        try (Connection con = DBConnection.getConnection();
		             PreparedStatement ps = con.prepareStatement(sql)) {

		            ps.setString(1, name);
		            ps.setDate(2, Date.valueOf(date));
		            ps.setString(3, location);
		            ps.setInt(4, capacity);

		            int rows = ps.executeUpdate();
		            System.out.println(rows > 0 ? "Event added successfully." : "Event was not added.");
		        }
		    }

		    private static void viewAllEvents() throws SQLException {
		        System.out.println("\n--- All Events ---");

		        String sql = "SELECT e.event_id, e.event_name, e.event_date, e.location, " +
		                "e.maximum_capacity, e.status, " +
		                "COUNT(CASE WHEN r.status='Registered' THEN 1 END) AS registered " +
		                "FROM events e LEFT JOIN registrations r ON e.event_id=r.event_id " +
		                "GROUP BY e.event_id, e.event_name, e.event_date, e.location, " +
		                "e.maximum_capacity, e.status ORDER BY e.event_date";

		        try (Connection con = DBConnection.getConnection();
		             PreparedStatement ps = con.prepareStatement(sql);
		             ResultSet rs = ps.executeQuery()) {

		            boolean found = false;
		            while (rs.next()) {
		                found = true;
		                System.out.printf(
		                        "ID: %d | %s | Date: %s | Location: %s | Capacity: %d | Registered: %d | Status: %s%n",
		                        rs.getInt("event_id"),
		                        rs.getString("event_name"),
		                        rs.getDate("event_date"),
		                        rs.getString("location"),
		                        rs.getInt("maximum_capacity"),
		                        rs.getInt("registered"),
		                        rs.getString("status")
		                );
		            }

		            if (!found) {
		                System.out.println("No events found.");
		            }
		        }
		    }

		    private static void searchEvent() throws SQLException {
		        System.out.println("\n--- Search Event ---");
		        String name = readNonEmpty("Enter event name: ");

		        String sql = "SELECT * FROM events WHERE event_name LIKE ? ORDER BY event_date";

		        try (Connection con = DBConnection.getConnection();
		             PreparedStatement ps = con.prepareStatement(sql)) {

		            ps.setString(1, "%" + name + "%");

		            try (ResultSet rs = ps.executeQuery()) {
		                boolean found = false;

		                while (rs.next()) {
		                    found = true;
		                    System.out.printf(
		                            "ID: %d | Name: %s | Date: %s | Location: %s | Capacity: %d | Status: %s%n",
		                            rs.getInt("event_id"),
		                            rs.getString("event_name"),
		                            rs.getDate("event_date"),
		                            rs.getString("location"),
		                            rs.getInt("maximum_capacity"),
		                            rs.getString("status")
		                    );
		                }

		                if (!found) {
		                    System.out.println("No matching event found.");
		                }
		            }
		        }
		    }

		    private static void addParticipant() throws SQLException {
		        System.out.println("\n--- Add Participant ---");
		        String name = readNonEmpty("Name: ");
		        String email = readNonEmpty("Email: ");
		        String phone = readNonEmpty("Phone: ");

		        String sql = "INSERT INTO participants (name, email, phone) VALUES (?, ?, ?)";

		        try (Connection con = DBConnection.getConnection();
		             PreparedStatement ps = con.prepareStatement(sql)) {

		            ps.setString(1, name);
		            ps.setString(2, email);
		            ps.setString(3, phone);

		            try {
		                int rows = ps.executeUpdate();
		                System.out.println(rows > 0 ? "Participant added successfully." : "Participant was not added.");
		            } catch (SQLIntegrityConstraintViolationException e) {
		                System.out.println("Email already exists. Use a different email.");
		            }
		        }
		    }

		    private static void registerParticipantForEvent() throws SQLException {
		        System.out.println("\n--- Register Participant for Event ---");

		        int eventId = readPositiveInt("Event ID: ");
		        int participantId = readPositiveInt("Participant ID: ");

		        String eventSql = "SELECT maximum_capacity, status FROM events WHERE event_id=?";
		        String countSql = "SELECT COUNT(*) FROM registrations " +
		                "WHERE event_id=? AND status='Registered'";
		        String insertSql = "INSERT INTO registrations " +
		                "(event_id, participant_id, registration_date, status) VALUES (?, ?, ?, 'Registered')";
		        String updateStatusSql = "UPDATE events SET status='Full' WHERE event_id=?";

		        try (Connection con = DBConnection.getConnection()) {
		            con.setAutoCommit(false);

		            try {
		                int capacity;
		                String eventStatus;

		                try (PreparedStatement ps = con.prepareStatement(eventSql)) {
		                    ps.setInt(1, eventId);

		                    try (ResultSet rs = ps.executeQuery()) {
		                        if (!rs.next()) {
		                            System.out.println("Event not found.");
		                            con.rollback();
		                            return;
		                        }

		                        capacity = rs.getInt("maximum_capacity");
		                        eventStatus = rs.getString("status");
		                    }
		                }

		                if ("Cancelled".equalsIgnoreCase(eventStatus) ||
		                        "Completed".equalsIgnoreCase(eventStatus)) {
		                    System.out.println("Registration is not allowed for this event.");
		                    con.rollback();
		                    return;
		                }

		                int registeredCount;

		                try (PreparedStatement ps = con.prepareStatement(countSql)) {
		                    ps.setInt(1, eventId);

		                    try (ResultSet rs = ps.executeQuery()) {
		                        rs.next();
		                        registeredCount = rs.getInt(1);
		                    }
		                }

		                if (registeredCount >= capacity) {
		                    System.out.println("Event is full.");
		                    con.rollback();
		                    return;
		                }

		                try (PreparedStatement ps = con.prepareStatement(insertSql)) {
		                    ps.setInt(1, eventId);
		                    ps.setInt(2, participantId);
		                    ps.setDate(3, Date.valueOf(LocalDate.now()));

		                    ps.executeUpdate();
		                } catch (SQLIntegrityConstraintViolationException e) {
		                    System.out.println("Invalid participant ID or participant is already registered.");
		                    con.rollback();
		                    return;
		                }

		                if (registeredCount + 1 >= capacity) {
		                    try (PreparedStatement ps = con.prepareStatement(updateStatusSql)) {
		                        ps.setInt(1, eventId);
		                        ps.executeUpdate();
		                    }
		                }

		                con.commit();
		                System.out.println("Participant registered successfully.");

		            } catch (SQLException e) {
		                con.rollback();
		                throw e;
		            } finally {
		                con.setAutoCommit(true);
		            }
		        }
		    }

		    private static void viewAllParticipants() throws SQLException {
		        System.out.println("\n--- All Participants ---");

		        String sql = "SELECT * FROM participants ORDER BY participant_id";

		        try (Connection con = DBConnection.getConnection();
		             PreparedStatement ps = con.prepareStatement(sql);
		             ResultSet rs = ps.executeQuery()) {

		            boolean found = false;

		            while (rs.next()) {
		                found = true;
		                System.out.printf(
		                        "ID: %d | Name: %s | Email: %s | Phone: %s%n",
		                        rs.getInt("participant_id"),
		                        rs.getString("name"),
		                        rs.getString("email"),
		                        rs.getString("phone")
		                );
		            }

		            if (!found) {
		                System.out.println("No participants found.");
		            }
		        }
		    }

		    private static void viewEventParticipants() throws SQLException {
		        System.out.println("\n--- Participants for Event ---");
		        int eventId = readPositiveInt("Event ID: ");

		        String sql = "SELECT p.participant_id, p.name, p.email, p.phone, " +
		                "r.registration_id, r.registration_date, r.status " +
		                "FROM participants p " +
		                "JOIN registrations r ON p.participant_id=r.participant_id " +
		                "WHERE r.event_id=? ORDER BY p.name";

		        try (Connection con = DBConnection.getConnection();
		             PreparedStatement ps = con.prepareStatement(sql)) {

		            ps.setInt(1, eventId);

		            try (ResultSet rs = ps.executeQuery()) {
		                boolean found = false;

		                while (rs.next()) {
		                    found = true;
		                    System.out.printf(
		                            "Registration ID: %d | Participant ID: %d | Name: %s | Email: %s | Phone: %s | Date: %s | Status: %s%n",
		                            rs.getInt("registration_id"),
		                            rs.getInt("participant_id"),
		                            rs.getString("name"),
		                            rs.getString("email"),
		                            rs.getString("phone"),
		                            rs.getDate("registration_date"),
		                            rs.getString("status")
		                    );
		                }

		                if (!found) {
		                    System.out.println("No registrations found for this event.");
		                }
		            }
		        }
		    }

		    private static void updateEvent() throws SQLException {
		        System.out.println("\n--- Update Event ---");

		        int eventId = readPositiveInt("Event ID to update: ");
		        String name = readNonEmpty("New event name: ");
		        String date = readDate("New event date (YYYY-MM-DD): ");
		        String location = readNonEmpty("New location: ");
		        int capacity = readPositiveInt("New maximum capacity: ");

		        String sql = "UPDATE events SET event_name=?, event_date=?, " +
		                "location=?, maximum_capacity=? WHERE event_id=?";

		        try (Connection con = DBConnection.getConnection();
		             PreparedStatement ps = con.prepareStatement(sql)) {

		            ps.setString(1, name);
		            ps.setDate(2, Date.valueOf(date));
		            ps.setString(3, location);
		            ps.setInt(4, capacity);
		            ps.setInt(5, eventId);

		            int rows = ps.executeUpdate();
		            System.out.println(rows > 0 ? "Event updated successfully." : "Event ID not found.");
		        }
		    }

		    private static void cancelEvent() throws SQLException {
		        System.out.println("\n--- Cancel Event ---");
		        int eventId = readPositiveInt("Event ID: ");

		        String sql = "UPDATE events SET status='Cancelled' WHERE event_id=?";

		        try (Connection con = DBConnection.getConnection();
		             PreparedStatement ps = con.prepareStatement(sql)) {

		            ps.setInt(1, eventId);

		            int rows = ps.executeUpdate();
		            System.out.println(rows > 0 ? "Event cancelled successfully." : "Event ID not found.");
		        }
		    }

		    private static void cancelRegistration() throws SQLException {
		        System.out.println("\n--- Cancel Participant Registration ---");
		        int registrationId = readPositiveInt("Registration ID: ");

		        String findSql = "SELECT event_id, status FROM registrations WHERE registration_id=?";
		        String updateSql = "UPDATE registrations SET status='Cancelled' WHERE registration_id=?";
		        String reopenSql = "UPDATE events SET status='Open' " +
		                "WHERE event_id=? AND status='Full'";

		        try (Connection con = DBConnection.getConnection()) {
		            int eventId;
		            String currentStatus;

		            try (PreparedStatement ps = con.prepareStatement(findSql)) {
		                ps.setInt(1, registrationId);

		                try (ResultSet rs = ps.executeQuery()) {
		                    if (!rs.next()) {
		                        System.out.println("Registration ID not found.");
		                        return;
		                    }

		                    eventId = rs.getInt("event_id");
		                    currentStatus = rs.getString("status");
		                }
		            }

		            if ("Cancelled".equalsIgnoreCase(currentStatus)) {
		                System.out.println("Registration is already cancelled.");
		                return;
		            }

		            try (PreparedStatement ps = con.prepareStatement(updateSql)) {
		                ps.setInt(1, registrationId);
		                ps.executeUpdate();
		            }

		            try (PreparedStatement ps = con.prepareStatement(reopenSql)) {
		                ps.setInt(1, eventId);
		                ps.executeUpdate();
		            }

		            System.out.println("Registration cancelled successfully.");
		        }
		    }

		    private static void countParticipants() throws SQLException {
		        System.out.println("\n--- Participant Count ---");
		        int eventId = readPositiveInt("Event ID: ");

		        String sql = "SELECT e.event_name, e.maximum_capacity, " +
		                "COUNT(CASE WHEN r.status='Registered' THEN 1 END) AS total " +
		                "FROM events e LEFT JOIN registrations r ON e.event_id=r.event_id " +
		                "WHERE e.event_id=? " +
		                "GROUP BY e.event_id, e.event_name, e.maximum_capacity";

		        try (Connection con = DBConnection.getConnection();
		             PreparedStatement ps = con.prepareStatement(sql)) {

		            ps.setInt(1, eventId);

		            try (ResultSet rs = ps.executeQuery()) {
		                if (rs.next()) {
		                    System.out.println("Event: " + rs.getString("event_name"));
		                    System.out.println("Registered Participants: " + rs.getInt("total"));
		                    System.out.println("Maximum Capacity: " + rs.getInt("maximum_capacity"));
		                } else {
		                    System.out.println("Event ID not found.");
		                }
		            }
		        }
		    }

		    private static String readNonEmpty(String message) {
		        while (true) {
		            System.out.print(message);
		            String value = sc.nextLine().trim();

		            if (!value.isEmpty()) {
		                return value;
		            }

		            System.out.println("Value cannot be empty.");
		        }
		    }

		    private static int readPositiveInt(String message) {
		        while (true) {
		            int value = readInt(message);

		            if (value > 0) {
		                return value;
		            }

		            System.out.println("Enter a value greater than zero.");
		        }
		    }

		    private static String readDate(String message) {
		        while (true) {
		            String value = readNonEmpty(message);

		            try {
		                Date.valueOf(value);
		                return value;
		            } catch (IllegalArgumentException e) {
		                System.out.println("Invalid date. Use YYYY-MM-DD.");
		            }
		        }
		    }
		
          
	}



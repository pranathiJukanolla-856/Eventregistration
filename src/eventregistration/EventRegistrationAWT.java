package eventregistration;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class EventRegistrationAWT extends Frame {
	private static final long serialVersionUID = 1L;
	// ================= DATABASE =================
    static final String URL =
            "jdbc:mysql://localhost:3306/event_registration_db";
    static final String USER = "root";
    static final String PASSWORD = "root";

    // ================= TEXT FIELDS =================
    TextField eventName = new TextField();
    TextField eventDate = new TextField();
    TextField location = new TextField();
    TextField capacity = new TextField();

    TextField participantName = new TextField();
    TextField email = new TextField();
    TextField phone = new TextField();

    TextField eventId = new TextField();
    TextField participantId = new TextField();
    TextField registrationId = new TextField();

    TextArea output = new TextArea();

    // ================= CONSTRUCTOR =================
    public EventRegistrationAWT() {

        setTitle("Event Registration Management System");
        setSize(1100, 700);
        setLayout(new BorderLayout());

        // ---------- TITLE ----------
        Label title = new Label(
                "EVENT REGISTRATION MANAGEMENT SYSTEM",
                Label.CENTER);

        title.setFont(new Font("Arial", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        // ---------- FORM ----------
        Panel formPanel = new Panel(new GridLayout(0, 2, 8, 8));

        formPanel.add(new Label("EVENT DETAILS"));
        formPanel.add(new Label(""));

        formPanel.add(new Label("Event Name:"));
        formPanel.add(eventName);

        formPanel.add(new Label("Event Date (YYYY-MM-DD):"));
        formPanel.add(eventDate);

        formPanel.add(new Label("Location:"));
        formPanel.add(location);

        formPanel.add(new Label("Maximum Capacity:"));
        formPanel.add(capacity);

        formPanel.add(new Label("PARTICIPANT DETAILS"));
        formPanel.add(new Label(""));

        formPanel.add(new Label("Participant Name:"));
        formPanel.add(participantName);

        formPanel.add(new Label("Email:"));
        formPanel.add(email);

        formPanel.add(new Label("Phone:"));
        formPanel.add(phone);

        formPanel.add(new Label("REGISTRATION DETAILS"));
        formPanel.add(new Label(""));

        formPanel.add(new Label("Event ID:"));
        formPanel.add(eventId);

        formPanel.add(new Label("Participant ID:"));
        formPanel.add(participantId);

        formPanel.add(new Label("Registration ID:"));
        formPanel.add(registrationId);

        add(formPanel, BorderLayout.WEST);

        // ---------- OUTPUT ----------
        output.setFont(new Font("Monospaced", Font.PLAIN, 14));
        add(output, BorderLayout.CENTER);

        // ---------- BUTTONS ----------
        Panel buttonPanel = new Panel(new GridLayout(7, 2, 5, 5));

        addButton(buttonPanel, "Add Event",
                e -> addEvent());

        addButton(buttonPanel, "View All Events",
                e -> viewEvents());

        addButton(buttonPanel, "Search Event",
                e -> searchEvent());

        addButton(buttonPanel, "Add Participant",
                e -> addParticipant());

        addButton(buttonPanel, "View Participants",
                e -> viewParticipants());

        addButton(buttonPanel, "Register Participant",
                e -> registerParticipant());

        addButton(buttonPanel, "View Event Participants",
                e -> viewEventParticipants());

        addButton(buttonPanel, "Update Event",
                e -> updateEvent());

        addButton(buttonPanel, "Cancel Event",
                e -> cancelEvent());

        addButton(buttonPanel, "Cancel Registration",
                e -> cancelRegistration());

        addButton(buttonPanel, "Count Participants",
                e -> countParticipants());

        addButton(buttonPanel, "Clear Fields",
                e -> clearFields());

        addButton(buttonPanel, "Exit",
                e -> System.exit(0));

        add(buttonPanel, BorderLayout.SOUTH);

        // ---------- WINDOW CLOSE ----------
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        setVisible(true);
    }

    // ================= BUTTON METHOD =================

    private void addButton(Panel panel, String text,
                            ActionListener action) {

        Button button = new Button(text);
        button.addActionListener(action);
        panel.add(button);
    }

    // ================= DATABASE CONNECTION =================

    static Connection getConnection() throws SQLException {

        return DriverManager.getConnection(
                URL, USER, PASSWORD);
    }

    // ================= ADD EVENT =================

    void addEvent() {

        String sql =
                "INSERT INTO events " +
                "(event_name,event_date,location,maximum_capacity,status) " +
                "VALUES (?,?,?,?,?)";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, eventName.getText());
            ps.setDate(2,
                    Date.valueOf(eventDate.getText()));
            ps.setString(3, location.getText());
            ps.setInt(4,
                    Integer.parseInt(capacity.getText()));
            ps.setString(5, "Active");

            ps.executeUpdate();

            output.setText(
                    "EVENT ADDED SUCCESSFULLY!\n\n" +
                    "Event Name : " + eventName.getText());

        } catch (Exception e) {
            showError(e);
        }
    }

    // ================= VIEW EVENTS =================

    void viewEvents() {

        String sql =
                "SELECT * FROM events";

        try (Connection con = getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            output.setText(
                    "================ ALL EVENTS ================\n\n");

            while (rs.next()) {

                output.append(
                        "Event ID       : " +
                        rs.getInt("event_id") + "\n");

                output.append(
                        "Event Name     : " +
                        rs.getString("event_name") + "\n");

                output.append(
                        "Date           : " +
                        rs.getDate("event_date") + "\n");

                output.append(
                        "Location       : " +
                        rs.getString("location") + "\n");

                output.append(
                        "Capacity       : " +
                        rs.getInt("maximum_capacity") + "\n");

                output.append(
                        "Status         : " +
                        rs.getString("status") + "\n");

                output.append(
                        "---------------------------------------------\n");
            }

        } catch (Exception e) {
            showError(e);
        }
    }

    // ================= SEARCH EVENT =================

    void searchEvent() {

        String sql =
                "SELECT * FROM events WHERE event_name LIKE ?";

        try (Connection con = getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setString(1,
                    "%" + eventName.getText() + "%");

            ResultSet rs = ps.executeQuery();

            output.setText(
                    "=============== SEARCH RESULT ===============\n\n");

            boolean found = false;

            while (rs.next()) {

                found = true;

                output.append(
                        "Event ID   : " +
                        rs.getInt("event_id") + "\n");

                output.append(
                        "Name       : " +
                        rs.getString("event_name") + "\n");

                output.append(
                        "Date       : " +
                        rs.getDate("event_date") + "\n");

                output.append(
                        "Location   : " +
                        rs.getString("location") + "\n");

                output.append(
                        "Capacity   : " +
                        rs.getInt("maximum_capacity") + "\n\n");
            }

            if (!found) {
                output.append("No event found.");
            }

        } catch (Exception e) {
            showError(e);
        }
    }

    // ================= ADD PARTICIPANT =================

    void addParticipant() {

        String sql =
                "INSERT INTO participants(name,email,phone) " +
                "VALUES (?,?,?)";

        try (Connection con = getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setString(1,
                    participantName.getText());

            ps.setString(2,
                    email.getText());

            ps.setString(3,
                    phone.getText());

            ps.executeUpdate();

            output.setText(
                    "PARTICIPANT ADDED SUCCESSFULLY!\n\n" +
                    "Name : " +
                    participantName.getText());

        } catch (Exception e) {
            showError(e);
        }
    }

    // ================= VIEW PARTICIPANTS =================

    void viewParticipants() {

        String sql =
                "SELECT * FROM participants";

        try (Connection con = getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            output.setText(
                    "============== ALL PARTICIPANTS ==============\n\n");

            while (rs.next()) {

                output.append(
                        "Participant ID : " +
                        rs.getInt("participant_id") + "\n");

                output.append(
                        "Name           : " +
                        rs.getString("name") + "\n");

                output.append(
                        "Email          : " +
                        rs.getString("email") + "\n");

                output.append(
                        "Phone          : " +
                        rs.getString("phone") + "\n");

                output.append(
                        "---------------------------------------------\n");
            }

        } catch (Exception e) {
            showError(e);
        }
    }

    // ================= REGISTER PARTICIPANT =================

    void registerParticipant() {

        String sql =
                "INSERT INTO registrations " +
                "(event_id,participant_id,registration_date) " +
                "VALUES (?,?,?)";

        try (Connection con = getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setInt(1,
                    Integer.parseInt(eventId.getText()));

            ps.setInt(2,
                    Integer.parseInt(
                            participantId.getText()));

            ps.setDate(3,
                    new Date(System.currentTimeMillis()));

            ps.executeUpdate();

            output.setText(
                    "PARTICIPANT REGISTERED SUCCESSFULLY!\n\n" +
                    "Event ID       : " +
                    eventId.getText() + "\n" +
                    "Participant ID : " +
                    participantId.getText());

        } catch (Exception e) {
            showError(e);
        }
    }

    // ================= VIEW EVENT PARTICIPANTS =================

    void viewEventParticipants() {

        String sql =
                "SELECT p.participant_id, p.name, " +
                "p.email, p.phone " +
                "FROM participants p " +
                "JOIN registrations r " +
                "ON p.participant_id = r.participant_id " +
                "WHERE r.event_id = ?";

        try (Connection con = getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setInt(1,
                    Integer.parseInt(eventId.getText()));

            ResultSet rs = ps.executeQuery();

            output.setText(
                    "=========== EVENT PARTICIPANTS ===========\n\n");

            boolean found = false;

            while (rs.next()) {

                found = true;

                output.append(
                        "Participant ID : " +
                        rs.getInt("participant_id") + "\n");

                output.append(
                        "Name           : " +
                        rs.getString("name") + "\n");

                output.append(
                        "Email          : " +
                        rs.getString("email") + "\n");

                output.append(
                        "Phone          : " +
                        rs.getString("phone") + "\n");

                output.append(
                        "------------------------------------------\n");
            }

            if (!found) {
                output.append("No participants found.");
            }

        } catch (Exception e) {
            showError(e);
        }
    }

    // ================= UPDATE EVENT =================

    void updateEvent() {

        String sql =
                "UPDATE events SET event_name=?, " +
                "event_date=?, location=?, " +
                "maximum_capacity=? " +
                "WHERE event_id=?";

        try (Connection con = getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setString(1,
                    eventName.getText());

            ps.setDate(2,
                    Date.valueOf(eventDate.getText()));

            ps.setString(3,
                    location.getText());

            ps.setInt(4,
                    Integer.parseInt(capacity.getText()));

            ps.setInt(5,
                    Integer.parseInt(eventId.getText()));

            int rows = ps.executeUpdate();

            if (rows > 0)
                output.setText(
                        "EVENT UPDATED SUCCESSFULLY!");
            else
                output.setText(
                        "Event ID not found.");

        } catch (Exception e) {
            showError(e);
        }
    }

    // ================= CANCEL EVENT =================

    void cancelEvent() {

        String sql =
                "UPDATE events SET status='Cancelled' " +
                "WHERE event_id=?";

        try (Connection con = getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setInt(1,
                    Integer.parseInt(eventId.getText()));

            int rows = ps.executeUpdate();

            if (rows > 0)
                output.setText(
                        "EVENT CANCELLED SUCCESSFULLY!");
            else
                output.setText(
                        "Event ID not found.");

        } catch (Exception e) {
            showError(e);
        }
    }

    // ================= CANCEL REGISTRATION =================

    void cancelRegistration() {

        String sql =
                "DELETE FROM registrations " +
                "WHERE registration_id=?";

        try (Connection con = getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setInt(1,
                    Integer.parseInt(
                            registrationId.getText()));

            int rows = ps.executeUpdate();

            if (rows > 0)
                output.setText(
                        "REGISTRATION CANCELLED SUCCESSFULLY!");
            else
                output.setText(
                        "Registration ID not found.");

        } catch (Exception e) {
            showError(e);
        }
    }

    // ================= COUNT =================

    void countParticipants() {

        String sql =
                "SELECT COUNT(*) AS total " +
                "FROM registrations";

        try (Connection con = getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {

                output.setText(
                        "====================================\n" +
                        " TOTAL REGISTERED PARTICIPANTS\n" +
                        "====================================\n\n" +
                        "Total : " +
                        rs.getInt("total"));
            }

        } catch (Exception e) {
            showError(e);
        }
    }

    // ================= CLEAR =================

    void clearFields() {

        eventName.setText("");
        eventDate.setText("");
        location.setText("");
        capacity.setText("");

        participantName.setText("");
        email.setText("");
        phone.setText("");

        eventId.setText("");
        participantId.setText("");
        registrationId.setText("");

        output.setText("");
    }

    // ================= ERROR =================

    void showError(Exception e) {

        output.setText(
                "ERROR:\n" +
                e.getMessage());
    }

    // ================= MAIN =================

    public static void main(String[] args) {

        new EventRegistrationAWT();
    }
}
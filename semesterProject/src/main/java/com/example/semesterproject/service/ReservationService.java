package com.example.semesterproject.service;

import com.example.semesterproject.model.Reservation;
import com.example.semesterproject.model.dBook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Service
public class ReservationService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<dBook> getAllBooks() {
        String sql = "SELECT b.ISBN, b.title, b.author, g.genre_name, bc.available_count " +
                "FROM books b " +
                "JOIN genre g ON b.genre_name = g.genre_name " +
                "JOIN bookcount bc ON b.ISBN = bc.ISBN";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            dBook book = new dBook();
            book.setISBN(rs.getString("ISBN"));
            book.setTitle(rs.getString("title"));
            book.setAuthor(rs.getString("author"));
            book.setGenre_name(rs.getString("genre_name"));
            book.setAvailable_count(rs.getInt("available_count"));
            return book;
        });
    }

    public boolean reserveBook(int userId, String isbn) {
        // Check if the book is already reserved by the user
        String checkExistingReservationSql = "SELECT COUNT(*) FROM reservation WHERE user_id = ? AND ISBN = ? AND reservation_status = 'PENDING'";
        Integer existingReservationCount = jdbcTemplate.queryForObject(checkExistingReservationSql, new Object[]{userId, isbn}, Integer.class);

        if (existingReservationCount != null && existingReservationCount > 0) {
            return false;
        }

        // Check if the book is available
        String checkAvailabilitySql = "SELECT available_count FROM bookcount WHERE ISBN = ?";
        Integer availableCount = jdbcTemplate.queryForObject(checkAvailabilitySql, new Object[]{isbn}, Integer.class);

        if (availableCount == null || availableCount <= 0) {
            return false;
        }

        // Determine the next reservation_id
        String getMaxReservationIdSql = "SELECT COALESCE(MAX(reservation_id), 0) FROM reservation";
        Integer maxReservationId = jdbcTemplate.queryForObject(getMaxReservationIdSql, Integer.class);
        int nextReservationId = (maxReservationId != null ? maxReservationId : 0) + 1;

        // Insert reservation record
        String insertReservationSql = "INSERT INTO reservation (reservation_id, user_id, ISBN, reservation_date, expiration_date, reservation_status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        int rowsInserted = jdbcTemplate.update(insertReservationSql,
                nextReservationId, // The new reservation_id
                userId,
                isbn,
                LocalDate.now(),
                LocalDate.now().plusDays(7), // Reservation valid for 7 days
                "PENDING");

        if (rowsInserted > 0) {
            String updateBookCountSql = "UPDATE bookcount SET available_count = available_count - 1 WHERE ISBN = ?";
            jdbcTemplate.update(updateBookCountSql, isbn);
            return true;
        }

        return false;
    }
    public List<Reservation> getUserReservations(int userId) {
        String sql = "SELECT reservation_id, ISBN, reservation_date, expiration_date, reservation_status " +
                "FROM reservation WHERE user_id = ?";
        return jdbcTemplate.query(sql, new Object[]{userId}, this::mapReservation);
    }

    public void cancelReservation(int reservationId, int userId) {
        // Check if the reservation belongs to the user and is still active
        String checkReservationSql = "SELECT ISBN FROM reservation WHERE reservation_id = ? AND user_id = ? AND reservation_status = 'PENDING'";
        List<String> isbnList = jdbcTemplate.query(checkReservationSql, new Object[]{reservationId, userId},
                (rs, rowNum) -> rs.getString("ISBN"));

        if (isbnList.isEmpty()) {
            throw new IllegalArgumentException("Reservation not found or cannot be canceled.");
        }

        String isbn = isbnList.get(0);

        // Update the reservation status to CANCELED
        String cancelReservationSql = "UPDATE reservation SET reservation_status = 'CANCELED' WHERE reservation_id = ?";
        jdbcTemplate.update(cancelReservationSql, reservationId);

        // Increment the book count
        String incrementBookCountSql = "UPDATE bookcount SET available_count = available_count + 1 WHERE ISBN = ?";
        jdbcTemplate.update(incrementBookCountSql, isbn);
    }

    private Reservation mapReservation(ResultSet rs, int rowNum) throws SQLException {
        Reservation reservation = new Reservation();
        reservation.setReservation_id(rs.getInt("reservation_id"));
        reservation.setIsbn(rs.getString("ISBN"));
        reservation.setReservation_date(rs.getDate("reservation_date").toLocalDate());
        reservation.setExpiration_date(rs.getDate("expiration_date").toLocalDate());
        reservation.setReservation_status(rs.getString("reservation_status"));
        return reservation;
    }

    public List<Reservation> getAllReservations() {
        String sql = "SELECT reservation_id, user_id, ISBN, reservation_date, expiration_date, reservation_status " +
                "FROM reservation";
        return jdbcTemplate.query(sql, this::mapReservation);
    }

    public void cancelReservationAsAdmin(int reservationId) {
        String checkReservationSql = "SELECT ISBN FROM reservation WHERE reservation_id = ? AND reservation_status = 'PENDING'";
        List<String> isbnList = jdbcTemplate.query(checkReservationSql, new Object[]{reservationId},
                (rs, rowNum) -> rs.getString("ISBN"));

        if (isbnList.isEmpty()) {
            throw new IllegalArgumentException("Reservation not found or already canceled.");
        }

        String isbn = isbnList.get(0);

        // Update the reservation status to CANCELED
        String cancelReservationSql = "UPDATE reservation SET reservation_status = 'CANCELED' WHERE reservation_id = ?";
        jdbcTemplate.update(cancelReservationSql, reservationId);

        // Increment the book count
        String incrementBookCountSql = "UPDATE bookcount SET available_count = available_count + 1 WHERE ISBN = ?";
        jdbcTemplate.update(incrementBookCountSql, isbn);
    }

    public List<Reservation> getOverdueReservations() {
        String sql = "SELECT reservation_id, user_id, ISBN, reservation_date, expiration_date, reservation_status " +
                "FROM reservation " +
                "WHERE expiration_date < ? AND reservation_status = 'PENDING'";
        return jdbcTemplate.query(sql, new Object[]{LocalDate.now()}, this::mapReservation);
    }

}

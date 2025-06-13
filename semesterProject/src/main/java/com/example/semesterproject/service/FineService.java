package com.example.semesterproject.service;

import com.example.semesterproject.model.Fine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class FineService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FineService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Fine> getFineByUserId(int userId) {
        String sql = "SELECT f.fine_id, f.borrowing_id, f.fine_amount, f.fine_due_date, f.fine_status " +
                "FROM fine f " +
                "JOIN borrowing_record br ON f.borrowing_id = br.borrowing_id " +
                "WHERE br.user_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToFine, userId);
    }

    public List<Fine> getAllFines() {
        String sql = "SELECT f.fine_id, f.borrowing_id, f.fine_amount, f.fine_due_date, f.fine_status, br.user_id, br.isbn " +
                     "FROM fine f " +
                     "JOIN borrowing_record br ON f.borrowing_id = br.borrowing_id";
        return jdbcTemplate.query(sql, this::mapRowToFine);
    }

    public boolean payFineAdmin(int fineId) {
        String sql = "UPDATE fine SET fine_status = 'PAID' WHERE fine_id = ?";
        return jdbcTemplate.update(sql, fineId) > 0;
    }

    public void deleteFine(int fineId) {
        String sql = "DELETE FROM fine WHERE fine_id = ?";
        jdbcTemplate.update(sql, fineId);
    }

    private Fine mapRowToFine(ResultSet rs, int rowNum) throws SQLException {
        Fine fine = new Fine();

        fine.setFineId(rs.getInt("fine_id"));
        fine.setBorrowing_Id(rs.getInt("borrowing_id"));
        fine.setFineAmount(rs.getDouble("fine_amount"));
        fine.setFineDueDate(rs.getDate("fine_due_date").toLocalDate());
        fine.setFineStatus(rs.getString("fine_status"));

        return fine;
    }

}

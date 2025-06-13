package com.example.semesterproject.service;

import com.example.semesterproject.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class UserService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User authenticateUser(String username, String password) {
        try {
            String sql = "SELECT * FROM users WHERE user_name = ? AND password = ?";
            // Query database for user with matching username and password
            return jdbcTemplate.queryForObject(sql, new Object[]{username, password}, (rs, rowNum) -> {
                User user = new User();
                user.setUserID(rs.getInt("user_id"));
                user.setUsername(rs.getString("user_name"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                user.setRegistrationDate(String.valueOf(rs.getDate("registration_date")));
                user.setLastLoginDate(String.valueOf(rs.getDate("last_login_date")));

                return user;
            });
        } catch (Exception e) {
            return null;
        }
    }

    public boolean userExists(String username, String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE user_name = ? OR email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username, email);
        return count != null && count > 0;
    }

    @Transactional
    public boolean registerUser(User user) {
        try {
            if (userExists(user.getUsername(), user.getEmail())) {
                return false;
            }
            // Fetch the last user_id from the database and increment it
            String fetchIdSql = "SELECT COALESCE(MAX(user_id), 0) FROM users";
            Integer lastUserId = jdbcTemplate.queryForObject(fetchIdSql, Integer.class);

            int newUserId = (lastUserId != null) ? lastUserId + 1 : 1;

            // Insert the new user into the database
            String insertSql = "INSERT INTO users (user_id, user_name, email, password, role, registration_date) VALUES (?, ?, ?, ?, ?, SYSDATE)";
            int rowsAffected = jdbcTemplate.update(insertSql, newUserId, user.getUsername(), user.getEmail(), user.getPassword(), user.getRole());

            return rowsAffected > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<User> getAllUsers() {
        String sql = "SELECT user_id as userID, user_name as username , password, email, role, registration_date as registrationDate, last_login_date as lastLoginDate FROM users";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class));
    }

    public User getUserById(int userId) {
        String sql = "SELECT user_id as userID, user_name as username , password, email, role, registration_date as registrationDate, last_login_date as lastLoginDate FROM users WHERE user_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{userId}, new BeanPropertyRowMapper<>(User.class));
        } catch (Exception e) {
            return null;
        }
    }

    public boolean addUser(User user) {
        String sql = "INSERT INTO users (user_name, email, password, role, registration_date) VALUES (?, ?, ?, ?, SYSDATE)";
        int rowsAffected = jdbcTemplate.update(sql, user.getUsername(), user.getEmail(), user.getPassword(), user.getRole());
        return rowsAffected > 0;
    }

    @Transactional
    public boolean updateUser(int userId, User user) {
        String sql = "UPDATE users SET user_name = ?, email = ?, password = ?, role = ? WHERE user_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, user.getUsername(), user.getEmail(), user.getPassword(), user.getRole(), userId);
        return rowsAffected > 0;
    }

    @Transactional
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, userId);
        return rowsAffected > 0;
    }

    public User getUserProfile(int userId) {
        String sql = "SELECT user_id AS userID, user_name AS username, password, email, role, " +
                "registration_date AS registrationDate, last_login_date AS lastLoginDate " +
                "FROM users WHERE user_id = ?";

        return jdbcTemplate.queryForObject(sql, new Object[]{userId}, (rs, rowNum) -> {
            User user = new User();
            user.setUserID(rs.getInt("userID"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password")); // Store password only if necessary
            user.setEmail(rs.getString("email"));
            user.setRole(rs.getString("role"));
            return user;
        });
    }

    public void updateUserProfile(User user) {
        String sql = "UPDATE users SET user_name = ?, email = ? WHERE user_id = ?";
        jdbcTemplate.update(sql, user.getUsername(), user.getEmail(), user.getUserID());
    }


}

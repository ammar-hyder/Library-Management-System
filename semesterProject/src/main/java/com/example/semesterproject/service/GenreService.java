package com.example.semesterproject.service;

import com.example.semesterproject.model.Genre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class GenreService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> getAllGenres() {
        String sql = "SELECT genre_name, genre_description FROM genre";
        List<Map<String, Object>> genres = jdbcTemplate.queryForList(sql);
        return genres;
    }

    public boolean genreExists(String genreName) {
        String sql = "SELECT COUNT(*) FROM genre WHERE genre_name = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, genreName);
        return count != null && count > 0;
    }

    public boolean addGenre(Genre genre) {
        // Check if genre already exists
        if (genreExists(genre.getGenre_Name())) {
            return false;  // Genre exists, cannot be added
        }

        // Insert the genre if it doesn't exist
        String sql = "INSERT INTO genre (genre_name, genre_description) VALUES (?, ?)";
        int rowsAffected = jdbcTemplate.update(sql, genre.getGenre_Name(), genre.getGenre_Description());
        return rowsAffected > 0;  // Return true if the genre was added successfully
    }

    public Genre getGenreByName(String genreName) {
        String sql = "SELECT genre_name, genre_description FROM genre WHERE genre_name = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setGenre_Name(rs.getString("genre_name"));
            genre.setGenre_Description(rs.getString("genre_description"));
            return genre;
        }, genreName);
    }
}

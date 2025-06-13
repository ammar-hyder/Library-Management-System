package com.example.semesterproject.service;

import com.example.semesterproject.model.Book;
import com.example.semesterproject.model.BookCount;
import com.example.semesterproject.model.dBook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
public class BookService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BookService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Transactional
    public boolean returnBook(String isbn, int userId) {
        // Check if the user has borrowed the book
        String checkBorrowingRecordSql = "SELECT borrowing_id, borrow_date, due_date FROM borrowing_record WHERE user_id = ? AND ISBN = ? AND status = 'BORROWED'";

        Map<String, Object> record = jdbcTemplate.queryForMap(checkBorrowingRecordSql, userId, isbn);

        if (record == null || record.isEmpty()) {
            return false;
        }

        Timestamp borrowTimestamp = (Timestamp) record.get("borrow_date");
        Timestamp dueTimestamp = (Timestamp) record.get("due_date");

        LocalDate borrowDate = borrowTimestamp.toLocalDateTime().toLocalDate();
        LocalDate dueDate = dueTimestamp.toLocalDateTime().toLocalDate();
        LocalDate returnDate = LocalDate.now();


        long daysLate = ChronoUnit.DAYS.between(dueDate, returnDate);

        double fine = 0.0;
        if (daysLate > 0) {
            fine = daysLate * 1.0; // You can change this rate as needed
        }

        // Update the borrowing record status to 'RETURNED' and add return date
        String updateBorrowingRecordSql = "UPDATE borrowing_record SET status = 'RETURNED', return_date = ? WHERE user_id = ? AND ISBN = ? AND status = 'BORROWED'";

        int rowsUpdated = jdbcTemplate.update(updateBorrowingRecordSql, Date.valueOf(returnDate), userId, isbn);

        if (rowsUpdated > 0) {
            // Insert a record into the fine table if a fine is applicable
            if (fine > -1) {
                // Get the next fine_id
                String getNextFineIdSql = "SELECT NVL(MAX(fine_id), 0) + 1 AS next_fine_id FROM fine";
                Integer nextFineId = jdbcTemplate.queryForObject(getNextFineIdSql, Integer.class);

                // Insert the fine record
                String insertFineSql = "INSERT INTO fine (fine_id, borrowing_id, fine_amount, fine_due_date, fine_status) VALUES (?, ?, ?, ?, ?)";
                LocalDate fineDueDate = returnDate.plusDays(7); // Set fine due date (7 days after return date)
                jdbcTemplate.update(insertFineSql, nextFineId, record.get("borrowing_id"), fine, Date.valueOf(fineDueDate), "PENDING");
            }

            // Increase the available count of the book
            String updateBookCountSql = "UPDATE bookcount SET available_count = available_count + 1 WHERE ISBN = ?";
            jdbcTemplate.update(updateBookCountSql, isbn);

            return true;
        }

        return false;
    }


    public List<Map<String, Object>> getBorrowedBooksByUser(int userId) {
        String sql = "SELECT b.ISBN, b.title, b.author, br.borrow_date, br.due_date, br.status " +
                "FROM books b " +
                "JOIN borrowing_record br ON b.ISBN = br.ISBN " +
                "WHERE br.user_id = ? AND br.status = 'BORROWED'";

        return jdbcTemplate.queryForList(sql, userId);
    }

    @Transactional
    public boolean borrowBook(String isbn, int userId) {
        String checkBorrowingRecordSql = "SELECT COUNT(*) FROM borrowing_record WHERE user_id = ? AND ISBN = ? AND status = 'BORROWED'";

        int count = jdbcTemplate.queryForObject(checkBorrowingRecordSql, new Object[]{userId, isbn}, Integer.class);

        if (count > 0) {
            // The user already borrowed the book, can't borrow again
            return false;
        }

        String fetchMaxBorrowingIdSql = "SELECT MAX(borrowing_id) FROM borrowing_record";
        Integer lastBorrowingId = jdbcTemplate.queryForObject(fetchMaxBorrowingIdSql, Integer.class);

        // Generate the next borrowing ID (last + 1)
        int nextBorrowingId = (lastBorrowingId == null) ? 1 : lastBorrowingId + 1;


        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(14);

        // Insert new borrowing record into the borrowing_record table
        String insertBorrowingRecordSql = "INSERT INTO borrowing_record (borrowing_id, user_id, ISBN, borrow_date, due_date, status) VALUES (?, ?, ?, ?, ?, ?)";

        int rowsInserted = jdbcTemplate.update(insertBorrowingRecordSql, nextBorrowingId, userId, isbn, Date.valueOf(borrowDate), Date.valueOf(dueDate), "BORROWED");

        if (rowsInserted > 0) {
            String updateBookCountSql = "UPDATE bookcount SET available_count = available_count - 1 WHERE ISBN = ?";
            jdbcTemplate.update(updateBookCountSql, isbn);

            return true;
        }

        return false;
    }

    public List<dBook> getAllBooks() {
        String sql = "SELECT b.ISBN, b.title, b.author, b.genre_name, bc.available_count " +
                "FROM books b JOIN bookcount bc ON b.ISBN = bc.ISBN";

        return jdbcTemplate.query(sql, new RowMapper<dBook>() {
            @Override
            public dBook mapRow(ResultSet rs, int rowNum) throws SQLException {
                dBook book = new dBook();
                book.setISBN(rs.getString("ISBN"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setGenre_name(rs.getString("genre_name"));
                book.setAvailable_count(rs.getInt("available_count"));
                return book;
            }
        });
    }

    public List<dBook> getAlldBooks() {
        // SQL query to join Book and BookCount on ISBN
        String sql = "SELECT b.ISBN, b.title, b.author, b.genre_name, bc.total_count, bc.available_count " +
                "FROM books b " +
                "JOIN bookcount bc ON b.ISBN = bc.ISBN";

        // RowMapper to map each row of the result set to a dBook object
        RowMapper<dBook> rowMapper = (rs, rowNum) -> {
            dBook book = new dBook();
            book.setISBN(rs.getString("ISBN"));
            book.setTitle(rs.getString("title"));
            book.setAuthor(rs.getString("author"));
            book.setGenre_name(rs.getString("genre_name"));
            book.setTotal_count(rs.getInt("total_count"));
            book.setAvailable_count(rs.getInt("available_count"));
            return book;
        };

        return jdbcTemplate.query(sql, rowMapper);
    }


    public boolean bookExists(String isbn) {
        String sql = "SELECT COUNT(*) FROM books WHERE ISBN = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, isbn);
        return count != null && count > 0;
    }

    public boolean addBookCount(BookCount bookCount) {
        String checkSql = "SELECT COUNT(*) FROM bookcount WHERE ISBN = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, bookCount.getISBN());

        if (count != null && count > 0) {
            return false;
        }
        // SQL to insert a new book count
        String insertSql = "INSERT INTO bookcount (ISBN, total_count, available_count) VALUES (?, ?, ?)";
        return jdbcTemplate.update(insertSql, bookCount.getISBN(), bookCount.getTotal_count(), bookCount.getAvailable_count()) > 0;
    }


    public List<Map<String, Object>> getAllBookCount() {
        String sql = "SELECT bc.ISBN, bc.total_count, bc.available_count " +
                "FROM bookcount bc";

        return jdbcTemplate.queryForList(sql);
    }


    public boolean genreExists(String genreName) {
        String sql = "SELECT COUNT(*) FROM genre WHERE genre_name = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, genreName);
        System.out.println(count);
        return count != null && count > 0;
    }

    @Transactional
    public boolean addBook(Book book) {
        String sql = "INSERT INTO books (ISBN, title, author, genre_name) VALUES (?, ?, ?, ?)";
        int rowsAffected = jdbcTemplate.update(sql, book.getISBN(), book.getTitle(), book.getAuthor(), book.getGenre_name());
        return rowsAffected > 0;
    }

    public Book getBookByIsbn(String isbn) {
        String sql = "SELECT * FROM books WHERE ISBN = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                Book book = new Book();
                book.setISBN(rs.getString("ISBN"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setGenre_name(rs.getString("genre_name"));
                return book;
            }, isbn);
        } catch (EmptyResultDataAccessException e) {
            System.out.println("No book found with ISBN: " + isbn);
            return null;
        } catch (Exception e) {
            System.out.println("Error fetching book by ISBN: " + e.getMessage());
            return null;
        }
    }
    @Transactional
    public boolean updateBook(String isbn, Book book) {
        System.out.println(book.getGenre_name());
        String sql = "UPDATE books SET title = ?, author = ?, genre_name = ? WHERE ISBN = ?";
        int rowsAffected = jdbcTemplate.update(sql, book.getTitle(), book.getAuthor(), book.getGenre_name(), isbn);
        System.out.println(rowsAffected);
        return rowsAffected > 0;
    }
    @Transactional
    public boolean deleteBook(String isbn) {
        String sql = "DELETE FROM books WHERE ISBN = ?";
        int rowsAffected = jdbcTemplate.update(sql, isbn);
        return rowsAffected > 0;
    }
}

package com.example.semesterproject.controller;

import com.example.semesterproject.model.Book;
import com.example.semesterproject.model.BookCount;
import com.example.semesterproject.model.Genre;
import com.example.semesterproject.model.dBook;
import com.example.semesterproject.service.BookService;
import com.example.semesterproject.service.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    @Autowired
    private GenreService genreService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // Show Manage Books Page
    @GetMapping
    public String manageBooks() {
        return "manageBooks";
    }

    // Show Add Book Form
    @GetMapping("/addBook")
    public String showAddBookForm(Model model) {
        List<Map<String, Object>> genres = genreService.getAllGenres();
        model.addAttribute("genres", genres);
        return "addBook";
    }

    // Handle Add Book Form Submission
    @PostMapping("/addBook")
    public String addBook(@RequestParam String isbn,
                          @RequestParam String title,
                          @RequestParam String author,
                          @RequestParam String genre,
                          @RequestParam int totalCount, Model model) {
        if (bookService.bookExists(isbn)) {
            model.addAttribute("error", "Book with this ISBN already exists.");
            return "addBook";
        }
        Book book = new Book();
        book.setISBN(isbn);
        book.setTitle(title);
        book.setAuthor(author);
        book.setGenre_name(genre);
        BookCount bookCount = new BookCount();
        bookCount.setISBN(isbn);
        bookCount.setAvailable_count(totalCount);
        bookCount.setTotal_count(totalCount);

        boolean success = bookService.addBook(book);
        if (success) {
            bookService.addBookCount(bookCount);
            return "redirect:/books";
        } else {
            model.addAttribute("error", "Failed to add the book. Please try again.");
            return "addBook";
        }
    }
    @GetMapping("/viewBooks")
    public String viewBooks(Model model) {
        List<dBook> books = bookService.getAlldBooks();
        model.addAttribute("books", books);
        return "viewBooks";
    }

    // Show Edit Book Form
    @GetMapping("/edit/{isbn}")
    public String showEditBookForm(@PathVariable("isbn") String isbn, Model model) {
        Book book = bookService.getBookByIsbn(isbn);
        if (book == null) {
            return "error";
        }
        model.addAttribute("book", book);
        List<Map<String, Object>> genres = genreService.getAllGenres();
        model.addAttribute("genres", genres);
        return "editBook";
    }

    // Handle Edit Book Form Submission
    @PostMapping("/edit/{isbn}")
    public String updateBook(@PathVariable("isbn") String ISBN, @RequestParam String isbn,
                                                                @RequestParam String title,
                                                                @RequestParam String author,
                                                                @RequestParam String genre, Model model) {
        Book book = new Book();
        book.setISBN(isbn);
        book.setTitle(title);
        book.setAuthor(author);
        book.setGenre_name(genre);

        boolean updated = bookService.updateBook(ISBN, book);
        if (updated) {
            return "redirect:/books";
        }
        return "error";
    }

    // Handle Delete Book
    @GetMapping("/delete/{isbn}")
    public String deleteBook(@PathVariable("isbn") String isbn) {
        boolean deleted = bookService.deleteBook(isbn);
        if (deleted) {
            return "redirect:/books";
        }
        return "error";
    }
}

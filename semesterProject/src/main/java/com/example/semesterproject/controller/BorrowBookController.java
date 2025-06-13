package com.example.semesterproject.controller;

import com.example.semesterproject.model.dBook;
import com.example.semesterproject.service.BookService;
import com.example.semesterproject.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/borrowBooks")
@SessionAttributes("loggedInUser")
public class BorrowBookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public String showBorrowBooksPage(Model model) {
        List<dBook> books = bookService.getAllBooks();
        model.addAttribute("books", books);
        return "borrowBooks";
    }

    @PostMapping("/borrow/{isbn}")
    public String borrowBook(@PathVariable String isbn, Model model) {
        User loggedInUser = (User) model.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login?error=UserNotAuthenticated";
        }

        int userId = loggedInUser.getUserID();


        boolean isSuccess = bookService.borrowBook(isbn, userId);

        if (isSuccess) {
            return "redirect:/borrowBooks";
        } else {
            return "redirect:/borrowBooks?error=BookNotAvailable";
        }
    }

    @GetMapping("/returnBook")
    public String getBorrowedBooks(Model model) {
        User loggedInUser = (User) model.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login?error=UserNotAuthenticated";
        }

        int userId = loggedInUser.getUserID();
        List<Map<String, Object>> borrowedBooks = bookService.getBorrowedBooksByUser(userId);
        model.addAttribute("borrowedBooks", borrowedBooks);
        return "borrowedBooks";
    }
    // Return a borrowed book
    @PostMapping("/return")
    public String returnBook(@RequestParam String isbn, Model model) {
        User loggedInUser = (User) model.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login?error=UserNotAuthenticated";
        }

        int userId = loggedInUser.getUserID();

        boolean isReturned = bookService.returnBook(isbn, userId);

        if (isReturned) {
            model.addAttribute("message", "Book returned successfully!");
        } else {
            model.addAttribute("message", "Error occurred while returning the book.");
        }

        return "redirect:/borrowBooks/returnBook";
    }

}

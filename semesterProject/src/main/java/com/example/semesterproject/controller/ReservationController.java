package com.example.semesterproject.controller;

import com.example.semesterproject.model.Reservation;
import com.example.semesterproject.model.dBook;
import com.example.semesterproject.model.User;
import com.example.semesterproject.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/reservations")
@SessionAttributes("loggedInUser")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;
    @GetMapping
    public String viewReservations(Model model) {
        User loggedInUser = (User) model.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login";
        }

        List<Reservation> reservations = reservationService.getUserReservations(loggedInUser.getUserID());
        model.addAttribute("reservations", reservations);

        return "viewReservations";
    }

    @PostMapping("/cancel/{reservationId}")
    public String cancelReservation(@PathVariable int reservationId, Model model) {
        User loggedInUser = (User) model.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login";
        }

        reservationService.cancelReservation(reservationId, loggedInUser.getUserID());
        return "redirect:/reservations";
    }

    @GetMapping("/reserve")
    public String reservation(Model model) {
        List<dBook> books = reservationService.getAllBooks();
        model.addAttribute("books", books);
        return "reserveBook";
    }

    @PostMapping("/reserve/{isbn}")
    public String reserveBook(@PathVariable String isbn, Model model) {
        User loggedInUser = (User) model.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        boolean isReserved = reservationService.reserveBook(loggedInUser.getUserID(), isbn);
        if (!isReserved) {
            return "error";
        }

        return "redirect:/reservations";
    }

    @GetMapping("/admin/view-all")
    public String adminViewAllReservations(Model model) {

        List<Reservation> reservations = reservationService.getAllReservations();
        model.addAttribute("reservations", reservations);

        return "adminViewReservations";
    }

    @PostMapping("/admin/cancel/{reservationId}")
    public String adminCancelReservation(@PathVariable int reservationId, Model model) {

        reservationService.cancelReservationAsAdmin(reservationId);
        return "redirect:/reservations/admin/view-all";
    }

    @GetMapping("/admin/overdue")
    public String adminViewOverdueReservations(Model model) {

        List<Reservation> overdueReservations = reservationService.getOverdueReservations();
        model.addAttribute("reservations", overdueReservations);

        return "adminOverdueReservations";
    }

    /**
     * Admin: Cancel an overdue reservation by ID.
     */
    @PostMapping("/admin/overdue/cancel/{reservationId}")
    public String adminCancelOverdueReservation(@PathVariable int reservationId, Model model) {
        reservationService.cancelReservationAsAdmin(reservationId);
        return "redirect:/reservations/admin/overdue"; // Refresh the overdue reservations view
    }

}

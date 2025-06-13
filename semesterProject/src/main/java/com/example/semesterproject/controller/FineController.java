package com.example.semesterproject.controller;

import com.example.semesterproject.model.Fine;
import com.example.semesterproject.model.User;
import com.example.semesterproject.service.FineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/fines")
@SessionAttributes("loggedInUser")
public class FineController {

    @Autowired
    private FineService fineService; // Inject the FineService

    /**
     * Display all fines for the logged-in user.
     */
    @GetMapping
    public String viewFines(Model model) {
        User loggedInUser = (User) model.getAttribute("loggedInUser");
        // Check if loggedInUser is valid
        if (loggedInUser == null) {
            // Redirect to the login page if the user is not logged in
            return "redirect:/login";
        }

        // Extract userId from the session attribute
        int userId = loggedInUser.getUserID();

        // Fetch fines for the logged-in user
        List<Fine> fines = fineService.getFineByUserId(userId);

        // Add fines to the model to render on the view page
        model.addAttribute("fines", fines);

        // Return the view name (e.g., 'checkFines')
        return "checkFines"; // Ensure this view exists (e.g., a Thymeleaf or JSP file)
    }

    /**
     * Pay a specific fine for the logged-in user.
     */
    @PostMapping("/pay/{fineId}")
    public String payFine(@PathVariable int fineId, Model model) {
        User loggedInUser = (User) model.getAttribute("loggedInUser");
        // Check if loggedInUser is valid
        if (loggedInUser == null) {
            // Redirect to the login page if the user is not logged in
            return "redirect:/login";
        }

        // Extract userId from the session attribute (even if not used directly for this method)
        int userId = loggedInUser.getUserID();

        // Call the service method to mark the fine as paid
        boolean isPaid = fineService.payFineAdmin(fineId);

        if (isPaid) {
            // Redirect back to the fines page after successful payment
            return "redirect:/fines";
        } else {
            // Handle errors or payment failure (optional: add an error message to the model)
            return "errorPage"; // Replace with the actual error page
        }
    }

    // Admin part

    /**
     * Display all fines for the admin to manage.
     */
    @GetMapping("/admin")
    public String viewAllFinesForAdmin(Model model) {
        List<Fine> fines = fineService.getAllFines();
        System.out.println(fines); // Debug line to check the fines fetched from database
        model.addAttribute("fines", fines);
        return "manageFines";
    }


    /**
     * Mark a fine as paid for the admin.
     */
    @PostMapping("/admin/pay/{fineId}")
    public String markFineAsPaid(@PathVariable int fineId) {
        // Mark the fine as paid
        boolean isPaid = fineService.payFineAdmin(fineId);

        // Redirect back to the manage fines page
        return isPaid ? "redirect:/fines/admin" : "errorPage"; // Replace `errorPage` with actual error handling page
    }

    /**
     * Delete a fine for the admin.
     */
    @PostMapping("/admin/delete/{fineId}")
    public String deleteFine(@PathVariable int fineId) {
        // Delete the fine record
        fineService.deleteFine(fineId);

        // Redirect back to the manage fines page
        return "redirect:/fines/admin";
    }
}

package com.example.semesterproject.controller;

import com.example.semesterproject.model.User;
import com.example.semesterproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@SessionAttributes("loggedInUser")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Add 'loggedInUser' attribute to the session
    @ModelAttribute("loggedInUser")
    public User setUpUser() {
        return new User();
    }

    // Display login page
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    // Handle login submission
    @PostMapping("/login")
    public String loginUser(@RequestParam("username") String username, @RequestParam("password") String password, Model model) {

        User user = userService.authenticateUser(username, password);

        if (user != null) {
            model.addAttribute("loggedInUser", user); // Add user to session

            if ("Admin".equalsIgnoreCase(user.getRole())) {
                return "redirect:/adminHome";
            } else if ("User".equalsIgnoreCase(user.getRole())) {
                return "redirect:/userHome";
            }
        }


        model.addAttribute("error", "Login failed. Invalid username or password.");
        return "login";
    }

    // Logout functionality
    @GetMapping("/logout")
    public String logoutUser(Model model) {
        model.addAttribute("loggedInUser", null);
        return "redirect:/login";
    }

    @GetMapping("/signup")
    public String showSignupPage() {
        return "signup";
    }

    @PostMapping("/signup")
    public String handleSignup(@RequestParam String username, @RequestParam String email, @RequestParam String password, @RequestParam String confirmPassword, Model model) {

        if (!password.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "Passwords do not match.");
            return "signup";
        }

        if (userService.userExists(username, email)) {
            model.addAttribute("errorMessage", "User already exists with this username or email.");
            return "signup";
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setRole("user");

        boolean success = userService.registerUser(newUser);

        if (success) {
            return "redirect:/login";
        } else {
            model.addAttribute("errorMessage", "An error occurred. Please try again.");
            return "signup";
        }
    }

    @GetMapping("/userHome")
    public String showUserHomePage() {
        return "userHome";
    }
// ----------------------------------------------------------------------------------------------
    @GetMapping("/adminHome")
    public String showAdminHomePage() {
        return "adminHome";
    }

    @GetMapping("/manageUsers")
    public String manageUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "manageUsers";
    }

    @GetMapping("/adminHome/manageUsers/edit/{userId}")
    public String showEditUserForm(@PathVariable("userId") int userId, Model model) {
        User user = userService.getUserById(userId);
        model.addAttribute("user", user);
        return "editUser";
    }

    // Handle user update
    @PostMapping("/adminHome/manageUsers/update")
    public String updateUser(@ModelAttribute User user) {
        boolean updated = userService.updateUser(user.getUserID(),user);
        if (updated) {
            return "redirect:/manageUsers";
        }
        return "error";
    }

    @GetMapping("/adminHome/manageUsers/delete/{userId}")
    public String deleteUser(@PathVariable("userId") int userId) {
        boolean deleted = userService.deleteUser(userId);
        if (deleted) {
            return "redirect:/manageUsers";
        }
        return "error";
    }

    @GetMapping("/adminHome/manageUsers/add")
    public String showAddUserForm(Model model) {
        model.addAttribute("addUserFormVisible", true); // Make the add user form visible
        return "manageUsers";
    }

    @PostMapping("/adminHome/manageUsers/add")
    public String addUser(@RequestParam String username, @RequestParam String email, @RequestParam String password, @RequestParam String confirmPassword, @RequestParam String role, Model model) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "Passwords do not match.");
            return "manageUsers";
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setRole(role);

        boolean success = userService.registerUser(newUser);
        if (success) {
            return "redirect:/manageUsers";
        } else {
            model.addAttribute("errorMessage", "User registration failed. Please try again.");
            return "manageUsers";
        }
    }

    @GetMapping("/profile")
    public String viewUserProfile(Model model) {
        User loggedInUser = (User) model.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login";
        }

        User userProfile = userService.getUserProfile(loggedInUser.getUserID());
        model.addAttribute("userProfile", userProfile);

        return "viewProfile";
    }

    @GetMapping("/profile/edit")
    public String editUserProfile(Model model) {
        User loggedInUser = (User) model.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login";
        }

        User userProfile = userService.getUserProfile(loggedInUser.getUserID());
        model.addAttribute("userProfile", userProfile);

        return "editProfile";
    }

    @PostMapping("/profile/update")
    public String updateUserProfile(@ModelAttribute User userProfile, Model model) {
        User loggedInUser = (User) model.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login";
        }
        userService.updateUserProfile(userProfile);
        model.addAttribute("loggedInUser", userProfile);

        return "redirect:/profile";
    }

}

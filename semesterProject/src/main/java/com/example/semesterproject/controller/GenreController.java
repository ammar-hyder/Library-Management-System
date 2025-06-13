package com.example.semesterproject.controller;

import com.example.semesterproject.model.Genre;
import com.example.semesterproject.service.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/genre")
public class GenreController {

    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping("/addGenre")
    public String showAddGenreForm() {
        return "addGenre";
    }

    @PostMapping("/addGenre")
    public String addGenre(@RequestParam("genreName") String genreName, @RequestParam("genreDescription") String genreDescription, Model model) {

        Genre genre = new Genre();
        genre.setGenre_Name(genreName);
        genre.setGenre_Description(genreDescription);

        if (genreService.genreExists(genreName)) {
            model.addAttribute("error", "Genre already exists.");
            return "addGenre"; // Return to Add Genre page with error message
        }

        boolean success = genreService.addGenre(genre);
        if (success) {
            model.addAttribute("success", "Genre added successfully.");
            return "addGenre"; // Return to Add Genre page with success message
        } else {
            model.addAttribute("error", "Failed to add genre. Please try again.");
            return "addGenre"; // Return to Add Genre page with error message
        }
    }

    @GetMapping("/viewGenres")
    public String viewGenres(Model model) {
        List<Map<String, Object>> genres = genreService.getAllGenres();
        model.addAttribute("genres", genres);
        return "viewGenre";
    }
}

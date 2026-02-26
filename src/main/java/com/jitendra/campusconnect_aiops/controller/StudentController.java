package com.jitendra.campusconnect_aiops.controller;

import com.jitendra.campusconnect_aiops.model.User;
import com.jitendra.campusconnect_aiops.repository.JobRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private JobRepository jobRepository;

    @GetMapping("/jobs")
    public String viewJobs(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null)
            return "redirect:/";

        model.addAttribute("jobs", jobRepository.findAll());
        model.addAttribute("role", user.getRole());
        return "student-jobs";
    }
}

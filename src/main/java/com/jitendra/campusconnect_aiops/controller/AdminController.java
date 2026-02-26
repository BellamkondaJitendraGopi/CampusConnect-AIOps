package com.jitendra.campusconnect_aiops.controller;

import com.jitendra.campusconnect_aiops.model.JobListing;
import com.jitendra.campusconnect_aiops.model.User;
import com.jitendra.campusconnect_aiops.repository.JobRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private JobRepository jobRepository;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {
        if (!isAdmin(session)) return "redirect:/";
        return "admin-dashboard"; // updated to match our new template
    }

    @GetMapping("/add-job")
    public String addJobForm(HttpSession session) {
        if (!isAdmin(session)) return "redirect:/";
        return "admin-add-job"; // updated template
    }

    @PostMapping("/add-job")
    public String addJob(
            @RequestParam String companyName,
            @RequestParam String role,
            @RequestParam String description,
            @RequestParam String location,
            @RequestParam Double packageAmount,
            HttpSession session
    ) {
        if (!isAdmin(session)) return "redirect:/";

        // Get currently logged-in user
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        // Save job with postedBy set
        JobListing job = new JobListing();
        job.setCompanyName(companyName);
        job.setRole(role);
        job.setDescription(description);
        job.setLocation(location);
        job.setPackageAmount(packageAmount);
        job.setPostedBy(loggedInUser);

        jobRepository.save(job);

        return "redirect:/admin/dashboard";
    }

    private boolean isAdmin(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        return user != null && "ADMIN".equals(user.getRole());
    }
}

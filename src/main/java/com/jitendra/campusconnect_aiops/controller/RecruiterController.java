package com.jitendra.campusconnect_aiops.controller;

import com.jitendra.campusconnect_aiops.model.JobListing;
import com.jitendra.campusconnect_aiops.model.User;
import com.jitendra.campusconnect_aiops.repository.JobRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/recruiter")
public class RecruiterController {

    @Autowired
    private JobRepository jobRepository;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {
        if (!isRecruiter(session)) return "redirect:/";
        return "recruiter-dashboard"; // updated template
    }

    @GetMapping("/jobs")
    public String viewJobs(Model model, HttpSession session) {
        if (!isRecruiter(session)) return "redirect:/";

        // Optional: show only jobs posted by this recruiter
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        model.addAttribute("jobs", jobRepository.findAll()); // can filter later by postedBy
        return "recruiter-jobs";
    }

    @GetMapping("/add-job")
    public String addJobForm(HttpSession session) {
        if (!isRecruiter(session)) return "redirect:/";
        return "recruiter-add-job"; // new template
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
        if (!isRecruiter(session)) return "redirect:/";

        // Get logged-in user
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

        return "redirect:/recruiter/jobs";
    }

    private boolean isRecruiter(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        return user != null && "RECRUITER".equals(user.getRole());
    }
}

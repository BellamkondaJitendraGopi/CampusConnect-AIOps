package com.jitendra.campusconnect_aiops.controller;

import com.jitendra.campusconnect_aiops.model.User;
import com.jitendra.campusconnect_aiops.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login-process")
    public String loginProcess(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model
    ) {
        User user = userRepository.findByEmail(email);

        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute("loggedInUser", user);
            session.setAttribute("role", user.getRole());

            return switch (user.getRole()) {
                case "ADMIN" -> "redirect:/admin/dashboard";
                case "RECRUITER" -> "redirect:/recruiter/dashboard";
                case "STUDENT" -> "redirect:/student/jobs";
                default -> "redirect:/";
            };
        }

        model.addAttribute("error", "Invalid email or password");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}

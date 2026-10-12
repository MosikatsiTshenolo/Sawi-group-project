
package za.ac.spu.sawi.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import za.ac.spu.sawi.entity.User;
import za.ac.spu.sawi.repository.UserRepository;

@Controller
public class DashboardController {

    private final UserRepository userRepository;

    public DashboardController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalStateException("Logged-in user was not found."));

        model.addAttribute("username", user.getUsername());
        model.addAttribute("firstName", user.getFirstName());
        model.addAttribute("lastName", user.getLastName());

        return switch (user.getRole().getRoleName().toUpperCase()) {
            case "LECTURER" -> "redirect:/lecturer/dashboard";
            case "ADVISOR" -> "redirect:/advisor/dashboard";
            case "COUNSELOR" -> "redirect:/counselor/dashboard";
            case "ADMIN" -> "redirect:/admin/dashboard";
            default -> "dashboard";
        };
    }
}

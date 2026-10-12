package za.ac.spu.sawi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import za.ac.spu.sawi.service.PasswordResetService;

@Controller
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgot-password";
    }

    @PostMapping("/password-reset/request")
    public String requestReset(
            @RequestParam String usernameOrEmail,
            RedirectAttributes redirectAttributes) {
        passwordResetService.requestReset(usernameOrEmail);
        redirectAttributes.addFlashAttribute("requestSubmitted", true);
        return "redirect:/forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPassword(
            @RequestParam(required = false) String token,
            Model model) {
        model.addAttribute("token", token == null ? "" : token);
        model.addAttribute("tokenValid", passwordResetService.isTokenValid(token));
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String completeReset(
            @RequestParam String token,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Model model) {
        if (!passwordResetService.completeReset(token, newPassword, confirmPassword)) {
            model.addAttribute("token", token);
            model.addAttribute("tokenValid", passwordResetService.isTokenValid(token));
            model.addAttribute("errorMessage",
                    "The reset link is invalid or expired, or the passwords do not match. Use a password of at least 8 characters.");
            return "reset-password";
        }
        return "redirect:/login?passwordReset";
    }
}
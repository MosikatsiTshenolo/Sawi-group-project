
package za.ac.spu.sawi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import za.ac.spu.sawi.dto.RegisterRequest;
import za.ac.spu.sawi.service.RegistrationService;
import za.ac.spu.sawi.service.RegistrationOptionsService;

@Controller
public class AuthController {

    private final RegistrationService registrationService;
    private final RegistrationOptionsService registrationOptionsService;

    public AuthController(
            RegistrationService registrationService,
            RegistrationOptionsService registrationOptionsService) {
        this.registrationService = registrationService;
        this.registrationOptionsService = registrationOptionsService;
    }

    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        RegisterRequest request = new RegisterRequest();
        request.setRoleName("STUDENT");
        model.addAttribute("registerRequest", request);
        addRegistrationOptions(model);
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(
            @Valid @ModelAttribute("registerRequest")
            RegisterRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            addRegistrationOptions(model);
            return "register";
        }

        try {
            var user = registrationService.registerUser(request);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                        "Registration submitted. An administrator must approve your account before you can log in."
            );

            return "redirect:/login";

        } catch (IllegalArgumentException | IllegalStateException exception) {
            model.addAttribute("errorMessage", exception.getMessage());
            addRegistrationOptions(model);
            return "register";
        }
    }

    private void addRegistrationOptions(Model model) {
        model.addAttribute("modules", registrationOptionsService.studentModules());
        model.addAttribute("studentFaculties", java.util.List.of(
                "NAS", "EDUCATION", "HUMANITIES", "AGRICULTURE AND ECONOMICS"));
        model.addAttribute("advisorFaculties", java.util.List.of(
                "NAS", "EDUCATION", "HUMANITIES", "AGRICULTURE AND ECONOMICS"));
        model.addAttribute("lecturerFaculties", java.util.List.of(
                "ICT301", "Computer science", "Data science",
                "BEd", "Agriculture", "BCom accounting"));
        model.addAttribute("courses", java.util.List.of(
                "ICT301", "Computer science", "Data science",
                "BEd", "Agriculture", "BCom accounting"));
    }
}

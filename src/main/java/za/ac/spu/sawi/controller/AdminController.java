
package za.ac.spu.sawi.controller;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import za.ac.spu.sawi.entity.User;
import za.ac.spu.sawi.entity.Appointment;
import za.ac.spu.sawi.dto.MonthlyConsultationSummary;
import za.ac.spu.sawi.repository.AppointmentRepository;
import za.ac.spu.sawi.repository.UserRepository;
import za.ac.spu.sawi.service.AdminUserService;
import za.ac.spu.sawi.service.EmailService;
import za.ac.spu.sawi.service.PasswordResetService;
import za.ac.spu.sawi.service.StaffAccountService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final StaffAccountService staffAccountService;
    private final AdminUserService adminUserService;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final EmailService emailService;
    private final PasswordResetService passwordResetService;

    public AdminController(
            StaffAccountService staffAccountService,
            AdminUserService adminUserService,
            UserRepository userRepository,
            AppointmentRepository appointmentRepository,
            EmailService emailService,
            PasswordResetService passwordResetService) {
        this.staffAccountService = staffAccountService;
        this.adminUserService = adminUserService;
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
        this.emailService = emailService;
        this.passwordResetService = passwordResetService;
    }

    @GetMapping({"", "/", "/dashboard"})
    public String showDashboard(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("emailDeliveryConfigured", emailService.isConfigured());
        model.addAttribute("pendingStudents",
            userRepository.findByRole_RoleNameAndActiveFalseOrderByUserIdAsc("STUDENT"));
        model.addAttribute("pendingPasswordResets", passwordResetService.pendingRequests());
        YearMonth month = YearMonth.now();
        LocalDateTime monthStart = month.atDay(1).atStartOfDay();
        LocalDateTime monthEnd = month.plusMonths(1).atDay(1)
            .atStartOfDay().minusNanos(1);
        var completedConsultations = appointmentRepository
            .findByStatusAndCompletedAtBetween(
                "COMPLETED", monthStart, monthEnd);
        Map<String, Set<Integer>> studentsByStaff = new LinkedHashMap<>();
        Map<String, Long> consultationsByStaff = new LinkedHashMap<>();
        for (Appointment appointment : completedConsultations) {
            String role;
            String name;
            if (appointment.getCounselor() != null) {
            role = "Counsellor";
            name = appointment.getCounselor().getUser().getFirstName()
                + " " + appointment.getCounselor().getUser().getLastName();
            } else if (appointment.getAdvisor() != null) {
            role = "Advisor";
            name = appointment.getAdvisor().getUser().getFirstName()
                + " " + appointment.getAdvisor().getUser().getLastName();
            } else {
            continue;
            }
            String key = role + ": " + name;
                studentsByStaff.computeIfAbsent(key, ignored -> new HashSet<>())
                    .add(appointment.getStudent().getStudentId());
                consultationsByStaff.merge(key, 1L, Long::sum);
        }
            var consultationSummaries = new ArrayList<MonthlyConsultationSummary>();
            studentsByStaff.forEach((key, studentIds) -> {
                int separator = key.indexOf(": ");
                String role = key.substring(0, separator);
                String name = key.substring(separator + 2);
                consultationSummaries.add(new MonthlyConsultationSummary(
                    name,
                    role,
                    studentIds.size(),
                    consultationsByStaff.get(key)));
            });
        consultationSummaries.sort(Comparator.comparingLong(
                MonthlyConsultationSummary::studentsConsulted).reversed());
        model.addAttribute("consultationMonth", month.toString());
            long uniqueMonthlyStudents = completedConsultations.stream()
                .map(appointment -> appointment.getStudent().getStudentId())
                .distinct()
                .count();
            model.addAttribute("totalMonthlyConsultations", uniqueMonthlyStudents);
            model.addAttribute("completedConsultationCount", completedConsultations.size());
        model.addAttribute("monthlyConsultations", consultationSummaries);
        model.addAttribute("completedConsultations", completedConsultations);
        return "admin-dashboard";
    }

    @GetMapping("/staff")
    public String showStaffForm(Model model) {
        model.addAttribute("pageTitle", "Create Staff Account");
        return "admin-staff";
    }

    @PostMapping("/staff")
    public String createStaffAccount(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam(required = false) String phone,
            @RequestParam String roleName,
            @RequestParam(required = false) String staffNumber,
            @RequestParam(required = false) String faculty,
            @RequestParam(required = false) String departmentOrSpecialization,
            RedirectAttributes redirectAttributes) {

        try {
            User user = staffAccountService.createStaffAccount(
                    username,
                    password,
                    firstName,
                    lastName,
                    email,
                    phone,
                    roleName,
                    staffNumber,
                    faculty,
                    departmentOrSpecialization
            );
                    boolean emailSent = emailService.sendStaffAccountCreated(user);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Staff account created successfully for "
                            + user.getFirstName() + " " + user.getLastName()
                            + (emailSent
                            ? ". A confirmation email was sent."
                            : ". Email could not be sent; check SMTP configuration and the staff email address.")
            );

            return "redirect:/admin/staff";

        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", ex.getMessage());
            return "redirect:/admin/staff";
        }
    }

    @PostMapping("/users/{userId}/status")
    public String updateUserStatus(
            @PathVariable Integer userId,
            @RequestParam boolean active,
            org.springframework.security.core.Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            User target = userRepository.findById(userId).orElse(null);
            boolean approvingStudent = active && target != null
                && !target.isActive()
                && target.getRole().getRoleName().equalsIgnoreCase("STUDENT");
                boolean studentApproved = adminUserService.setActive(
                    userId, active, authentication.getName());
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    studentApproved
                    ? "Student approved and approval email sent."
                    : approvingStudent
                        ? "Student approved, but email could not be sent. Configure SMTP and verify the student email address."
                        : active ? "Account access restored." : "Account access removed.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/password-resets/{requestId}/approve")
    public String approvePasswordReset(
            @PathVariable Integer requestId,
            RedirectAttributes redirectAttributes) {
        try {
            boolean sent = passwordResetService.approve(requestId);
            redirectAttributes.addFlashAttribute("successMessage",
                    sent ? "Password-reset approval email sent."
                            : "Approval email could not be sent; configure SMTP and check the email address. The request remains pending.");
        } catch (IllegalArgumentException | IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/admin/dashboard";
    }
}

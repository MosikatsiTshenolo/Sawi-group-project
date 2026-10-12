
package za.ac.spu.sawi.controller;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import za.ac.spu.sawi.entity.Appointment;
import za.ac.spu.sawi.entity.User;
import za.ac.spu.sawi.entity.Advisor;
import za.ac.spu.sawi.entity.Counselor;
import za.ac.spu.sawi.entity.Student;
import za.ac.spu.sawi.repository.StudentRepository;
import za.ac.spu.sawi.repository.UserRepository;
import za.ac.spu.sawi.repository.AdvisorRepository;
import za.ac.spu.sawi.repository.CounselorRepository;
import za.ac.spu.sawi.service.AppointmentService;
import za.ac.spu.sawi.service.EmailService;
import za.ac.spu.sawi.service.NotificationService;

@Controller
public class StaffAppointmentController {

    private final AppointmentService appointmentService;
    private final UserRepository userRepository;
    private final AdvisorRepository advisorRepository;
    private final CounselorRepository counselorRepository;
        private final StudentRepository studentRepository;
        private final NotificationService notificationService;
        private final EmailService emailService;

    public StaffAppointmentController(
            AppointmentService appointmentService,
            UserRepository userRepository,
            AdvisorRepository advisorRepository,
            CounselorRepository counselorRepository,
            StudentRepository studentRepository,
            NotificationService notificationService,
            EmailService emailService) {

        this.appointmentService = appointmentService;
        this.userRepository = userRepository;
        this.advisorRepository = advisorRepository;
        this.counselorRepository = counselorRepository;
        this.studentRepository = studentRepository;
        this.notificationService = notificationService;
        this.emailService = emailService;
    }

    @GetMapping("/advisor/appointments")
    public String advisorAppointments(
            Authentication authentication,
            Model model) {

        User user = userRepository
                .findByUsername(authentication.getName())
                .orElse(null);

        if (user == null) {
            model.addAttribute("errorMessage",
                    "Your user account could not be found.");
            model.addAttribute("appointments", List.of());
            model.addAttribute("staffType", "Academic Advisor");
            return "staff-appointments";
        }

        Advisor advisor = advisorRepository
                .findByUser_UserId(user.getUserId())
                .orElse(null);

        if (advisor == null) {
            model.addAttribute("errorMessage",
                    "Your advisor profile is not set up yet. Please contact the administrator.");
            model.addAttribute("appointments", List.of());
            model.addAttribute("staffType", "Academic Advisor");
            return "staff-appointments";
        }

        List<Appointment> appointments = appointmentService.findAll()
                .stream()
                .filter(a -> "Academic Advising".equals(a.getAppointmentType())
                        || "Emergency Academic Advising".equals(a.getAppointmentType()))
                .filter(a ->
                        ("PENDING".equals(a.getStatus())
                                && a.getAdvisor() == null)
                        || (a.getAdvisor() != null
                                && Objects.equals(
                                        a.getAdvisor().getAdvisorId(),
                                        advisor.getAdvisorId())))
                .collect(Collectors.toList());

        model.addAttribute("appointments", appointments);
        model.addAttribute("staffType", "Academic Advisor");
        return "staff-appointments";
    }

    @GetMapping("/counselor/appointments")
    public String counselorAppointments(
            Authentication authentication,
            Model model) {

        User user = userRepository
                .findByUsername(authentication.getName())
                .orElse(null);

        if (user == null) {
            model.addAttribute("errorMessage",
                    "Your user account could not be found.");
            model.addAttribute("appointments", List.of());
            model.addAttribute("staffType", "Counsellor");
            return "staff-appointments";
        }

        Counselor counselor = counselorRepository
                .findByUser_UserId(user.getUserId())
                .orElse(null);

        if (counselor == null) {
            model.addAttribute("errorMessage",
                    "Your counsellor profile is not set up yet. Please contact the administrator.");
            model.addAttribute("appointments", List.of());
            model.addAttribute("staffType", "Counsellor");
            return "staff-appointments";
        }

        List<Appointment> appointments = appointmentService.findAll()
                .stream()
                .filter(a -> "Counselling".equals(a.getAppointmentType())
                        || "Emergency Counselling".equals(a.getAppointmentType()))
                .filter(a ->
                        ("PENDING".equals(a.getStatus())
                                && a.getCounselor() == null)
                        || (a.getCounselor() != null
                                && Objects.equals(
                                        a.getCounselor().getCounselorId(),
                                        counselor.getCounselorId())))
                .collect(Collectors.toList());

        model.addAttribute("appointments", appointments);
        model.addAttribute("staffType", "Counsellor");
        return "staff-appointments";
    }

    @PostMapping("/advisor/appointments/{id}/review")
    public String reviewAdvisorAppointment(
            @PathVariable("id") Integer id,
            @org.springframework.web.bind.annotation.RequestParam String decision,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        User user = userRepository
                .findByUsername(authentication.getName())
                .orElse(null);

        Advisor advisor = user == null ? null
                : advisorRepository.findByUser_UserId(user.getUserId())
                        .orElse(null);

        if (advisor == null) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Your advisor profile could not be found.");
            return "redirect:/advisor/appointments";
        }

        Appointment appointment = appointmentService.findById(id)
                .orElse(null);

        if (appointment == null
                || !"Academic Advising".equals(appointment.getAppointmentType())
                || !"PENDING".equals(appointment.getStatus())
                || (appointment.getAdvisor() != null
                    && !Objects.equals(
                        appointment.getAdvisor().getAdvisorId(),
                        advisor.getAdvisorId()))) {

            redirectAttributes.addFlashAttribute("errorMessage",
                    "This appointment cannot be reviewed by you.");
            return "redirect:/advisor/appointments";
        }

        if ("APPROVE".equals(decision)) {
            appointment.setStatus("APPROVED");
            appointment.setAdvisor(advisor);
        } else if ("REJECT".equals(decision)) {
            appointment.setStatus("REJECTED");
            appointment.setAdvisor(advisor);
        } else {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Invalid appointment decision.");
            return "redirect:/advisor/appointments";
        }

        appointmentService.save(appointment);
        redirectAttributes.addFlashAttribute("successMessage",
                "Appointment request " + decision.toLowerCase() + "d successfully.");

        return "redirect:/advisor/appointments";
    }

    @PostMapping("/counselor/appointments/{id}/review")
    public String reviewCounselorAppointment(
            @PathVariable("id") Integer id,
            @org.springframework.web.bind.annotation.RequestParam String decision,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        User user = userRepository
                .findByUsername(authentication.getName())
                .orElse(null);

        Counselor counselor = user == null ? null
                : counselorRepository.findByUser_UserId(user.getUserId())
                        .orElse(null);

        if (counselor == null) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Your counsellor profile could not be found.");
            return "redirect:/counselor/appointments";
        }

        Appointment appointment = appointmentService.findById(id)
                .orElse(null);

        if (appointment == null
                || !"Counselling".equals(appointment.getAppointmentType())
                || !"PENDING".equals(appointment.getStatus())
                || (appointment.getCounselor() != null
                    && !Objects.equals(
                        appointment.getCounselor().getCounselorId(),
                        counselor.getCounselorId()))) {

            redirectAttributes.addFlashAttribute("errorMessage",
                    "This appointment cannot be reviewed by you.");
            return "redirect:/counselor/appointments";
        }

        if ("APPROVE".equals(decision)) {
            appointment.setStatus("APPROVED");
            appointment.setCounselor(counselor);
        } else if ("REJECT".equals(decision)) {
            appointment.setStatus("REJECTED");
            appointment.setCounselor(counselor);
        } else {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Invalid appointment decision.");
            return "redirect:/counselor/appointments";
        }

        appointmentService.save(appointment);
        redirectAttributes.addFlashAttribute("successMessage",
                "Appointment request " + decision.toLowerCase() + "d successfully.");

        return "redirect:/counselor/appointments";
    }

    @PostMapping("/counselor/students/{studentId}/emergency")
    public String requestCounselorEmergencyAppointment(
            @PathVariable Integer studentId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
                try {
                        requestEmergencyAppointment(studentId, authentication, false,
                                        redirectAttributes);
                } catch (IllegalArgumentException | IllegalStateException exception) {
                        redirectAttributes.addFlashAttribute("errorMessage",
                                        exception.getMessage());
                }
        return "redirect:/counselor/dashboard";
    }

    @PostMapping("/advisor/students/{studentId}/emergency")
    public String requestAdvisorEmergencyAppointment(
            @PathVariable Integer studentId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            requestEmergencyAppointment(studentId, authentication, true,
                    redirectAttributes);
        } catch (IllegalArgumentException | IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    exception.getMessage());
        }
        return "redirect:/advisor/dashboard";
    }

    @PostMapping("/counselor/appointments/{id}/complete")
    public String completeCounselorConsultation(
            @PathVariable Integer id,
            @RequestParam String consultationReport,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        completeConsultation(id, consultationReport, authentication, false,
                redirectAttributes);
        return "redirect:/counselor/appointments";
    }

    @PostMapping("/advisor/appointments/{id}/complete")
    public String completeAdvisorConsultation(
            @PathVariable Integer id,
            @RequestParam String consultationReport,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        completeConsultation(id, consultationReport, authentication, true,
                redirectAttributes);
        return "redirect:/advisor/appointments";
    }

    private void requestEmergencyAppointment(
            Integer studentId,
            Authentication authentication,
            boolean academicAdvisor,
            RedirectAttributes redirectAttributes) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Student profile could not be found."));
        Appointment appointment = new Appointment();
        appointment.setStudent(student);
        appointment.setAppointmentDate(LocalDateTime.now());
        appointment.setStatus("EMERGENCY_REQUESTED");
        appointment.setAppointmentType(academicAdvisor
                ? "Emergency Academic Advising" : "Emergency Counselling");
        appointment.setNotes("Urgent follow-up requested after a high-risk check-in.");

        User staffUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException(
                        "Logged-in user was not found."));
        if (academicAdvisor) {
            Advisor advisor = advisorRepository.findByUser_UserId(staffUser.getUserId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Advisor profile could not be found."));
            String advisorFaculty = advisor.getFaculty() != null
                    ? advisor.getFaculty() : advisor.getDepartment();
            if (student.getFaculty() == null || advisorFaculty == null
                    || !student.getFaculty().equalsIgnoreCase(advisorFaculty)) {
                throw new IllegalArgumentException(
                        "You can only request an emergency appointment for a student in your faculty.");
            }
            appointment.setAdvisor(advisor);
        } else {
            Counselor counselor = counselorRepository
                    .findByUser_UserId(staffUser.getUserId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Counsellor profile could not be found."));
            appointment.setCounselor(counselor);
        }

                boolean duplicateRequest = appointmentService.findAll().stream()
                                .anyMatch(existing -> existing.getStudent().getStudentId()
                                                .equals(studentId)
                                                && existing.getStatus().equals("EMERGENCY_REQUESTED")
                                                && (academicAdvisor
                                                        ? existing.getAdvisor() != null
                                                                && existing.getAdvisor().getAdvisorId().equals(
                                                                                appointment.getAdvisor().getAdvisorId())
                                                        : existing.getCounselor() != null
                                                                && existing.getCounselor().getCounselorId().equals(
                                                                                appointment.getCounselor().getCounselorId())));
                if (duplicateRequest) {
                        redirectAttributes.addFlashAttribute("errorMessage",
                                        "An open emergency appointment already exists for this student.");
                        return;
                }

        Appointment savedAppointment = appointmentService.save(appointment);
        var notification = new za.ac.spu.sawi.entity.Notification();
        notification.setUser(student.getUser());
        notification.setSender(staffUser);
        notification.setRelatedAppointment(savedAppointment);
        notification.setTitle("Urgent appointment requested");
        notification.setMessage("A member of your support team has requested an urgent appointment. Please check your appointments or contact campus support.");
        notification.setNotificationType("EMERGENCY");
        notificationService.save(notification);
        boolean emailSent = emailService.sendStaffNotification(
                student.getUser(), staffUser,
                notification.getTitle(), notification.getMessage());
        redirectAttributes.addFlashAttribute("successMessage",
                "Emergency appointment request sent to "
                        + student.getUser().getFirstName()
                        + (emailSent ? ". Email sent." : ". In-app notification created; email unavailable."));
    }

    private void completeConsultation(
            Integer appointmentId,
            String consultationReport,
            Authentication authentication,
            boolean academicAdvisor,
            RedirectAttributes redirectAttributes) {
        if (consultationReport == null || consultationReport.isBlank()
                || consultationReport.trim().length() > 1000) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Enter a consultation report of up to 1000 characters.");
            return;
        }

        Appointment appointment = appointmentService.findById(appointmentId)
                .orElse(null);
        User user = userRepository.findByUsername(authentication.getName())
                .orElse(null);
        boolean assignedToUser = appointment != null && user != null
                && (academicAdvisor
                    ? appointment.getAdvisor() != null
                        && appointment.getAdvisor().getUser().getUserId().equals(user.getUserId())
                    : appointment.getCounselor() != null
                        && appointment.getCounselor().getUser().getUserId().equals(user.getUserId()));
        if (!assignedToUser || !("APPROVED".equals(appointment.getStatus())
                || "EMERGENCY_REQUESTED".equals(appointment.getStatus())
                || "STUDENT_CONFIRMED".equals(appointment.getStatus()))) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Only an appointment assigned to you can be marked complete.");
            return;
        }

        appointment.setStatus("COMPLETED");
        appointment.setConsultationReport(consultationReport.trim());
        appointment.setCompletedAt(LocalDateTime.now());
        appointmentService.save(appointment);
        redirectAttributes.addFlashAttribute("successMessage",
                "Consultation report saved and marked complete.");
    }
}

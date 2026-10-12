
package za.ac.spu.sawi.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import za.ac.spu.sawi.entity.Appointment;
import za.ac.spu.sawi.entity.Student;
import za.ac.spu.sawi.repository.StudentRepository;
import za.ac.spu.sawi.service.AppointmentService;

@Controller
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final StudentRepository studentRepository;

    public AppointmentController(
            AppointmentService appointmentService,
            StudentRepository studentRepository) {
        this.appointmentService = appointmentService;
        this.studentRepository = studentRepository;
    }

    @GetMapping("/appointments")
    public String showAppointments(
            Authentication authentication,
            Model model) {

        Student student = studentRepository
                .findByUser_Username(authentication.getName())
                .orElse(null);

        model.addAttribute("appointment", new Appointment());

        if (student == null) {
            model.addAttribute(
                    "errorMessage",
                    "Your student profile could not be found.");
            model.addAttribute("appointments", List.of());
            return "appointments";
        }

        List<Appointment> appointments =
                appointmentService.findByStudentId(
                        student.getStudentId());

        model.addAttribute("appointments", appointments);

        return "appointments";
    }

    @PostMapping("/appointments")
    public String requestAppointment(
            @ModelAttribute("appointment") Appointment appointment,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Student student = studentRepository
                .findByUser_Username(authentication.getName())
                .orElse(null);

        if (student == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Your student profile could not be found.");
            return "redirect:/appointments";
        }

        if (appointment.getAppointmentDate() == null
                || !appointment.getAppointmentDate()
                        .isAfter(LocalDateTime.now())) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Please select a future appointment date and time.");
            return "redirect:/appointments";
        }

        String type = appointment.getAppointmentType();

        if (type == null
                || (!type.equals("Academic Advising")
                && !type.equals("Counselling"))) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Please select Academic Advising or Counselling.");
            return "redirect:/appointments";
        }

        // The student cannot assign their own staff member or status.
        appointment.setAppointmentId(null);
        appointment.setStudent(student);
        appointment.setAdvisor(null);
        appointment.setCounselor(null);
        appointment.setStatus("PENDING");

        appointmentService.save(appointment);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Your appointment request has been submitted successfully.");

        return "redirect:/appointments";
    }
}

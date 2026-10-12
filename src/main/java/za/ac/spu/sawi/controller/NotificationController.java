package za.ac.spu.sawi.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import za.ac.spu.sawi.entity.User;
import za.ac.spu.sawi.entity.Appointment;
import za.ac.spu.sawi.entity.Notification;
import za.ac.spu.sawi.repository.UserRepository;
import za.ac.spu.sawi.service.AppointmentService;
import za.ac.spu.sawi.service.EmailService;
import za.ac.spu.sawi.service.NotificationService;

@Controller
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final AppointmentService appointmentService;
    private final EmailService emailService;

    public NotificationController(
            NotificationService notificationService,
            UserRepository userRepository,
            AppointmentService appointmentService,
            EmailService emailService) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
        this.appointmentService = appointmentService;
        this.emailService = emailService;
    }

    @GetMapping("/notifications")
    public String showNotifications(Authentication authentication, Model model) {
        User user = findCurrentUser(authentication);
        String role = user.getRole().getRoleName().toUpperCase();
        model.addAttribute("notifications",
                notificationService.findByUserId(user.getUserId()));
        model.addAttribute("unreadCount",
                notificationService.countUnreadByUserId(user.getUserId()));
        model.addAttribute("canBroadcast",
                role.equals("LECTURER") || role.equals("ADVISOR")
                        || role.equals("COUNSELOR") || role.equals("ADMIN"));
        return "notifications";
    }

    @PostMapping("/notifications/broadcast")
    public String broadcastToStudents(
            @RequestParam String title,
            @RequestParam String message,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            User sender = findCurrentUser(authentication);
                var broadcastResult = notificationService.broadcastToStudents(
                    sender, title, message);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Update saved for " + broadcastResult.recipients()
                        + " active student account(s); email sent to "
                        + broadcastResult.emailsSent() + ".");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/notifications";
    }

    @PostMapping("/notifications/{notificationId}/read")
    public String markAsRead(
            @PathVariable Integer notificationId,
            Authentication authentication) {
        User user = findCurrentUser(authentication);
        notificationService.markAsRead(notificationId, user.getUserId());
        return "redirect:/notifications";
    }

        @PostMapping("/notifications/{notificationId}/reply")
        public String replyToNotification(
            @PathVariable Integer notificationId,
            @RequestParam String message,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        User sender = findCurrentUser(authentication);
            Notification original;
            try {
                original = findRecipientNotification(notificationId, sender);
            } catch (IllegalArgumentException exception) {
                redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
                return "redirect:/notifications";
            }
        String replyText = message == null ? "" : message.trim();
        if (original.getSender() == null || replyText.isBlank()
            || replyText.length() > 500) {
            redirectAttributes.addFlashAttribute("errorMessage",
                "This item cannot be replied to, or the message is empty or too long.");
            return "redirect:/notifications";
        }

        Notification reply = new Notification();
        reply.setUser(original.getSender());
        reply.setSender(sender);
        reply.setTitle("Reply: " + original.getTitle());
        if (reply.getTitle().length() > 100) {
            reply.setTitle(reply.getTitle().substring(0, 100));
        }
        reply.setMessage(replyText);
        reply.setNotificationType(sender.getRole().getRoleName() + " reply");
        reply.setRelatedAppointment(original.getRelatedAppointment());
        notificationService.save(reply);
        emailService.sendStaffNotification(original.getSender(), sender,
            reply.getTitle(), replyText);
        notificationService.markAsRead(notificationId, sender.getUserId());
        redirectAttributes.addFlashAttribute("successMessage",
            "Your reply was sent.");
        return "redirect:/notifications";
        }

        @PostMapping("/notifications/{notificationId}/confirm-emergency")
        public String confirmEmergencyAttendance(
            @PathVariable Integer notificationId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        User student = findCurrentUser(authentication);
            Notification notification;
            try {
                notification = findRecipientNotification(notificationId, student);
            } catch (IllegalArgumentException exception) {
                redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
                return "redirect:/notifications";
            }
        Appointment appointment = notification.getRelatedAppointment();
        if (appointment == null
            || !"EMERGENCY".equals(notification.getNotificationType())
            || appointment.getStudent() == null
            || !appointment.getStudent().getUser().getUserId()
                .equals(student.getUserId())
            || !"EMERGENCY_REQUESTED".equals(appointment.getStatus())) {
            redirectAttributes.addFlashAttribute("errorMessage",
                "This emergency request can no longer be confirmed.");
            return "redirect:/notifications";
        }

        appointment.setStatus("STUDENT_CONFIRMED");
        appointmentService.save(appointment);
        notificationService.markAsRead(notificationId, student.getUserId());

        User staff = appointment.getCounselor() != null
            ? appointment.getCounselor().getUser()
            : appointment.getAdvisor() != null
                ? appointment.getAdvisor().getUser() : null;
        if (staff != null) {
            Notification confirmation = new Notification();
            confirmation.setUser(staff);
            confirmation.setSender(student);
            confirmation.setRelatedAppointment(appointment);
            confirmation.setTitle("Student confirmed emergency appointment");
            confirmation.setMessage(student.getFirstName() + " "
                + student.getLastName() + " confirmed they will attend.");
            confirmation.setNotificationType("EMERGENCY_REPLY");
            notificationService.save(confirmation);
                emailService.sendStaffNotification(staff, student,
                    confirmation.getTitle(), confirmation.getMessage());
        }

        redirectAttributes.addFlashAttribute("successMessage",
            "Your support team has been notified that you will attend.");
        return "redirect:/notifications";
        }

        private Notification findRecipientNotification(Integer notificationId, User recipient) {
        Notification notification = notificationService.findById(notificationId)
            .orElseThrow(() -> new IllegalArgumentException(
                "Notification could not be found."));
        if (!notification.getUser().getUserId().equals(recipient.getUserId())) {
            throw new IllegalArgumentException(
                "You cannot respond to another user's notification.");
        }
        return notification;
        }

    private User findCurrentUser(Authentication authentication) {
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException(
                        "Logged-in user was not found."));
    }
}
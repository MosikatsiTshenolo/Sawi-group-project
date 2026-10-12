package za.ac.spu.sawi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.spu.sawi.entity.Notification;
import za.ac.spu.sawi.entity.Student;
import za.ac.spu.sawi.entity.User;
import za.ac.spu.sawi.dto.BroadcastResult;
import za.ac.spu.sawi.repository.NotificationRepository;
import za.ac.spu.sawi.repository.StudentRepository;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final StudentRepository studentRepository;
    private final EmailService emailService;

    public NotificationService(
            NotificationRepository notificationRepository,
            StudentRepository studentRepository,
            EmailService emailService) {
        this.notificationRepository = notificationRepository;
        this.studentRepository = studentRepository;
        this.emailService = emailService;
    }

    public List<Notification> findAll() {
        return notificationRepository.findAll();
    }

    public Optional<Notification> findById(Integer notificationId) {
        return notificationRepository.findById(notificationId);
    }

    public List<Notification> findByUserId(Integer userId) {
        return notificationRepository
                .findByUser_UserIdOrderByCreatedAtDesc(userId);
    }

    public List<Notification> findUnreadByUserId(Integer userId) {
        return notificationRepository
                .findByUser_UserIdAndReadFalseOrderByCreatedAtDesc(userId);
    }

    public long countUnreadByUserId(Integer userId) {
        return notificationRepository
                .countByUser_UserIdAndReadFalse(userId);
    }

    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    public void deleteById(Integer notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    @Transactional
    public boolean markAsRead(Integer notificationId, Integer userId) {
        return notificationRepository
                .findByNotificationIdAndUser_UserId(notificationId, userId)
                .map(notification -> {
                    notification.setRead(true);
                    notificationRepository.save(notification);
                    return true;
                })
                .orElse(false);
    }

    @Transactional
    public BroadcastResult broadcastToStudents(User sender, String title, String message) {
        String safeTitle = title == null ? "" : title.trim();
        String safeMessage = message == null ? "" : message.trim();
        if (safeTitle.isBlank() || safeTitle.length() > 100
                || safeMessage.isBlank() || safeMessage.length() > 500) {
            throw new IllegalArgumentException(
                    "Enter a title of up to 100 characters and a message of up to 500 characters.");
        }

        String senderName = sender.getRole().getRoleName() + ": "
                + sender.getFirstName() + " " + sender.getLastName();
        String senderLabel = senderName.length() > 50
            ? senderName.substring(0, 50)
            : senderName;

        List<Notification> notifications = studentRepository.findAll().stream()
                .map(Student::getUser)
                .filter(User::isActive)
                .map(studentUser -> {
                    Notification notification = new Notification();
                    notification.setUser(studentUser);
                    notification.setSender(sender);
                    notification.setTitle(safeTitle);
                    notification.setMessage(safeMessage);
                    notification.setNotificationType(senderLabel);
                    return notification;
                })
                .toList();

        if (notifications.isEmpty()) {
            return new BroadcastResult(0, 0);
        }
        notificationRepository.saveAll(notifications);
        int emailsSent = (int) notifications.stream()
                .filter(notification -> emailService.sendStaffNotification(
                        notification.getUser(), sender, safeTitle, safeMessage))
                .count();
        return new BroadcastResult(notifications.size(), emailsSent);
    }
}
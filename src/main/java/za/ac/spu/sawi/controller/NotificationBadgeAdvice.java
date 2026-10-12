package za.ac.spu.sawi.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import za.ac.spu.sawi.repository.UserRepository;
import za.ac.spu.sawi.service.NotificationService;

@ControllerAdvice(annotations = Controller.class)
public class NotificationBadgeAdvice {

    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public NotificationBadgeAdvice(
            UserRepository userRepository,
            NotificationService notificationService) {
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @ModelAttribute
    public void addUnreadNotificationCount(
            Authentication authentication,
            Model model) {
        long unreadCount = 0;
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName())) {
            unreadCount = userRepository.findByUsername(authentication.getName())
                    .map(user -> notificationService.countUnreadByUserId(user.getUserId()))
                    .orElse(0L);
        }
        model.addAttribute("unreadNotificationCount", unreadCount);
    }
}
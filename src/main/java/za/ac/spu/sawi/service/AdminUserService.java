package za.ac.spu.sawi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import za.ac.spu.sawi.entity.User;
import za.ac.spu.sawi.repository.UserRepository;

@Service
public class AdminUserService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    public AdminUserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Transactional
    public boolean setActive(Integer userId, boolean active, String currentUsername) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "The selected account could not be found."));

        if (!active && user.getUsername().equals(currentUsername)) {
            throw new IllegalArgumentException(
                    "You cannot remove access to your own administrator account.");
        }

        if (!active && user.getRole().getRoleName().equalsIgnoreCase("ADMIN")
                && user.isActive()
                && userRepository.countByRole_RoleNameAndActiveTrue("ADMIN") <= 1) {
            throw new IllegalStateException(
                    "The last active administrator account cannot be disabled.");
        }

        boolean studentApproval = active && !user.isActive()
            && user.getRole().getRoleName().equalsIgnoreCase("STUDENT");
        user.setActive(active);
        userRepository.save(user);
        return studentApproval && emailService.sendStudentApproval(user);
    }
}
package za.ac.spu.sawi.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import za.ac.spu.sawi.entity.PasswordResetRequest;
import za.ac.spu.sawi.entity.User;
import za.ac.spu.sawi.repository.PasswordResetRequestRepository;
import za.ac.spu.sawi.repository.UserRepository;

@Service
public class PasswordResetService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final PasswordResetRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public PasswordResetService(
            PasswordResetRequestRepository requestRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional
    public void requestReset(String usernameOrEmail) {
        String identifier = usernameOrEmail == null ? "" : usernameOrEmail.trim();
        Optional<User> user = userRepository.findByUsername(identifier);
        if (user.isEmpty()) {
            user = userRepository.findByEmail(identifier);
        }
        if (user.isEmpty() || !user.get().isActive()
                || requestRepository.existsByUser_UserIdAndStatus(
                        user.get().getUserId(), "PENDING")) {
            return;
        }

        PasswordResetRequest request = new PasswordResetRequest();
        request.setUser(user.get());
        request.setStatus("PENDING");
        requestRepository.save(request);
    }

    @Transactional(readOnly = true)
    public List<PasswordResetRequest> pendingRequests() {
        return requestRepository.findByStatusOrderByRequestedAtAsc("PENDING");
    }

    @Transactional
    public boolean approve(Integer requestId) {
        PasswordResetRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Password-reset request could not be found."));
        if (!"PENDING".equals(request.getStatus()) || !request.getUser().isActive()) {
            throw new IllegalArgumentException(
                    "This password-reset request is no longer pending.");
        }

        byte[] randomToken = new byte[32];
        SECURE_RANDOM.nextBytes(randomToken);
        String token = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(randomToken);
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(30);
        request.setTokenHash(hash(token));
        request.setExpiresAt(expiresAt);
        request.setApprovedAt(LocalDateTime.now());
        request.setStatus("APPROVED");
        requestRepository.save(request);

        String resetUrl = baseUrl + "/reset-password?token=" + token;
        if (!emailService.sendPasswordResetApproval(request.getUser(), resetUrl)) {
            request.setTokenHash(null);
            request.setExpiresAt(null);
            request.setApprovedAt(null);
            request.setStatus("PENDING");
            requestRepository.save(request);
            return false;
        }
        return true;
    }

    @Transactional(readOnly = true)
    public boolean isTokenValid(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        return requestRepository.findByTokenHashAndStatus(hash(token), "APPROVED")
                .filter(request -> request.getExpiresAt() != null
                        && request.getExpiresAt().isAfter(LocalDateTime.now())
                        && request.getUser().isActive())
                .isPresent();
    }

    @Transactional
    public boolean completeReset(
            String token, String newPassword, String confirmPassword) {
        if (newPassword == null || newPassword.length() < 8
                || newPassword.length() > 100
                || !newPassword.equals(confirmPassword)) {
            return false;
        }
        PasswordResetRequest request = requestRepository
                .findByTokenHashAndStatus(hash(token), "APPROVED")
                .filter(reset -> reset.getExpiresAt() != null
                        && reset.getExpiresAt().isAfter(LocalDateTime.now())
                        && reset.getUser().isActive())
                .orElse(null);
        if (request == null) {
            return false;
        }

        User user = request.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        request.setStatus("COMPLETED");
        request.setTokenHash(null);
        request.setExpiresAt(null);
        requestRepository.save(request);
        return true;
    }

    private String hash(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available.", exception);
        }
    }
}
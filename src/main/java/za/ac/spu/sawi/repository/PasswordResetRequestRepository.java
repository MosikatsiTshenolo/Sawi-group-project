package za.ac.spu.sawi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import za.ac.spu.sawi.entity.PasswordResetRequest;

@Repository
public interface PasswordResetRequestRepository
        extends JpaRepository<PasswordResetRequest, Integer> {

    List<PasswordResetRequest> findByStatusOrderByRequestedAtAsc(String status);

    boolean existsByUser_UserIdAndStatus(Integer userId, String status);

    Optional<PasswordResetRequest> findByTokenHashAndStatus(
            String tokenHash, String status);
}
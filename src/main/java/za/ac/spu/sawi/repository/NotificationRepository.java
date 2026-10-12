package za.ac.spu.sawi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.spu.sawi.entity.Notification;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    List<Notification> findByUser_UserIdOrderByCreatedAtDesc(
            Integer userId);

    List<Notification> findByUser_UserIdAndReadFalseOrderByCreatedAtDesc(
            Integer userId);

    long countByUser_UserIdAndReadFalse(Integer userId);

        Optional<Notification> findByNotificationIdAndUser_UserId(
                        Integer notificationId,
                        Integer userId);
}
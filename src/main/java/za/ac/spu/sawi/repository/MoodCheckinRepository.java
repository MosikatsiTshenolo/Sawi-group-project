
package za.ac.spu.sawi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import za.ac.spu.sawi.entity.MoodCheckin;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MoodCheckinRepository
        extends JpaRepository<MoodCheckin, Integer> {

    List<MoodCheckin> findByStudent_StudentIdOrderByCheckinDateDesc(
            Integer studentId);

    List<MoodCheckin> findByStudent_StudentIdAndCheckinDateBetween(
            Integer studentId,
            LocalDateTime startDate,
            LocalDateTime endDate);

    List<MoodCheckin> findByMood(String mood);

    // Retrieve all wellness check-ins, newest first.
    List<MoodCheckin> findAllByOrderByCheckinDateDesc();
}

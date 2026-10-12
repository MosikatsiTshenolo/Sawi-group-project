package za.ac.spu.sawi.service;

import org.springframework.stereotype.Service;
import za.ac.spu.sawi.entity.MoodCheckin;
import za.ac.spu.sawi.repository.MoodCheckinRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MoodCheckinService {

    private final MoodCheckinRepository moodCheckinRepository;

    public MoodCheckinService(MoodCheckinRepository moodCheckinRepository) {
        this.moodCheckinRepository = moodCheckinRepository;
    }

    public List<MoodCheckin> findAll() {
        return moodCheckinRepository.findAll();
    }

    public Optional<MoodCheckin> findById(Integer checkinId) {
        return moodCheckinRepository.findById(checkinId);
    }

    public List<MoodCheckin> findByStudentId(Integer studentId) {
        return moodCheckinRepository
                .findByStudent_StudentIdOrderByCheckinDateDesc(studentId);
    }

    public List<MoodCheckin> findByStudentIdAndDateBetween(
            Integer studentId,
            LocalDateTime startDate,
            LocalDateTime endDate) {

        return moodCheckinRepository
                .findByStudent_StudentIdAndCheckinDateBetween(
                        studentId, startDate, endDate);
    }

    public List<MoodCheckin> findByMood(String mood) {
        return moodCheckinRepository.findByMood(mood);
    }

    public MoodCheckin save(MoodCheckin moodCheckin) {
        return moodCheckinRepository.save(moodCheckin);
    }

    public void deleteById(Integer checkinId) {
        moodCheckinRepository.deleteById(checkinId);
    }
}
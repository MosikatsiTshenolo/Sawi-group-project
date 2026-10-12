
package za.ac.spu.sawi.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import za.ac.spu.sawi.entity.MoodCheckin;
import za.ac.spu.sawi.repository.MoodCheckinRepository;

@Controller
public class CounselorController {

    private final MoodCheckinRepository moodCheckinRepository;

    public CounselorController(MoodCheckinRepository moodCheckinRepository) {
        this.moodCheckinRepository = moodCheckinRepository;
    }

    @GetMapping("/counselor/dashboard")
    public String counselorDashboard(Model model) {

        // Keep only the newest check-in for each student.
        Map<Integer, MoodCheckin> latestCheckins =
                new LinkedHashMap<>();

        List<MoodCheckin> checkins =
                moodCheckinRepository.findAllByOrderByCheckinDateDesc();

        for (MoodCheckin checkin : checkins) {
            if (checkin.getStudent() == null
                    || checkin.getStudent().getStudentId() == null) {
                continue;
            }

            Integer studentId = checkin.getStudent().getStudentId();
            latestCheckins.putIfAbsent(studentId, checkin);
        }

        List<MoodCheckin> highStressStudents = new ArrayList<>();
        List<MoodCheckin> lowerStressStudents = new ArrayList<>();

        for (MoodCheckin checkin : latestCheckins.values()) {
            Integer stressLevel = checkin.getStressLevel();

            if (stressLevel == null) {
                continue;
            }

            if (stressLevel >= 4 && stressLevel <= 5) {
                highStressStudents.add(checkin);
            } else if (stressLevel >= 1 && stressLevel <= 3) {
                lowerStressStudents.add(checkin);
            }
        }

        model.addAttribute("highStressStudents", highStressStudents);
        model.addAttribute("lowerStressStudents", lowerStressStudents);
        model.addAttribute("highStressCount", highStressStudents.size());
        model.addAttribute("lowerStressCount", lowerStressStudents.size());
        model.addAttribute("totalStudentsWithCheckins",
                highStressStudents.size() + lowerStressStudents.size());
        return "counselor-dashboard";
    }
}

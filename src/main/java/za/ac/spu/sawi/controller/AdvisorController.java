
package za.ac.spu.sawi.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import za.ac.spu.sawi.entity.MoodCheckin;
import za.ac.spu.sawi.entity.Advisor;
import za.ac.spu.sawi.entity.Student;
import za.ac.spu.sawi.repository.AdvisorRepository;
import za.ac.spu.sawi.repository.MoodCheckinRepository;
import za.ac.spu.sawi.repository.StudentRepository;

@Controller
public class AdvisorController {

    private final MoodCheckinRepository moodCheckinRepository;
    private final AdvisorRepository advisorRepository;
    private final StudentRepository studentRepository;

    public AdvisorController(
            MoodCheckinRepository moodCheckinRepository,
            AdvisorRepository advisorRepository,
            StudentRepository studentRepository) {
        this.moodCheckinRepository = moodCheckinRepository;
        this.advisorRepository = advisorRepository;
        this.studentRepository = studentRepository;
    }

    @GetMapping("/advisor/dashboard")
    @PreAuthorize("hasRole('ADVISOR')")
    public String advisorDashboard(Authentication authentication, Model model) {
        Advisor advisor = advisorRepository
            .findByUser_Username(authentication.getName())
            .orElseThrow(() -> new IllegalStateException(
                "No advisor profile found for this account."));
        String faculty = advisor.getFaculty() != null
                ? advisor.getFaculty() : advisor.getDepartment();
        List<Student> facultyStudents = faculty == null || faculty.isBlank()
                ? List.of()
                : studentRepository.findByFacultyIgnoreCase(faculty);
        Set<Integer> facultyStudentIds = facultyStudents.stream()
            .map(Student::getStudentId)
            .collect(Collectors.toSet());

        // Keep only the latest check-in for each student.
        Map<Integer, MoodCheckin> latestCheckins =
                new LinkedHashMap<>();

        List<MoodCheckin> checkins =
                moodCheckinRepository.findAllByOrderByCheckinDateDesc();

        for (MoodCheckin checkin : checkins) {
            if (checkin.getStudent() == null
                    || checkin.getStudent().getStudentId() == null
                    || !facultyStudentIds.contains(
                        checkin.getStudent().getStudentId())) {
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
        model.addAttribute("advisorFaculty", faculty);
        model.addAttribute("facultyStudents", facultyStudents);

        return "advisor-dashboard";
    }
}

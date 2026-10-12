
package za.ac.spu.sawi.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import za.ac.spu.sawi.entity.MoodCheckin;
import za.ac.spu.sawi.entity.Student;
import za.ac.spu.sawi.repository.MoodCheckinRepository;
import za.ac.spu.sawi.repository.StudentRepository;

@Controller
public class MoodCheckinController {

    private final MoodCheckinRepository moodCheckinRepository;
    private final StudentRepository studentRepository;

    public MoodCheckinController(
            MoodCheckinRepository moodCheckinRepository,
            StudentRepository studentRepository) {
        this.moodCheckinRepository = moodCheckinRepository;
        this.studentRepository = studentRepository;
    }

    @GetMapping("/mood")
    public String showMoodCheckin(
            Authentication authentication,
            Model model) {

        String username = authentication.getName();

        Student student = studentRepository
                .findByUser_Username(username)
                .orElse(null);

        model.addAttribute("moodCheckin", new MoodCheckin());

        if (student == null) {
            model.addAttribute(
                    "message",
                    "Your student profile has not been created yet.");
            model.addAttribute("checkins", List.of());
            return "mood";
        }

        List<MoodCheckin> checkins =
                moodCheckinRepository
                        .findByStudent_StudentIdOrderByCheckinDateDesc(
                                student.getStudentId());

        model.addAttribute("checkins", checkins);

        return "mood";
    }

    @PostMapping("/mood")
    public String saveMoodCheckin(
            @ModelAttribute("moodCheckin") MoodCheckin moodCheckin,
            Authentication authentication) {

        String username = authentication.getName();

        Student student = studentRepository
                .findByUser_Username(username)
                .orElse(null);

        if (student == null) {
            return "redirect:/mood?error=no-profile";
        }

        moodCheckin.setStudent(student);
        moodCheckin.setCheckinId(null);
        moodCheckin.setCheckinDate(null);

        moodCheckinRepository.save(moodCheckin);

        return "redirect:/mood?success";
    }
}

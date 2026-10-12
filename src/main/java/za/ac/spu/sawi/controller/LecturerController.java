
package za.ac.spu.sawi.controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import za.ac.spu.sawi.entity.Lecturer;
import za.ac.spu.sawi.entity.Assessment;
import za.ac.spu.sawi.dto.AssessmentProgress;
import za.ac.spu.sawi.repository.AssessmentRepository;
import za.ac.spu.sawi.repository.GradeRepository;
import za.ac.spu.sawi.repository.LecturerRepository;
import za.ac.spu.sawi.repository.ModuleRepository;
import za.ac.spu.sawi.repository.StudentModuleRepository;

@Controller
@RequestMapping("/lecturer")
public class LecturerController {

    private final LecturerRepository lecturerRepository;
    private final ModuleRepository moduleRepository;
    private final AssessmentRepository assessmentRepository;
    private final StudentModuleRepository studentModuleRepository;
    private final GradeRepository gradeRepository;

    public LecturerController(
            LecturerRepository lecturerRepository,
            ModuleRepository moduleRepository,
            AssessmentRepository assessmentRepository,
            StudentModuleRepository studentModuleRepository,
            GradeRepository gradeRepository) {
        this.lecturerRepository = lecturerRepository;
        this.moduleRepository = moduleRepository;
        this.assessmentRepository = assessmentRepository;
        this.studentModuleRepository = studentModuleRepository;
        this.gradeRepository = gradeRepository;
    }

    @GetMapping("/dashboard")
    public String showDashboard(
            Authentication authentication,
            Model model) {

        String username = authentication.getName();

        Lecturer lecturer = lecturerRepository
                .findByUser_Username(username)
                .orElseThrow(() -> new IllegalStateException(
                        "No lecturer profile found for this account."));

        model.addAttribute("pageTitle", "Lecturer Dashboard");
        model.addAttribute("lecturer", lecturer);
        List<za.ac.spu.sawi.entity.Module> modules =
            moduleRepository.findDistinctByLecturers_LecturerId(
                lecturer.getLecturerId());
        List<Assessment> assessments = assessmentRepository
            .findDistinctByModule_Lecturers_LecturerId(lecturer.getLecturerId());
        List<AssessmentProgress> assessmentProgress = assessments.stream()
            .map(assessment -> {
                long enrolled = studentModuleRepository
                    .findByModule_ModuleId(assessment.getModule().getModuleId())
                    .size();
                long submitted = gradeRepository
                    .countByAssessment_AssessmentIdAndSubmittedTrue(
                        assessment.getAssessmentId());
                return new AssessmentProgress(
                    assessment,
                    enrolled,
                    submitted,
                    Math.max(0, enrolled - submitted),
                    ChronoUnit.DAYS.between(
                        LocalDate.now(), assessment.getDueDate()));
            })
            .toList();

        model.addAttribute("modules", modules);
        model.addAttribute("assessmentProgress", assessmentProgress);

        return "lecturer-dashboard";
    }
}

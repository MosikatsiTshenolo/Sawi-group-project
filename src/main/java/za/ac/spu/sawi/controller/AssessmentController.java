
package za.ac.spu.sawi.controller;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import za.ac.spu.sawi.entity.Assessment;
import za.ac.spu.sawi.entity.Student;
import za.ac.spu.sawi.entity.StudentModule;
import za.ac.spu.sawi.entity.Grade;
import za.ac.spu.sawi.repository.AssessmentRepository;
import za.ac.spu.sawi.repository.StudentModuleRepository;
import za.ac.spu.sawi.repository.StudentRepository;
import za.ac.spu.sawi.repository.GradeRepository;

@Controller
public class AssessmentController {

    private final AssessmentRepository assessmentRepository;
    private final StudentModuleRepository studentModuleRepository;
    private final StudentRepository studentRepository;
    private final GradeRepository gradeRepository;

    public AssessmentController(
            AssessmentRepository assessmentRepository,
            StudentModuleRepository studentModuleRepository,
            StudentRepository studentRepository,
            GradeRepository gradeRepository) {
        this.assessmentRepository = assessmentRepository;
        this.studentModuleRepository = studentModuleRepository;
        this.studentRepository = studentRepository;
        this.gradeRepository = gradeRepository;
    }

    @GetMapping("/assessments")
    public String assessments(
            Authentication authentication,
            Model model) {

        String username = authentication.getName();

        Student student = studentRepository
                .findByUser_Username(username)
                .orElse(null);

        List<Assessment> assessments = new ArrayList<>();
        Map<Integer, Grade> gradesByAssessment = new HashMap<>();
        Map<Integer, Long> daysRemainingByAssessment = new HashMap<>();

        if (student != null) {
            List<StudentModule> studentModules =
                    studentModuleRepository
                            .findByStudent_StudentId(student.getStudentId());

            for (StudentModule studentModule : studentModules) {
                List<Assessment> moduleAssessments =
                        assessmentRepository.findByModule_ModuleId(
                                studentModule.getModule().getModuleId());

                for (Assessment assessment : moduleAssessments) {
                    assessments.add(assessment);
                    daysRemainingByAssessment.put(
                            assessment.getAssessmentId(),
                            ChronoUnit.DAYS.between(
                                    LocalDate.now(), assessment.getDueDate()));

                    gradeRepository
                            .findByStudent_StudentIdAndAssessment_AssessmentId(
                                    student.getStudentId(),
                                    assessment.getAssessmentId())
                            .ifPresent(grade ->
                                    gradesByAssessment.put(
                                            assessment.getAssessmentId(),
                                            grade));
                }
            }
        }

        model.addAttribute("assessments", assessments);
        model.addAttribute("gradesByAssessment", gradesByAssessment);
        model.addAttribute("daysRemainingByAssessment", daysRemainingByAssessment);

        return "assessments";
    }

    @PostMapping("/assessments/{assessmentId}/submission")
    public String updateSubmissionStatus(
            @PathVariable Integer assessmentId,
            @RequestParam(required = false) Boolean submitted,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        Student student = studentRepository
                .findByUser_Username(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException(
                        "A student profile is required to update submissions."));
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "The selected assessment could not be found."));

        boolean enrolled = studentModuleRepository
                .findByStudent_StudentId(student.getStudentId()).stream()
                .anyMatch(studentModule -> studentModule.getModule().getModuleId()
                        .equals(assessment.getModule().getModuleId()));
        if (!enrolled) {
            throw new IllegalArgumentException(
                    "You can only update submissions for your enrolled modules.");
        }

        boolean isSubmitted = Boolean.TRUE.equals(submitted);
        Grade grade = gradeRepository
                .findByStudent_StudentIdAndAssessment_AssessmentId(
                        student.getStudentId(), assessmentId)
                .orElseGet(Grade::new);
        grade.setStudent(student);
        grade.setAssessment(assessment);
        grade.setSubmitted(isSubmitted);
        grade.setSubmittedAt(isSubmitted ? LocalDateTime.now() : null);
        gradeRepository.save(grade);

        redirectAttributes.addFlashAttribute("submissionMessage",
                isSubmitted ? "Submission status updated." : "Submission status cleared.");
        return "redirect:/assessments";
    }
}

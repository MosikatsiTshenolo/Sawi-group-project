
package za.ac.spu.sawi.controller;

import java.time.LocalDate;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import za.ac.spu.sawi.entity.Assessment;
import za.ac.spu.sawi.entity.Lecturer;
import za.ac.spu.sawi.entity.Module;
import za.ac.spu.sawi.repository.AssessmentRepository;
import za.ac.spu.sawi.repository.GradeRepository;
import za.ac.spu.sawi.repository.LecturerRepository;
import za.ac.spu.sawi.repository.ModuleRepository;

@Controller
@RequestMapping("/lecturer/assessments")
public class LecturerAssessmentController {

    private final AssessmentRepository assessmentRepository;
    private final LecturerRepository lecturerRepository;
    private final ModuleRepository moduleRepository;
        private final GradeRepository gradeRepository;

    public LecturerAssessmentController(
            AssessmentRepository assessmentRepository,
            LecturerRepository lecturerRepository,
            ModuleRepository moduleRepository,
            GradeRepository gradeRepository) {

        this.assessmentRepository = assessmentRepository;
        this.lecturerRepository = lecturerRepository;
        this.moduleRepository = moduleRepository;
        this.gradeRepository = gradeRepository;
    }

    @GetMapping("/new")
    public String showAssessmentForm(
            Authentication authentication,
            Model model) {

        Lecturer lecturer = lecturerRepository
                .findByUser_Username(authentication.getName())
                .orElseThrow(() -> new IllegalStateException(
                        "No lecturer profile found for this account."));

        model.addAttribute("modules",
                moduleRepository.findDistinctByLecturers_LecturerId(
                        lecturer.getLecturerId()));

        model.addAttribute("pageTitle", "Add Assessment");
                model.addAttribute("assessment", new Assessment());

        return "lecturer-assessment-form";
    }

        @GetMapping("/{assessmentId}/edit")
        public String editAssessment(
                        @PathVariable Integer assessmentId,
                        Authentication authentication,
                        Model model) {
                Lecturer lecturer = findLecturer(authentication);
                Assessment assessment = findOwnedAssessment(assessmentId, lecturer);
                model.addAttribute("modules", assignedModules(lecturer));
                model.addAttribute("assessment", assessment);
                model.addAttribute("pageTitle", "Edit Assessment");
                return "lecturer-assessment-form";
        }

    @PostMapping("/save")
    public String saveAssessment(
            Authentication authentication,
            @RequestParam(required = false) Integer assessmentId,
            @RequestParam Integer moduleId,
            @RequestParam String assessmentName,
            @RequestParam String assessmentType,
            @RequestParam(required = false) String description,
            @RequestParam LocalDate dueDate,
            @RequestParam(required = false) Double weight,
            RedirectAttributes redirectAttributes) {

        Lecturer lecturer = findLecturer(authentication);

        Module module = assignedModules(lecturer)
                .stream()
                .filter(m -> m.getModuleId().equals(moduleId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "You can only add assessments to your assigned modules."));

        if (assessmentName == null || assessmentName.isBlank()
                || assessmentType == null || assessmentType.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Assessment name and type are required.");
            return assessmentFormRedirect(assessmentId);
        }

        if (dueDate.isBefore(LocalDate.now())) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "The due date cannot be in the past.");
            return assessmentFormRedirect(assessmentId);
        }

        if (weight != null && (weight < 0 || weight > 100)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Assessment weight must be between 0 and 100.");
                        return assessmentFormRedirect(assessmentId);
        }

                Assessment assessment = assessmentId == null
                                ? new Assessment()
                                : findOwnedAssessment(assessmentId, lecturer);
        assessment.setModule(module);
        assessment.setAssessmentName(assessmentName.trim());
        assessment.setAssessmentType(assessmentType.trim());
        assessment.setDescription(description);
        assessment.setDueDate(dueDate);
        assessment.setWeight(weight);

        assessmentRepository.save(assessment);

        redirectAttributes.addFlashAttribute("successMessage",
                assessmentId == null
                        ? "Assessment added successfully."
                        : "Assessment updated successfully.");

        return "redirect:/lecturer/dashboard";
    }

    @PostMapping("/{assessmentId}/delete")
    public String deleteAssessment(
            @PathVariable Integer assessmentId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        Lecturer lecturer = findLecturer(authentication);
        Assessment assessment = findOwnedAssessment(assessmentId, lecturer);
        if (gradeRepository.existsByAssessment_AssessmentId(assessmentId)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "This assessment has submission or grade records and cannot be deleted.");
            return "redirect:/lecturer/dashboard";
        }
        assessmentRepository.delete(assessment);
        redirectAttributes.addFlashAttribute("successMessage",
                "Assessment deleted successfully.");
        return "redirect:/lecturer/dashboard";
    }

    private Lecturer findLecturer(Authentication authentication) {
        return lecturerRepository.findByUser_Username(authentication.getName())
                .orElseThrow(() -> new IllegalStateException(
                        "No lecturer profile found for this account."));
    }

    private java.util.List<Module> assignedModules(Lecturer lecturer) {
        return moduleRepository.findDistinctByLecturers_LecturerId(
                lecturer.getLecturerId());
    }

    private Assessment findOwnedAssessment(Integer assessmentId, Lecturer lecturer) {
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Assessment could not be found."));
        boolean assigned = assignedModules(lecturer).stream()
                .anyMatch(module -> module.getModuleId().equals(
                        assessment.getModule().getModuleId()));
        if (!assigned) {
            throw new IllegalArgumentException(
                    "You can only manage assessments for your assigned modules.");
        }
        return assessment;
    }

    private String assessmentFormRedirect(Integer assessmentId) {
        return assessmentId == null
                ? "redirect:/lecturer/assessments/new"
                : "redirect:/lecturer/assessments/" + assessmentId + "/edit";
    }
}

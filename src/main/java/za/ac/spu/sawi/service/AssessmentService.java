package za.ac.spu.sawi.service;

import org.springframework.stereotype.Service;
import za.ac.spu.sawi.entity.Assessment;
import za.ac.spu.sawi.repository.AssessmentRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AssessmentService {

    private final AssessmentRepository assessmentRepository;

    public AssessmentService(AssessmentRepository assessmentRepository) {
        this.assessmentRepository = assessmentRepository;
    }

    public List<Assessment> findAll() {
        return assessmentRepository.findAll();
    }

    public Optional<Assessment> findById(Integer assessmentId) {
        return assessmentRepository.findById(assessmentId);
    }

    public List<Assessment> findByModuleId(Integer moduleId) {
        return assessmentRepository.findByModule_ModuleId(moduleId);
    }

    public List<Assessment> findByDueDateBetween(
            LocalDate startDate, LocalDate endDate) {

        return assessmentRepository.findByDueDateBetween(
                startDate, endDate);
    }

    public List<Assessment> findByModuleIdAndDueDateBetween(
            Integer moduleId,
            LocalDate startDate,
            LocalDate endDate) {

        return assessmentRepository.findByModule_ModuleIdAndDueDateBetween(
                moduleId, startDate, endDate);
    }

    public Assessment save(Assessment assessment) {
        return assessmentRepository.save(assessment);
    }

    public void deleteById(Integer assessmentId) {
        assessmentRepository.deleteById(assessmentId);
    }
}
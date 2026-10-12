package za.ac.spu.sawi.service;

import org.springframework.stereotype.Service;
import za.ac.spu.sawi.entity.Grade;
import za.ac.spu.sawi.repository.GradeRepository;

import java.util.List;
import java.util.Optional;

@Service
public class GradeService {

    private final GradeRepository gradeRepository;

    public GradeService(GradeRepository gradeRepository) {
        this.gradeRepository = gradeRepository;
    }

    public List<Grade> findAll() {
        return gradeRepository.findAll();
    }

    public Optional<Grade> findById(Integer gradeId) {
        return gradeRepository.findById(gradeId);
    }

    public List<Grade> findByStudentId(Integer studentId) {
        return gradeRepository.findByStudent_StudentId(studentId);
    }

    public List<Grade> findByAssessmentId(Integer assessmentId) {
        return gradeRepository.findByAssessment_AssessmentId(assessmentId);
    }

    public Optional<Grade> findByStudentIdAndAssessmentId(
            Integer studentId, Integer assessmentId) {

        return gradeRepository.findByStudent_StudentIdAndAssessment_AssessmentId(
                studentId, assessmentId);
    }

    public Grade save(Grade grade) {
        return gradeRepository.save(grade);
    }

    public void deleteById(Integer gradeId) {
        gradeRepository.deleteById(gradeId);
    }
}
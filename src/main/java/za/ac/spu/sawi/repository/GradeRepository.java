package za.ac.spu.sawi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.spu.sawi.entity.Grade;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Integer> {

    List<Grade> findByStudent_StudentId(Integer studentId);

    List<Grade> findByAssessment_AssessmentId(Integer assessmentId);

    Optional<Grade> findByStudent_StudentIdAndAssessment_AssessmentId(
            Integer studentId,
            Integer assessmentId);

    long countByAssessment_AssessmentIdAndSubmittedTrue(Integer assessmentId);

    boolean existsByAssessment_AssessmentId(Integer assessmentId);
}
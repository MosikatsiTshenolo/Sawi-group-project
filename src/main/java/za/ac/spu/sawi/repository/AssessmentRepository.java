
package za.ac.spu.sawi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.spu.sawi.entity.Assessment;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AssessmentRepository
        extends JpaRepository<Assessment, Integer> {

    List<Assessment> findByModule_ModuleId(Integer moduleId);

    List<Assessment> findByDueDateBetween(
            LocalDate startDate,
            LocalDate endDate);

    List<Assessment> findByModule_ModuleIdAndDueDateBetween(
            Integer moduleId,
            LocalDate startDate,
            LocalDate endDate);

    List<Assessment> findDistinctByModule_Lecturers_LecturerId(
            Integer lecturerId);
}

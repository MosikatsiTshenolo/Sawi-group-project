
package za.ac.spu.sawi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.spu.sawi.entity.StudentModule;

import java.util.List;

@Repository
public interface StudentModuleRepository
        extends JpaRepository<StudentModule, Integer> {

    List<StudentModule> findByStudent_StudentId(Integer studentId);

    List<StudentModule> findByModule_ModuleId(Integer moduleId);

    List<StudentModule> findByStudent_StudentIdAndAcademicYear(
            Integer studentId, Integer academicYear);

    boolean existsByStudent_StudentIdAndModule_ModuleIdAndAcademicYearAndSemester(
            Integer studentId,
            Integer moduleId,
            Integer academicYear,
            String semester);

    List<StudentModule> findByStudent_User_Username(String username);

}

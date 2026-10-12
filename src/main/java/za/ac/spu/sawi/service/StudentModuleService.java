package za.ac.spu.sawi.service;

import org.springframework.stereotype.Service;
import za.ac.spu.sawi.entity.StudentModule;
import za.ac.spu.sawi.repository.StudentModuleRepository;

import java.util.List;
import java.util.Optional;

@Service
public class StudentModuleService {

    private final StudentModuleRepository studentModuleRepository;

    public StudentModuleService(StudentModuleRepository studentModuleRepository) {
        this.studentModuleRepository = studentModuleRepository;
    }

    public List<StudentModule> findAll() {
        return studentModuleRepository.findAll();
    }

    public Optional<StudentModule> findById(Integer studentModuleId) {
        return studentModuleRepository.findById(studentModuleId);
    }

    public List<StudentModule> findByStudentId(Integer studentId) {
        return studentModuleRepository.findByStudent_StudentId(studentId);
    }

    public List<StudentModule> findByModuleId(Integer moduleId) {
        return studentModuleRepository.findByModule_ModuleId(moduleId);
    }

    public List<StudentModule> findByStudentIdAndAcademicYear(
            Integer studentId, Integer academicYear) {

        return studentModuleRepository.findByStudent_StudentIdAndAcademicYear(
                studentId, academicYear);
    }

    public boolean existsByStudentModuleDetails(
            Integer studentId,
            Integer moduleId,
            Integer academicYear,
            String semester) {

        return studentModuleRepository
                .existsByStudent_StudentIdAndModule_ModuleIdAndAcademicYearAndSemester(
                        studentId,
                        moduleId,
                        academicYear,
                        semester);
    }

    public StudentModule save(StudentModule studentModule) {
        return studentModuleRepository.save(studentModule);
    }

    public void deleteById(Integer studentModuleId) {
        studentModuleRepository.deleteById(studentModuleId);
    }
}
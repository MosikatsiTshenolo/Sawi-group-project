package za.ac.spu.sawi.service;

import org.springframework.stereotype.Service;
import za.ac.spu.sawi.entity.Student;
import za.ac.spu.sawi.repository.StudentRepository;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    public Optional<Student> findById(Integer studentId) {
        return studentRepository.findById(studentId);
    }

    public Optional<Student> findByStudentNumber(String studentNumber) {
        return studentRepository.findByStudentNumber(studentNumber);
    }

    public Optional<Student> findByUserId(Integer userId) {
        return studentRepository.findByUser_UserId(userId);
    }

    public boolean existsByStudentNumber(String studentNumber) {
        return studentRepository.existsByStudentNumber(studentNumber);
    }

    public Student save(Student student) {
        return studentRepository.save(student);
    }

    public void deleteById(Integer studentId) {
        studentRepository.deleteById(studentId);
    }
}
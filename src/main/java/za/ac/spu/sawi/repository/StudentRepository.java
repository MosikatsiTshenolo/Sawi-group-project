package za.ac.spu.sawi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.spu.sawi.entity.Student;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {

    Optional<Student> findByStudentNumber(String studentNumber);

    Optional<Student> findByUser_UserId(Integer userId);

    boolean existsByStudentNumber(String studentNumber);

    Optional<Student> findByUser_Username(String username);

    java.util.List<Student> findByFacultyIgnoreCase(String faculty);
}
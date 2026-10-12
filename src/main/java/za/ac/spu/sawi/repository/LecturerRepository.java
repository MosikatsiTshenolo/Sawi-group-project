package za.ac.spu.sawi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import za.ac.spu.sawi.entity.Lecturer;

@Repository
public interface LecturerRepository extends JpaRepository<Lecturer, Integer> {

    Optional<Lecturer> findByUser_UserId(Integer userId);

    Optional<Lecturer> findByUser_Username(String username);

    boolean existsByStaffNumber(String staffNumber);
}

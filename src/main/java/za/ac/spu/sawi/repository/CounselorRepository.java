
package za.ac.spu.sawi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.spu.sawi.entity.Counselor;

import java.util.Optional;

@Repository
public interface CounselorRepository extends JpaRepository<Counselor, Integer> {

    Optional<Counselor> findByStaffNumber(String staffNumber);

    Optional<Counselor> findByUser_UserId(Integer userId);

    boolean existsByStaffNumber(String staffNumber);
}

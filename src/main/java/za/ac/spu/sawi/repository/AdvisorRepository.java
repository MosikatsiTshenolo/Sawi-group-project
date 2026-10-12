
package za.ac.spu.sawi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.spu.sawi.entity.Advisor;

import java.util.Optional;

@Repository
public interface AdvisorRepository extends JpaRepository<Advisor, Integer> {

    Optional<Advisor> findByStaffNumber(String staffNumber);

    Optional<Advisor> findByUser_UserId(Integer userId);

    Optional<Advisor> findByUser_Username(String username);

    boolean existsByStaffNumber(String staffNumber);
}

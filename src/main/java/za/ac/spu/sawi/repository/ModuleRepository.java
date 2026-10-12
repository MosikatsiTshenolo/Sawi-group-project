


package za.ac.spu.sawi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.spu.sawi.entity.Module;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Integer> {

    Optional<Module> findByModuleCode(String moduleCode);

    boolean existsByModuleCode(String moduleCode);

    List<Module> findDistinctByLecturers_LecturerId(Integer lecturerId);
}

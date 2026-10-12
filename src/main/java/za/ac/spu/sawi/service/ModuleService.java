package za.ac.spu.sawi.service;

import org.springframework.stereotype.Service;
import za.ac.spu.sawi.entity.Module;
import za.ac.spu.sawi.repository.ModuleRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ModuleService {

    private final ModuleRepository moduleRepository;

    public ModuleService(ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }

    public List<Module> findAll() {
        return moduleRepository.findAll();
    }

    public Optional<Module> findById(Integer moduleId) {
        return moduleRepository.findById(moduleId);
    }

    public Optional<Module> findByModuleCode(String moduleCode) {
        return moduleRepository.findByModuleCode(moduleCode);
    }

    public boolean existsByModuleCode(String moduleCode) {
        return moduleRepository.existsByModuleCode(moduleCode);
    }

    public Module save(Module module) {
        return moduleRepository.save(module);
    }

    public void deleteById(Integer moduleId) {
        moduleRepository.deleteById(moduleId);
    }
}

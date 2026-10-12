package za.ac.spu.sawi.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import za.ac.spu.sawi.entity.Module;
import za.ac.spu.sawi.repository.ModuleRepository;

@Service
public class RegistrationOptionsService {

    private final ModuleRepository moduleRepository;

    public RegistrationOptionsService(ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }

    @Transactional
    public List<Module> studentModules() {
        return List.of(
                createIfMissing("ACC101", "Accounting"),
                createIfMissing("APM101", "Applied Maths"),
                createIfMissing("CAL101", "Calculus"),
                createIfMissing("PRG101", "Programming"),
                createIfMissing("ECO101", "Economics"),
                createIfMissing("SOIL101", "Soil Science and Management"));
    }

    private Module createIfMissing(String code, String name) {
        return moduleRepository.findByModuleCode(code).orElseGet(() -> {
            Module module = new Module();
            module.setModuleCode(code);
            module.setModuleName(name);
            module.setDescription("Available for student registration");
            return moduleRepository.save(module);
        });
    }
}
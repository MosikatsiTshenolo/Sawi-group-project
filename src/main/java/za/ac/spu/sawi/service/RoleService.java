package za.ac.spu.sawi.service;

import org.springframework.stereotype.Service;
import za.ac.spu.sawi.entity.Role;
import za.ac.spu.sawi.repository.RoleRepository;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    public Optional<Role> findById(Integer roleId) {
        return roleRepository.findById(roleId);
    }

    public Optional<Role> findByRoleName(String roleName) {
        return roleRepository.findByRoleName(roleName);
    }

    public Role save(Role role) {
        return roleRepository.save(role);
    }

    public void deleteById(Integer roleId) {
        roleRepository.deleteById(roleId);
    }
}
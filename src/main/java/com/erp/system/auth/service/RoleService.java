package com.erp.system.auth.service;

import com.erp.system.auth.entity.Role;
import com.erp.system.auth.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    // Create new role
    public Role createRole(String roleName) {

        Optional<Role> existingRole = roleRepository.findByName(roleName);

        if (existingRole.isPresent()) {
            return existingRole.get();
        }

        Role role = new Role();
        role.setName(roleName);

        return roleRepository.save(role);
    }

    // Get role by name
    public Role getRoleByName(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
    }
}
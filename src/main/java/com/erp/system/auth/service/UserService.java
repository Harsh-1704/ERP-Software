package com.erp.system.auth.service;

import com.erp.system.auth.entity.Role;
import com.erp.system.auth.entity.User;
import com.erp.system.auth.repository.RoleRepository;
import com.erp.system.auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // CREATE USER
    public User createUser(String username, String password, String roleName) {

        if(userRepository.findByUsername(username).isPresent()){
            throw new RuntimeException("Username already exists");
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() ->
                        new RuntimeException("Role not found: " + roleName)
                );

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setActive(true);

        return userRepository.save(user);
    }

    // REGISTER USER (public self-registration)
    public User registerUser(String username, String password, String roleName) {

        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        // Validate role name to prevent arbitrary role assignment
        if (!roleName.equals("ROLE_USER") && !roleName.equals("ROLE_ADMIN")) {
            throw new RuntimeException("Invalid role: " + roleName);
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() ->
                        new RuntimeException("Role not found: " + roleName)
                );

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setActive(true);

        return userRepository.save(user);
    }

    // GET USER BY USERNAME
    public User getUserByUsername(String username) {

        return userRepository.findByUsername(username)
                .orElse(null);
    }

    // GET ALL USERS
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
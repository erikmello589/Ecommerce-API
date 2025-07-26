package com.erikm.ecommerce.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.erikm.ecommerce.dto.CreateUserDTO;
import com.erikm.ecommerce.model.Role;
import com.erikm.ecommerce.model.User;
import com.erikm.ecommerce.repository.RoleRepository;
import com.erikm.ecommerce.repository.UserRepository;

import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, 
                       RoleRepository roleRepository, 
                       BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerNewUser(CreateUserDTO createUserDTO) 
    {
        var basicRole = roleRepository.findByName(Role.Values.BASIC.name());
        if (basicRole == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Role básica não encontrada");
        }

        Optional<User> userFromUsernameDB = userRepository.findByUsername(createUserDTO.username());
        Optional<User> userFromEmailDB = userRepository.findByEmail(createUserDTO.email());

        if (userFromUsernameDB.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nome de usuário já existe.");
        }
        if (userFromEmailDB.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já existe.");
        }

        var user = new User();
        user.setUsername(createUserDTO.username());
        user.setEmail(createUserDTO.email());
        user.setName(createUserDTO.name());
        user.setLastName(createUserDTO.lastName());
        user.setPassword(passwordEncoder.encode(createUserDTO.password()));
        user.setRoles(Set.of(basicRole));

        return userRepository.save(user);
    }

    public User acharUserPorId(UUID idUser) 
    {
        return userRepository.findById(idUser)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    }
}
package com.web_ii.tech_catalog.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.web_ii.tech_catalog.models.User;
import com.web_ii.tech_catalog.repository.UserRepository;
import com.web_ii.tech_catalog.service.IUserService;

@Service
public class UserServiceImpl implements IUserService, UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public Integer saveUser(User user) {
        String passwd = user.getPassword();
        String encodedPassword = passwordEncoder.encode(passwd);
        user.setPassword(encodedPassword);
        user = userRepo.save(user);
        return user.getId();
    }
    
    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepo.findUserByEmail(email);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public Optional<User> getUserById(Integer id) {
        return userRepo.findById(id);
    }

    @Override
    public void updateUser(User user, String newPassword) {
        Optional<User> existingUserOpt = userRepo.findById(user.getId());
        
        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            
            // Atualiza os dados básicos
            existingUser.setName(user.getName());
            existingUser.setEmail(user.getEmail());
            existingUser.setRoles(user.getRoles());
            
            // Atualiza a senha apenas se uma nova for fornecida
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                String encodedPassword = passwordEncoder.encode(newPassword);
                existingUser.setPassword(encodedPassword);
            }
            
            userRepo.save(existingUser);
        } else {
            throw new RuntimeException("Usuário não encontrado com ID: " + user.getId());
        }
    }

    @Override
    public void deleteUser(Integer id) {
        if (userRepo.existsById(id)) {
            userRepo.deleteById(id);
        } else {
            throw new RuntimeException("Usuário não encontrado com ID: " + id);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<User> opt = userRepo.findUserByEmail(email);

        org.springframework.security.core.userdetails.User springUser = null;

        if (opt.isEmpty()) {
            throw new UsernameNotFoundException("User with email: " + email + " not found");
        } else {
            User user = opt.get();
            List<String> roles = user.getRoles();
            Set<GrantedAuthority> ga = new HashSet<>();
            for (String role : roles) {
                ga.add(new SimpleGrantedAuthority(role));
            }

            springUser = new org.springframework.security.core.userdetails.User(
                    email,
                    user.getPassword(),
                    ga);
        }

        return springUser;
    }
<<<<<<< HEAD
}
=======
}
>>>>>>> 97b682778b5d3be228136496a9e077f50dca5ba0

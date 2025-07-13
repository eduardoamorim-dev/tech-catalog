package com.web_ii.tech_catalog.service;

import com.web_ii.tech_catalog.models.User;
import java.util.List;
import java.util.Optional;

public interface IUserService {
    
    public Integer saveUser(User user);
    
    Optional<User> findUserByEmail(String email);
    
    List<User> getAllUsers();
    
    Optional<User> getUserById(Integer id);
    
    void updateUser(User user, String newPassword);
    
    void deleteUser(Integer id);
}
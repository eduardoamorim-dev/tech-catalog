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
<<<<<<< HEAD
}
=======
}
>>>>>>> 97b682778b5d3be228136496a9e077f50dca5ba0

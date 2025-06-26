package com.web_ii.tech_catalog.service;

import com.web_ii.tech_catalog.models.User;
import java.util.Optional;

public interface IUserService {
	
	public Integer saveUser(User user);
	Optional<User> findUserByEmail(String email);
}
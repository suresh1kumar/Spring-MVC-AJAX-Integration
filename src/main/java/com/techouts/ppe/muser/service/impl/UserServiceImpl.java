package com.techouts.ppe.muser.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techouts.ppe.muser.model.User;
import com.techouts.ppe.muser.repositories.UserRepository;
import com.techouts.ppe.muser.service.UserService;

@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private UserRepository userRepository;

	@Override
	public List<User> findUserList() {
		
		return userRepository.findAll();
	}

	@Override
	public User findById(Long id) {
		
		return userRepository.getOne(id);
	}

	@Override
	public User updateUser(User user) {
		
		return userRepository.save(user);
	}

	@Override
	public Integer deleteUserById(Long id) {
		
		return null;
	}
	
	

}

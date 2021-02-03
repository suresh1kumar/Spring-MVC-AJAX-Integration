package com.techouts.ppe.muser.service;

import java.util.List;

import com.techouts.ppe.muser.model.User;

public interface UserService {
	
	List<User> findUserList();
	User findById(Long id);
	
	User updateUser(User user);
	
	Integer deleteUserById(Long id);
	
	
	
	

}

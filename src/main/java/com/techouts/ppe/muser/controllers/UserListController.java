package com.techouts.ppe.muser.controllers;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.techouts.ppe.muser.exceptions.ResourceNotFoundException;
import com.techouts.ppe.muser.model.User;
import com.techouts.ppe.muser.service.impl.UserServiceImpl;

@RestController
public class UserListController {

	@Autowired
	UserServiceImpl userServiceImpl;

	
	@GetMapping(value = "/api/auth/users", produces = "application/json")
	public List<User> userList() {
		List<User> users = userServiceImpl.findUserList();
		return users;
	}

	@GetMapping("/api/auth/user/{userid}")
	public ResponseEntity<User> getUser(@PathVariable(value = "userid") Long id) {
	    try {
	    	User product = userServiceImpl.findById(id);
	        return new ResponseEntity<User>(product, HttpStatus.OK);
	    } catch (NoSuchElementException e) {
	        return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
	    }      
	}
	
	@PutMapping("/api/auth/updateuser")
	public ResponseEntity<User> updateUser(@RequestBody User reqJson) {

		User user = userServiceImpl.updateUser(reqJson);

		return ResponseEntity.ok().body(user);
	}
	
}
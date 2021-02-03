package com.techouts.ppe.muser.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techouts.ppe.muser.model.User;


@Repository
public interface UserRepositoryByEmail extends JpaRepository<User, Long>{
	
	User findByEmail(String confirmationToken);

}

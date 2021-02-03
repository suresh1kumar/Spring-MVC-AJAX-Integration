package com.techouts.ppe.muser.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techouts.ppe.muser.model.Role;
import com.techouts.ppe.muser.model.RoleName;

/**
 * Created by pavan on 11/11/20f.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}

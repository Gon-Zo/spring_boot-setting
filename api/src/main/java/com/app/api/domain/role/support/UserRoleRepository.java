package com.app.api.domain.role.support;

import com.app.api.domain.role.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole , Long> {


}

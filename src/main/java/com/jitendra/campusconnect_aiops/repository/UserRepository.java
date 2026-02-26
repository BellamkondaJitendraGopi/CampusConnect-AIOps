package com.jitendra.campusconnect_aiops.repository;

import com.jitendra.campusconnect_aiops.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}

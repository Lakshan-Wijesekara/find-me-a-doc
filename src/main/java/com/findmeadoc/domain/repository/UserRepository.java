package com.findmeadoc.domain.repository;

import com.findmeadoc.domain.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); // Using Optional to handle the possibility of a user not being found
}

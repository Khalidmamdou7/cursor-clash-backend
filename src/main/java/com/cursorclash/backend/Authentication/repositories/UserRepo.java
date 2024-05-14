package com.cursorclash.backend.Authentication.repositories;



import com.cursorclash.backend.Authentication.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@EnableJpaRepositories
@Repository
public interface UserRepo extends JpaRepository<User,Integer>
{
    Optional<User> findOneByEmailAndPassword(String email, String password);
    User findByEmail(String email);
    Optional<User> findById(int userId);
}

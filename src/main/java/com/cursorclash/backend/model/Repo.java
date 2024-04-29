package com.cursorclash.backend.model;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface Repo {

    Optional<User> findByEmail(String Email);
}

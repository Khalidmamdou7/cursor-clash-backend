package com.cursorclash.backend.RegistrationAndLogin.Repo;

import com.cursorclash.backend.Document.Document;
import com.cursorclash.backend.RegistrationAndLogin.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepo extends JpaRepository<Document, Long> {

    List<Document> findByOwner(User user);
}

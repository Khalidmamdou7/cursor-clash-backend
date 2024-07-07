package com.cursorclash.backend.Document.repositories;

import com.cursorclash.backend.Document.entities.Document;
import com.cursorclash.backend.Authentication.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepo extends JpaRepository<Document, Long> {

    List<Document> findByOwner(User user);
}

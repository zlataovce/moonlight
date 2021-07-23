package me.zlataovce.moonlight.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasteRepository extends JpaRepository<Paste, Integer> {
    Optional<Paste> findByUrl(String url);
    Optional<Paste> findByIdentifier(String identifier);
}

package rest.pkbe.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import rest.pkbe.domain.model.Tag;

public interface TagRepository extends JpaRepository<Tag, Long>{

    Optional<Tag> findByName(String name);

    boolean existsByName(String name);
    
}

package rest.pkbe.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import rest.pkbe.domain.model.Tag;
import rest.pkbe.domain.model.User;

public interface TagRepository extends JpaRepository<Tag, Long>{

    Optional<Tag> findByNameAndUser(String name, User user);

    boolean existsByName(String name);
    
}

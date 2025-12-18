package rest.pkbe.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rest.pkbe.model.Note;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long>{
    
    List<Note> findAllByUserId(Long userId);

}

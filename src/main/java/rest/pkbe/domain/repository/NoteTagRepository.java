package rest.pkbe.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import rest.pkbe.domain.model.NoteTag;
import rest.pkbe.domain.model.NoteTagId;

public interface NoteTagRepository extends JpaRepository<NoteTag, NoteTagId>{
    
}

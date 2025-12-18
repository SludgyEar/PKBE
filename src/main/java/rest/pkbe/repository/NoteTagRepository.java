package rest.pkbe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import rest.pkbe.model.NoteTag;
import rest.pkbe.model.NoteTagId;

public interface NoteTagRepository extends JpaRepository<NoteTag, NoteTagId>{
    
}

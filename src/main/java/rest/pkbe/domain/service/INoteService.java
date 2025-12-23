package rest.pkbe.domain.service;

import java.util.List;
import java.util.Set;

import rest.pkbe.domain.model.Note;

public interface INoteService {
    Note createNote(Long userId, Note note, Set<String> tagNames);

    List<Note> getAllNotes(Long userId);

    void deleteNoteById(Long noteId, Long userId);

    void updateNoteById(Long noteId, Long userId, String title, String content, Set<String> tagNames);

}

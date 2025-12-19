package rest.pkbe.domain.service;

import java.util.Set;

import rest.pkbe.domain.model.Note;

public interface INoteService {
    Note createNote(Long userId, Note note, Set<String> tagNames);
}

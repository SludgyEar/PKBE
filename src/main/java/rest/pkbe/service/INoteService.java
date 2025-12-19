package rest.pkbe.service;

import java.util.Set;

import rest.pkbe.model.Note;

public interface INoteService {
    Note createNote(Long userId, Note note, Set<String> tagNames);
}

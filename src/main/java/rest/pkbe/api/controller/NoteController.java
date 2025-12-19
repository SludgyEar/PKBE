package rest.pkbe.api.controller;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import rest.pkbe.api.dto.request.CreateNoteRequest;
import rest.pkbe.domain.model.Note;
import rest.pkbe.domain.service.impl.NoteServiceImpl;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/notes")
public class NoteController {

    @Autowired
    private NoteServiceImpl noteServiceImpl;

    @PostMapping("/user/{userId}")
    public ResponseEntity<?> createNote(@PathVariable Long userId, @Valid @RequestBody CreateNoteRequest req)throws URISyntaxException {
        Note note = new Note();
        note.setTitle(req.getTitle());
        note.setContent(req.getContent());

        Note saved = noteServiceImpl.createNote(userId, note, req.getTags());
        return ResponseEntity.created(new URI("/user/"+ userId + "/note" + saved.getId())).build();
    }
    
    
}

package rest.pkbe.api.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import rest.pkbe.api.dto.NoteDTO;
import rest.pkbe.api.dto.request.CreateNoteRequest;
import rest.pkbe.domain.model.Note;
import rest.pkbe.domain.service.INoteService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/notes")
/**
 * Endpoint que atiende las peticiones relacionadas con notas
 */
public class NoteController {

    @Autowired
    private INoteService noteService; // Corrección: Inyectamos la interfaz, no la implementación

    @PostMapping("/user/{userId}") // se sacará del token
    public ResponseEntity<?> createNote(@PathVariable Long userId, @Valid @RequestBody CreateNoteRequest req)throws URISyntaxException {
        /**
         * Crea una nota con los valores recibidos a través de un DTO
         * Se adjunta al usuario recibido por la path variable (temporal)
         */
        Note note = new Note();
        note.setTitle(req.getTitle());
        note.setContent(req.getContent());

        Note saved = noteService.createNote(userId, note, req.getTags());
        return ResponseEntity.created(new URI("/user/"+ userId + "/note/" + saved.getId())).build();
    }

    @GetMapping("/user/{userId}") // se sacará del token
    public ResponseEntity<?> getAllUserNotes(@PathVariable Long userId){
        /**
         * Obtiene las notas pertenecientes a un usuario dado su id
         * El id se recupera de una path variable (temporal)
         * Se regresa una lista usando un DTO de respuesta como base
         */
        List<NoteDTO> noteList = noteService.getAllNotes(userId)
            .stream()
            .map(note -> NoteDTO.builder()
            .id(note.getId())
            .title(note.getTitle())
            .content(note.getContent())
            .createdAt(note.getCreatedAt().toString().split("T")[0])
            .tags(note.getNoteTags().stream().map(nt -> nt.getTag().getName()).collect(Collectors.toSet()))
            .build())
        .toList();

        return ResponseEntity.ok(noteList);
    }
    
    @DeleteMapping("/user/{userId}/{noteId}")   // se sacará del token
    public ResponseEntity<?> deleteNoteById(@PathVariable Long userId, @PathVariable Long noteId) {
        /**
         * Se elimina una nota dado un id de usuario y un id de nota
         * Ambos id's se recuperan de los path variables, (temporal)
         * Se elimina la nota pero no la tag, ya que una tag puede ser utilizada por una o más notas
         */
        noteService.deleteNoteById(noteId, userId);
        return ResponseEntity.ok("La nota con id " + noteId + " perteneciente al usuario con id " + userId + " se ha eliminado.");
    }
    
    @PatchMapping("/user/{userId}/{noteId}")
    public ResponseEntity<?> updateNoteById(@PathVariable Long userId, @PathVariable Long noteId, @RequestBody NoteDTO req){
        /**
         * Se reciben id's de dos path variables, identifican al usuario dueño de la nota y la nota que se quiere modificar
         * Además se incluye un DTO que contiene los nuevos datos que se requieren actualizar
         * Los id's recuperados de los path variables son temporales
         */
        noteService.updateNoteById(noteId, userId, req.getTitle(), req.getContent(), req.getTags());
        return ResponseEntity.ok("La nota con el id " + noteId + " perteneciente al usuario con id " + userId + " ha sido actualizada.");
    }

}

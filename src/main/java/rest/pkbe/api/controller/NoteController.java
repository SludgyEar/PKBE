package rest.pkbe.api.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import rest.pkbe.api.dto.request.note.CreateNoteRequest;
import rest.pkbe.api.dto.response.note.NoteDTO;
import rest.pkbe.domain.model.Note;
import rest.pkbe.domain.model.User;
import rest.pkbe.domain.service.INoteService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/notes")
/**
 * Endpoint que atiende las peticiones relacionadas con notas
 */
public class NoteController {

    @Autowired
    private INoteService noteService; // Corrección: Inyectamos la interfaz, no la implementación
    private static final Logger logger = LoggerFactory.getLogger(NoteController.class);

    @PostMapping
    public ResponseEntity<?> createNote(@AuthenticationPrincipal User user, @Valid @RequestBody CreateNoteRequest req)
    throws URISyntaxException {
        logger.info("Iniciando POST / - Crear nota");
        /**
         * Crea una nota con los valores recibidos a través de un DTO
         * Se obtiene el id del usuario que está logeado
         */
        Note note = new Note();
        note.setTitle(req.getTitle());
        note.setContent(req.getContent());

        Note saved = noteService.createNote(user.getId(), note, req.getTags());
        NoteDTO res = NoteDTO.builder()
            .id(saved.getId())
            .title(saved.getTitle())
            .content(saved.getContent())
            .createdAt(saved.getCreatedAt().toString().split("T")[0])
            .tags(req.getTags())
            .build();
        logger.info("Operación POST / - Finalizada");
        return ResponseEntity.created(new URI("/user/"+ user.getId() + "/note/" + res.getId())).body(res);
    }

    @GetMapping
    public ResponseEntity<?> getAllUserNotes(@AuthenticationPrincipal User user){
        logger.info("Iniciando GET / - Obtener notas de un usuario");
        /**
         * Obtiene las notas pertenecientes a un usuario dado su id
         * El id se recupera del token de acceso
         * Se regresa una lista usando un DTO de respuesta como base
         */
        List<NoteDTO> noteList = noteService.getAllNotes(user.getId())
            .stream()
            .map(note -> NoteDTO.builder()
            .id(note.getId())
            .title(note.getTitle())
            .content(note.getContent())
            .createdAt(note.getCreatedAt().toString().split("T")[0])
            .tags(note.getNoteTags().stream().map(nt -> nt.getTag().getName()).collect(Collectors.toSet()))
            .build())
        .toList();
        logger.info("Operación GET / - Finalizada");
        return ResponseEntity.ok(noteList);
    }
    
    @DeleteMapping("/{noteId}")   // se sacará del token
    public ResponseEntity<?> deleteNoteById(@PathVariable Long noteId, @AuthenticationPrincipal User user) {
        logger.info("Iniciando DELETE /{} - Eliminar nota", noteId);
        /**
         * Se elimina una nota dado un id de usuario y un id de nota
         * noteId se recupera del path para identificar el recurso
         * el id de usuario se recupera del token de acceso
         * Se elimina la nota pero no la tag, ya que una tag puede ser utilizada por una o más notas
         */
        noteService.deleteNoteById(noteId, user.getId());
        logger.info("Operación DELETE /{} - Finalizada", noteId);
        return ResponseEntity.ok("La nota con id " + noteId + " perteneciente al usuario con id " + user.getId() + " se ha eliminado.");
    }
    
    @PatchMapping("/{noteId}")
    public ResponseEntity<?> updateNoteById(@AuthenticationPrincipal User user, @PathVariable Long noteId, @RequestBody NoteDTO req){
        logger.info("Iniciando PATCH /{} - Actulizar nota", noteId);
        /**
         * noteId se recupera del path para identificar el recurso
         * el id de usuario se recupera del token de acceso
         * Además se incluye un DTO que contiene los nuevos datos que se requieren actualizar
         */
        noteService.updateNoteById(noteId, user.getId(), req.getTitle(), req.getContent(), req.getTags());
        logger.info("Operación PATCH /{} - Finalizada", noteId);
        return ResponseEntity.ok("La nota con el id " + noteId + " perteneciente al usuario con id " + user.getId() + " ha sido actualizada.");
    }

}

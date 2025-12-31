package rest.pkbe.domain.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.NonNull;
import rest.pkbe.domain.model.Note;
import rest.pkbe.domain.model.NoteTag;
import rest.pkbe.domain.model.Tag;
import rest.pkbe.domain.model.User;
import rest.pkbe.domain.repository.NoteRepository;
import rest.pkbe.domain.repository.NoteTagRepository;
import rest.pkbe.domain.repository.UserRepository;
import rest.pkbe.domain.service.INoteService;
import rest.pkbe.domain.service.ITagService;
import rest.pkbe.exception.exceptions.ResourceNotFoundException;

@Service
public class NoteServiceImpl implements INoteService{

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private NoteTagRepository noteTagRepository;

    @Autowired
    private ITagService tagService; // Corrección: Inyectamos la interfaz, no la implementación


    @Override
    @Transactional
    public Note createNote(@NonNull Long userId, Note note, Set<String> tagNames) {
        /**
         * - En primer lugar se comprueba que el usuario no es null (porque usamos Long)
         * - Comprobamos que la nota tenga al menos un tag
         * - Comprobamos si el usuario existe
         * - Ligamos la nota al usuario y la guardamos
         * - Recorremos los tags dados por el usuario y los adjuntamos a la nota a través de la relación NoteTag
         * - Una vez que hayamos terminado de ligar los tags con la nota principal, la regresamos
         */
        User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("El usuario no existe"));
        if(tagNames == null || tagNames.isEmpty()){
            throw new IllegalArgumentException("La nota debe contener al menos una etiqueta");
        }
        note.setUser(user);
        Note savedNote = noteRepository.save(note);

        for(String tagName : tagNames){
            Tag tag = tagService.getOrCreate(tagName, user);
            NoteTag noteTag = new NoteTag(savedNote, tag);
            noteTagRepository.save(noteTag);
        }
        return savedNote;
    }

    @Override
    public List<Note> getAllNotes(@NonNull Long userId){
        /**
         * - Primero se comprueba que el id no sea nulo
         * - Comprobamos que el usuario existe
         * - Retornamos las notas que le pertencen
         */
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario no existe"));

        List<Note> noteList = noteRepository.findAllByUserIdWithTags(userId);

        return noteList;
    }

    @Override
    @Transactional
    public void deleteNoteById(@NonNull Long noteId, @NonNull Long userId){
        /**
         * - Para borrar una nota corroboramos que la nota exista y le pertenezca al usuario
         * - Borramos la nota pero no sus tags
         */
        if(!noteRepository.existsByIdAndUserId(noteId, userId)){
            throw new ResourceNotFoundException("No tienes una nota con estas características");
        }
        noteRepository.deleteById(noteId);
    }

    @Override
    @Transactional
    public void updateNoteById(@NonNull Long noteId, @NonNull Long userId, String title, String content, Set<String> tagNames){
        /**
         * Para editar una nota, recibimos el id de la nota, el id del usuario dueño, además, el contenido que va a ser editado
         * Primero corroboramos si los campos simples no son nulos, si no lo son, los actualizamos.
         * Si se quiere actualizar las tags de una nota, tenemos que trabajar con las relaciones de NoteTag
         * - Sacamos las tags actuales de la nota y asilamos sus nombres
         * - en un ciclo for, iteramos la nueva lista de tags, así identificamos las tags que ya se encuentran en la nota y las que se agregarán o eliminaran
         * - guardamos la nueva nota con save, al ya tener un id, JPA identifica que es una actualización
         */
        Optional<Note> optionalNote = noteRepository.findByIdAndUserId(noteId, userId);
        if(optionalNote.isPresent()){
            Note updatedNote = optionalNote.get();
            if(title != null){
                updatedNote.setTitle(title);
            }
            if(content != null){
                updatedNote.setContent(content);
            }
            if(tagNames != null){
                Set<NoteTag> current = updatedNote.getNoteTags();
                // Mapear tags actuales por nombre
                Map<String, NoteTag> currentByName = current.stream().collect(Collectors.toMap(nt -> nt.getTag().getName(), Function.identity()));
                Set<NoteTag> updated = new HashSet<>();
                for(String tagName : tagNames){
                    NoteTag existing = currentByName.get(tagName);
                    if(existing != null){
                        // Ya existía, lo conservamos
                        updated.add(existing);
                    }else{
                        // Nuevo tag, creamos relación
                        Tag tag = tagService.getOrCreate(tagName, updatedNote.getUser());
                        updated.add(new NoteTag(updatedNote, tag));
                    }
                }
                current.clear();
                current.addAll(updated);
            }
            noteRepository.save(updatedNote);
        }else{
            throw new ResourceNotFoundException("La nota no existe");
        }
    }

}

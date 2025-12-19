package rest.pkbe.domain.service.impl;

import java.util.Set;

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

@Service
public class NoteServiceImpl implements INoteService{

    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NoteTagRepository noteTagRepository;
    @Autowired
    private TagServiceImpl tagServiceImpl;


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
                    .orElseThrow(() -> new IllegalArgumentException("El usuario no existe"));
        if(tagNames == null || tagNames.isEmpty()){
            throw new IllegalArgumentException("La nota debe contener al menos una etiqueta");
        }
        note.setUser(user);
        Note savedNote = noteRepository.save(note);

        for(String tagName : tagNames){
            Tag tag = tagServiceImpl.getOrCreate(tagName, user);
            NoteTag noteTag = new NoteTag(savedNote, tag);
            noteTagRepository.save(noteTag);
        }
        return savedNote;
    }
    
}

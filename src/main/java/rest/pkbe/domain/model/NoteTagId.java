package rest.pkbe.domain.model;
import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Clase embebible que representa la clave primaria compuesta para NoteTag.
 * No mapea una tabla, solo define los campos de la clave compuesta.
 */
@Embeddable
public class NoteTagId implements Serializable{
    /**
     * Identificador de la nota (parte de la clave compuesta).
     */
    @Column(name = "note_id")
    private Long noteId;

    /**
     * Identificador de la etiqueta (parte de la clave compuesta).
     */
    @Column(name = "tag_id")
    private Long tagId;

    /**
     * Constructor protegido requerido por JPA.
     */
    protected NoteTagId() {}

    /**
     * Constructor para crear una clave compuesta con los IDs de nota y etiqueta.
     */
    public NoteTagId(Long noteId, Long tagId) {
        this.noteId = noteId;
        this.tagId = tagId;
    }

    public Long getNoteId() {
        return noteId;
    }

    public void setNoteId(Long noteId) {
        this.noteId = noteId;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    /**
     * Compara dos claves compuestas por sus IDs de nota y etiqueta.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NoteTagId)) return false;
        NoteTagId that = (NoteTagId) o;
        return Objects.equals(noteId, that.noteId)
            && Objects.equals(tagId, that.tagId);
    }

    /**
     * Calcula el hashCode usando los IDs de nota y etiqueta.
     */
    @Override
    public int hashCode() {
        return Objects.hash(noteId, tagId);
    }
}

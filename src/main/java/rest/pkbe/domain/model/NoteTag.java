package rest.pkbe.domain.model;

import java.util.Objects;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Entidad JPA que representa la relación entre una nota y una etiqueta (tag).
 * Utiliza una clave primaria compuesta (NoteTagId) para identificar la asociación.
 * Permite asociar múltiples etiquetas a una nota y viceversa.
 */
@Entity
@Table(name = "note_tags")
@Setter
@Getter
@ToString
public class NoteTag {

    /**
     * Constructor para crear una asociación entre una nota y una etiqueta.
     */
    public NoteTag(Note note, Tag tag) {
        this.note = note;
        this.tag = tag;
        // this.id = new NoteTagId(note.getId(), tag.getId());
    }
    /**
     * Constructor protegido requerido por JPA.
     */
    protected NoteTag(){}

    /**
     * Clave primaria compuesta que representa la relación nota-etiqueta.
     */
    @EmbeddedId
    private NoteTagId id = new NoteTagId();

    /**
     * Referencia a la entidad Note (parte de la clave compuesta).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("noteId")
    @JoinColumn(name = "note_id")
    private Note note;

    /**
     * Referencia a la entidad Tag (parte de la clave compuesta).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tagId")
    @JoinColumn(name = "tag_id")
    private Tag tag;
    
    /**
     * Compara dos asociaciones NoteTag por nota y nombre de la etiqueta.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NoteTag)) return false;
        NoteTag that = (NoteTag) o;
        if(!this.note.equals(that.getNote())) return false;
        return this.tag.getName().equals(that.getTag().getName());
        // if(this.id != null && that.id != null) return Objects.equals(id, that.id);
        // return Objects.equals(id, that.id);
        // return Objects.equals(tag.getName(), that.tag.getName());
    }

    /**
     * Calcula el hashCode usando la nota y el nombre de la etiqueta.
     */
    @Override
    public int hashCode() {
        // return Objects.hash(id);
        return Objects.hash(note, tag.getName());
    }

}

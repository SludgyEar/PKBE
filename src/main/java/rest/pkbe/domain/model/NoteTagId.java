package rest.pkbe.domain.model;
import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;

@Embeddable
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class NoteTagId implements Serializable{
    /**
     * No mapea una tabla, crea un PK compuesto
     * El @embeddable indica que es un valor, no una entidad
     */

    @Column(name = "note_id")
    @EqualsAndHashCode.Include
    private Long noteId;

    @Column(name = "tag_id")
    @EqualsAndHashCode.Include
    private Long tagId;
}

package rest.pkbe.domain.model;
import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class NoteTagId implements Serializable{
    /**
     * No mapea una tabla, crea un PK compuesto
     * El @embeddable indica que es un valor, no una entidad
     */
    @EqualsAndHashCode.Include
    private Long noteId;

    @EqualsAndHashCode.Include
    private Long tagId;
}

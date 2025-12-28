package rest.pkbe.domain.model;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
/**
 * Entidad JPA que representa una etiqueta (tag) creada por un usuario.
 * Cada etiqueta tiene un nombre único por usuario y puede asociarse a muchas notas.
 * Utiliza anotaciones de Lombok y JPA para facilitar el mapeo y la generación de métodos.
 */
@Entity
@Table(name = "tags", uniqueConstraints = {
    @UniqueConstraint(name = "uq_user_tag", columnNames = {"user_id", "name"})
})
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Tag {
    /**
     * Identificador único de la etiqueta (clave primaria, autoincremental).
     */
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre de la etiqueta (máximo 50 caracteres, único por usuario).
     */
    @Column(name = "name", length = 50)
    private String name;

    /**
     * Relación ManyToOne: muchas etiquetas pueden pertenecer a un usuario.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Relación OneToMany: una etiqueta puede estar asociada a muchas notas (a través de NoteTag).
     */
    @OneToMany(mappedBy = "tag", fetch = FetchType.LAZY)
    private Set<NoteTag> noteTags;
}

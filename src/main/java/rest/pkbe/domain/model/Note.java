package rest.pkbe.domain.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CurrentTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
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
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entidad JPA que representa una nota en el sistema.
 * Incluye título, contenido, fechas de creación/actualización y relaciones con usuario y etiquetas.
 * Utiliza anotaciones de Lombok y JPA para facilitar el mapeo y la generación de métodos.
 */
@Entity
@Table(name = "notes")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Note {
    /**
     * Identificador único de la nota (clave primaria, autoincremental).
     */
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Título de la nota (no nulo, máximo 150 caracteres).
     */
    @Column(name = "title", nullable = false, length = 150)
    private String title;

    /**
     * Contenido de la nota (no nulo).
     */
    @Column(name = "content", nullable = false)
    private String content;

    /**
     * Fecha y hora de creación de la nota (asignada automáticamente).
     */
    @Column(name = "created_at")
    @CurrentTimestamp
    private LocalDateTime createdAt;

    /**
     * Fecha y hora de la última actualización de la nota (asignada automáticamente).
     */
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /**
     * Relación ManyToOne: muchas notas pertenecen a un usuario.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Relación OneToMany: una nota puede tener muchas asociaciones con etiquetas (NoteTag).
     */
    @OneToMany(mappedBy = "note"/*, fetch = FetchType.LAZY*/, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<NoteTag> noteTags = new HashSet<>();
}

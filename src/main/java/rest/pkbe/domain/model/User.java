package rest.pkbe.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CurrentTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "users")
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
/**
 * Entidad JPA que representa a un usuario en el sistema.
 * Incluye información básica como nombre de usuario, email, contraseña y
 * relaciones con notas y etiquetas.
 * Utiliza anotaciones de Lombok para generación automática de métodos y JPA
 * para mapeo a la base de datos.
 */

public class User {
    /**
     * Identificador único del usuario (clave primaria, autoincremental).
     */
    @EqualsAndHashCode.Include
    @Id // PK
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto increment
    private Long id;

    /**
     * Nombre de usuario (no nulo, máximo 50 caracteres).
     */
    @Column(name = "username", nullable = false, length = 50) // Restricciones de columna
    private String username;

    /**
     * Correo electrónico del usuario (único, no nulo, máximo 120 caracteres).
     */
    @Column(name = "email", nullable = false, length = 120, unique = true)
    private String email;

    /**
     * Hash de la contraseña del usuario (no se almacena la contraseña en texto plano).
     */
    @Column(name = "password_hash", nullable = false, length = 120)
    private String passwordHash;

    /**
     * Fecha y hora de creación del usuario (asignada automáticamente).
     */
    @Column(name = "created_at")
    @CurrentTimestamp
    private LocalDateTime createdAt;

    /**
     * Relación OneToMany: un usuario puede tener muchas notas asociadas.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Note> notas = new ArrayList<>();

    /**
     * Relación OneToMany: un usuario puede crear muchas etiquetas (tags).
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tag> tags = new ArrayList<>();
}

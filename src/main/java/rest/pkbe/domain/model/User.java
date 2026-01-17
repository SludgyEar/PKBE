package rest.pkbe.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hibernate.annotations.CurrentTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonProperty;

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
@ToString(exclude = {"notas", "tags", "token", "passwordHash", "email"})
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

public class User implements UserDetails{
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
    // Como sobreescribimos getUsername para UserDetails, debemos de crear un nuevo método para obtener el nombre del usuario
    public String getNombreUsuario(){
        return this.username;
    }

    /**
     * Correo electrónico del usuario (único, no nulo, máximo 120 caracteres).
     */
    @Column(name = "email", nullable = false, length = 120, unique = true)
    private String email;

    /**
     * Hash de la contraseña del usuario (no se almacena la contraseña en texto plano).
     * Omitimos devolver la contraseña en una response JSON, pero si podemos recibirla en un request de creación de usuario
     */
    @Column(name = "password_hash", nullable = false, length = 120)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
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

    /**
     * Relación OneToMany: un usuario puede tener muchos tokens de refresco de sesión
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RefreshToken> token = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Retornamos una lista vacía porque aún no manejamos roles
        return Collections.emptyList();
    }

    /**
     * Estos métodos son utilizados por DaoAuthenticationProvider por lo que tiene que
     * apuntar a la contraseña
     */
    @Override
    public String getPassword() {
        return this.passwordHash;
    }
    @Override
    public String getUsername(){
        return this.email;
    }
}

package rest.pkbe.api.dto.request.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
/**
 * CreateUserRequest es un DTO que contiene datos simples pero necesarios para
 * crear un usuario.
 * La información recibida no puede estar vacía, lo cual se valida con jakarta
 * validation constraints
 * Se recibe un nombre, email y contraseña, la contraseña ya debe de estar encriptada,
 * así la petición no viaja con información sensible en texto plano
 */
public class CreateUserRequest {
    @NotBlank(message = "El nombre no puede estar vacío")
    private String username;
    @NotBlank(message = "El email no puede estar vacío")
    @Email
    private String email;
    @NotBlank(message = "Debe de incluirse una contraseña")
    private String passwordHash;
}

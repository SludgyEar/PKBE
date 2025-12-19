package rest.pkbe.api.dto.request;

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
public class CreateUserRequest {
    @NotBlank(message = "El nombre no puede estar vacío")
    private String username;
    @NotBlank(message = "El email no puede estar vacío")
    @Email
    private String email;
    @NotBlank(message = "Debe de incluirse una contraseña")
    private String passwordHash;
}

package rest.pkbe.api.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class LoginRequest {
    /**
     * DTO para hacer una petición de login, solo contiene email y contraseña en texto plano
     * El decoder se encarga de comparar las contraseñas internamente
     */
    @NotBlank
    private String email;
    @NotBlank
    private String password;
}

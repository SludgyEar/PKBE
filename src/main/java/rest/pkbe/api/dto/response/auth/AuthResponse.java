package rest.pkbe.api.dto.response.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthResponse {
    /**
     * DTO de respuesta a un login exitoso
     * Solo contiene un token de acceso
     */
    @NotBlank
    private String accessToken;

    public AuthResponse(String accessToken){
        this.accessToken = accessToken;
    }
}

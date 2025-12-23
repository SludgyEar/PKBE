package rest.pkbe.api.dto.response.auth;

import jakarta.validation.constraints.NotBlank;

public class AuthResponse {
    @NotBlank
    private String accessToken;

    public AuthResponse(String accessToken){
        this.accessToken = accessToken;
    }
}

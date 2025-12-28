package rest.pkbe.api.dto.response.note;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * NoteDTO contiene datos simples pero necesarios para recibir o mandar una nota como respuesta del servidor.
 * La información recibida no puede estar vacía, lo cual se valida con jakarta
 * validation constraints
 * Se recibe/envia un título para la nota, su contenido y etiquetas (tags) para
 * identificar la nota
 */
public class NoteDTO {
    private Long id;
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    private String createdAt;
    private Set<@NotBlank String> tags;

}

package rest.pkbe.api.dto.request.note;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
/**
 * CreateNoteRequest es un DTO que contiene datos simples pero necesarios para crear una nota.
 * La información recibida no puede estar vacía, lo cual se valida con jakarta validation constraints
 * Se recibe un título para la nota, su contenido y etiquetas (tags) para identificar la nota
 */
public class CreateNoteRequest {
    @NotBlank(message = "La nota debe de tener título")
    private String title;
    @NotBlank(message = "No se puede crear una nota vacía")
    private String content;
    @NotEmpty(message = "La nota debe contener al menos una etiqueta")
    private Set<@NotBlank String> tags;
}

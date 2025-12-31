package rest.pkbe.exception.dto;

import java.time.LocalDateTime;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionResponse {
    
    private LocalDateTime timeStamp;
    private int status;
    private String error;
    private String message;
    private String path;
    

}

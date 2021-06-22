package com.glivion.backend.exception.handler;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class ApiError {

    private HttpStatus status;
    private String message;
    private String debugMessage;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;
    private Map<String, String> subErrors;

    private ApiError() {
        timestamp = LocalDateTime.now();
    }

    protected ApiError(HttpStatus status) {
        this();
        this.status = status;
    }

    protected ApiError(HttpStatus status, Throwable ex) {
        this(status);
        this.message = "Unexpected error";
        this.debugMessage = ex.getLocalizedMessage();
    }

    protected ApiError(HttpStatus status, String message, Throwable ex) {
        this(status,ex);
        this.message = message;
        this.debugMessage = ex.getLocalizedMessage();
    }
    protected ApiError(HttpStatus status, String message, Map<String, String> subErrors){
        this(status);
        this.message = message;
        this.subErrors = subErrors;
    }
}

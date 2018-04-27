package io.github.yoshikawaa.app.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {
    private String message;
    private List<Error> errors;
    @JsonProperty("documentation_url")
    private String documentationUrl;
    
    @Getter
    @Setter
    public static class Error {
        private String resource;
        private String field;
        private String code;
    }
}

package io.github.yoshikawaa.app.githubmanager.api.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.github.yoshikawaa.app.githubmanager.api.entity.Error;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {
    private String message;
    private List<Error> errors;
    @JsonProperty("documentation_url")
    private String documentationUrl;
}

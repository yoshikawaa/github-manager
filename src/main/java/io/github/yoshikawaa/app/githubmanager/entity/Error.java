package io.github.yoshikawaa.app.githubmanager.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Error {
    private String resource;
    private String field;
    private String code;
    private String message;
}

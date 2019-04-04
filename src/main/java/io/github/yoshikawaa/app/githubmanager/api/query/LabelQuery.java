package io.github.yoshikawaa.app.githubmanager.api.query;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LabelQuery {
    @NotEmpty
    private String name;
    @NotEmpty
    @Pattern(regexp = "[0-9a-fA-F]{6}")
    private String color;
    private String description; // not found...
}

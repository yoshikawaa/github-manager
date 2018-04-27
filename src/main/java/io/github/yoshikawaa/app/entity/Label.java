package io.github.yoshikawaa.app.entity;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Label {
    private int id;
    @NotEmpty
    private String name;
    private String description; // not found...
    @NotEmpty
    @Pattern(regexp = "[0-9a-fA-F]{6}")
    private String color;
}

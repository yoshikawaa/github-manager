package io.github.yoshikawaa.app.entity.query;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SearchQuery {
    @NotEmpty
    private String q;
    @Pattern(regexp = "(|comments|created|updated)")
    private String sort;
    @Pattern(regexp = "(|asc|desc)")
    private String order;
}

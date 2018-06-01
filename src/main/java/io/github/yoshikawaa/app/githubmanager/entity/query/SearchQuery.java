package io.github.yoshikawaa.app.githubmanager.entity.query;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
    @Min(1)
    private int page;
    @Min(10)
    @Max(100)
    private int perPage;
}

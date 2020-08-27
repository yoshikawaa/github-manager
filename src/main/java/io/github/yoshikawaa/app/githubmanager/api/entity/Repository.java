package io.github.yoshikawaa.app.githubmanager.api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Repository {
    private long id;
    private String name;
    private String description;
    @JsonProperty("html_url")
    private String htmlUrl;
    @JsonProperty("has_issues")
    private boolean hasIssues;
}

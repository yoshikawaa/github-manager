package io.github.yoshikawaa.app.githubmanager.api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Assignee {
    private long id;
    private String login;
    private String type;
    @JsonProperty("html_url")
    private String htmlUrl;
    @JsonProperty("avatar_url")
    private String avatarUrl;
}

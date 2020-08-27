package io.github.yoshikawaa.app.githubmanager.api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Organization {
    private long id;
    private String login;
    private String description;
    private String url;
    @JsonProperty("avatar_url")
    private String avatarUrl;
}

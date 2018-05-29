package io.github.yoshikawaa.app.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Assignee {
    private int id;
    private String login;
    private String type;
    @JsonProperty("html_url")
    private String htmlUrl;
    @JsonProperty("avatar_url")
    private String avatarUrl;
}

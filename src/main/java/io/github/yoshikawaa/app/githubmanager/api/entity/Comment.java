package io.github.yoshikawaa.app.githubmanager.api.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Comment {
    private int id;
    private String body;
    @JsonProperty("html_url")
    private String htmlUrl;
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    private User user;

    @JsonIgnore
    private String category;
}

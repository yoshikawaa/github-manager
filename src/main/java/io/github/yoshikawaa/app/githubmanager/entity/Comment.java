package io.github.yoshikawaa.app.githubmanager.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Comment {
    private int id;
    private String body;
    @JsonProperty("html_url")
    private String htmlUrl;
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    @JsonIgnore
    private String category;
    
    private User user;
}

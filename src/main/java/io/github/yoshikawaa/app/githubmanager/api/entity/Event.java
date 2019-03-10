package io.github.yoshikawaa.app.githubmanager.api.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Event {
    private int id;
    @JsonProperty("node_id")
    private String nodeId;
    private String url;
    private User actor;
    private String event;
    @JsonProperty("commit_id")
    private String commitId;
    @JsonProperty("commit_url")
    private String commitUrl;
    @JsonProperty("created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime createdAt;
}

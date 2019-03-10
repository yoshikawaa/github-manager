package io.github.yoshikawaa.app.githubmanager.api.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Issue {
    private int id;
    private int number;
    private String title;
    private String body;
    @JsonProperty("html_url")
    private String htmlUrl;
    private String state;
    private boolean merged;
    private boolean mergeable;
    @JsonProperty("created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime createdAt;
    @JsonProperty("updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime updatedAt;
    @JsonProperty("closed_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime closedAt;
    @JsonProperty("merged_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime mergedAt;
    private int comments;
    
    private User user;
    private List<Label> labels;
    private List<Assignee> assignees;
    private Milestone milestone;
    @JsonProperty("pull_request")
    private PullRequest pullRequest;
    
    @JsonIgnore
    private String owner;
    @JsonIgnore
    private String repo;
    
    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
        if (StringUtils.hasText(htmlUrl)) {
            String[] paths = htmlUrl.split("/");
            this.owner = paths[3];
            this.repo = paths[4];
        }
    }
    
    public String is() {
        return Objects.equals(state, "closed") ? state
                : merged ? "merged"
                : mergeable ? "mergeable"
                : state;
    }
}

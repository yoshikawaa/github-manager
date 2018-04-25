package io.github.yoshikawaa.app.entity;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Milestone {
    private int id;
    private int number;
    private String title;
    private String description;
    @JsonProperty("html_url")
    private String htmlUrl;
    @JsonProperty("due_on")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm")
    private LocalDateTime dueOn;
    private String state;
    @JsonProperty("open_issues")
    private int openIssues;
    @JsonProperty("closed_issues")
    private int closedIssues;
}

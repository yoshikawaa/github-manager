package io.github.yoshikawaa.app.githubmanager.api.query;

import java.time.LocalDateTime;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MilestoneQuery {
    @NotEmpty
    private String title;
    @NotEmpty
    @Pattern(regexp = "(open|closed)")
    private String state;
    private String description;
    @NotNull
    @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm")
    @JsonProperty("due_on")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime dueOn;
}

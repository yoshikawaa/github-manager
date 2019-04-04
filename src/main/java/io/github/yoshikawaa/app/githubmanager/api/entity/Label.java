package io.github.yoshikawaa.app.githubmanager.api.entity;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Label {
    private int id;
    private String name;
    private String description; // not found...
    @JsonProperty("url")
    private String htmlUrl;
    private String color;
    
    public void setHtmlUrl(String url) {
        this.htmlUrl = StringUtils.hasText(url) ? url.replaceFirst("api.github.com/repos/", "github.com/") : url;
    }
}

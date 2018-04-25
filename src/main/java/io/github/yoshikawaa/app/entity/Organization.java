package io.github.yoshikawaa.app.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Organization {
    private int id;
    private String login;
    private String description;
    private String url;
}

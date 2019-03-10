package io.github.yoshikawaa.app.githubmanager.api.query;

import javax.validation.constraints.AssertFalse;

import org.thymeleaf.util.ArrayUtils;
import org.thymeleaf.util.StringUtils;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReportSearchQuery {
    // search by user & repository
    private String repo;

    // search by category
    private String type;
    private String[] label;

    // for example <2012-10-01
    private String created;
    private String updated;
    private String merged;
    private String closed;

    @AssertFalse(message = "must be not blank all field.")
    public boolean isBlankFields() {
        return StringUtils.isEmptyOrWhitespace(repo)
                && StringUtils.isEmptyOrWhitespace(type)
                && ArrayUtils.isEmpty(label)
                && StringUtils.isEmptyOrWhitespace(created)
                && StringUtils.isEmptyOrWhitespace(updated)
                && StringUtils.isEmptyOrWhitespace(merged)
                && StringUtils.isEmptyOrWhitespace(closed);
    }
}

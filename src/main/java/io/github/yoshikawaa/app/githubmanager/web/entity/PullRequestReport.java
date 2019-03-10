package io.github.yoshikawaa.app.githubmanager.web.entity;

import java.util.List;
import java.util.Map;

import io.github.yoshikawaa.app.githubmanager.api.entity.Issue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class PullRequestReport {
	private Issue pull;
	private Map<String, Integer> statusSummary;
	private List<Integer> relatedIssueNumbers;
}

package io.github.yoshikawaa.app.githubmanager.api;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;

import io.github.yoshikawaa.app.githubmanager.api.entity.Comment;
import io.github.yoshikawaa.app.githubmanager.api.entity.Issue;
import io.github.yoshikawaa.app.githubmanager.api.entity.Label;
import io.github.yoshikawaa.app.githubmanager.api.entity.Milestone;
import io.github.yoshikawaa.app.githubmanager.api.entity.Organization;
import io.github.yoshikawaa.app.githubmanager.api.entity.Repository;
import io.github.yoshikawaa.app.githubmanager.api.helper.QueryBuilder;
import io.github.yoshikawaa.app.githubmanager.api.query.ReportSearchQuery;
import io.github.yoshikawaa.app.githubmanager.api.query.SearchQuery;
import io.github.yoshikawaa.app.githubmanager.api.response.IssuesResponse;
import io.github.yoshikawaa.app.githubmanager.core.pagination.Page;
import lombok.Getter;

@Service
public class GithubApiService {

    @Autowired
    private RestOperations restOperations;

    @Value("${app.url.github-api}")
    @Getter
    private String baseUrl;

    @Value("${app.pagination.per-page:30}")
    @Getter
    private int perPage;

    public Organization[] orgs() {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/user/orgs")
                .queryParam("per_page", perPage)
                .build()
                .toUri();
        return restOperations.getForEntity(uri, Organization[].class).getBody();
	}
    
    public Repository[] repos(String owner, Authentication authentication) {
        return owner.equals(authentication.getName()) ? userRepos(owner) : orgsRepos(owner);
    }
    
    public Repository[] userRepos(String owner) {
    	return repos("/users/{owner}/repos", owner);
    }

    public Repository[] orgsRepos(String owner) {
    	return repos("/orgs/{owner}/repos", owner);
    }
    
    private Repository[] repos(String path, String owner) {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .path(path)
                .queryParam("per_page", perPage)
                .build(owner);
        return restOperations.getForEntity(uri, Repository[].class).getBody();
    }
    
    public Label[] labels(String owner, String repo) {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/repos/{owner}/{repo}/labels")
                .queryParam("per_page", perPage)
                .build(owner, repo);
        return restOperations.getForEntity(uri, Label[].class).getBody();
    }
    
    public void labelsCreate(String owner, String repo, Label label) {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/repos/{owner}/{repo}/labels")
                .build(owner, repo);
        restOperations.postForEntity(uri, label, Label.class);
    }
    
    public void labelsUpdate(String owner, String repo, String name, Label label) {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/repos/{owner}/{repo}/labels/{name}")
                .build(owner, repo, name);
        restOperations.patchForObject(uri, label, Label.class);
    }
    
    public void labelsDelete(String owner, String repo, String name) {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/repos/{owner}/{repo}/labels/{name}")
                .build(owner, repo, name);
        restOperations.delete(uri);
    }
    
    public Milestone[] milestones(String owner, String repo) {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/repos/{owner}/{repo}/milestones")
                .queryParam("state", "all")
                .queryParam("direction", "desc")
                .queryParam("per_page", perPage)
                .build(owner, repo);
        return restOperations.getForEntity(uri, Milestone[].class).getBody();
    }
    
    public void milestonesCreate(String owner, String repo, Milestone milestone) {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/repos/{owner}/{repo}/milestones")
                .build(owner, repo);
        restOperations.postForEntity(uri, milestone, Milestone.class);
    }
    
    public void milestonesUpdate(String owner, String repo, int number, Milestone milestone) {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/repos/{owner}/{repo}/milestones/{number}")
                .build(owner, repo, number);
        restOperations.patchForObject(uri, milestone, Milestone.class);
    }
    
    public void milestonesDelete(String owner, String repo, int number) {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/repos/{owner}/{repo}/milestones/{number}")
                .build(owner, repo, number);
        restOperations.delete(uri);
    }
    
    public Page<Issue> issues(SearchQuery query) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/search/issues")
                .queryParam("q", query.getQ())
                .queryParam("page", query.getPage())
                .queryParam("per_page", query.getPerPage());
        if (StringUtils.hasText(query.getSort())) {
            builder.queryParam("sort", query.getSort());
        }
        if (StringUtils.hasText(query.getOrder())) {
            builder.queryParam("order", query.getOrder());
        }
        URI uri = builder.build().toUri();
        IssuesResponse res = restOperations.getForEntity(uri, IssuesResponse.class).getBody();
        return new Page<Issue>(res.getItems(), query.getPage(), query.getPerPage(), res.getTotalCount());
    }
    
    public IssuesResponse issues(ReportSearchQuery query, int page) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/search/issues")
                .queryParam("q", QueryBuilder.from(query).build())
                .queryParam("page", page)
                .queryParam("per_page", perPage);
        URI uri = builder.build().toUri();
        return restOperations.getForEntity(uri, IssuesResponse.class).getBody();
    }
    
    public Issue issue(String owner, String repo, int number) {
        URI issueUrl = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/repos/{owner}/{repo}/issues/{number}")
                .build(owner, repo, number);
        return restOperations.getForEntity(issueUrl, Issue.class).getBody();
    }
    
    public Comment[] issueComments(String owner, String repo, int number) {
        URI commentUri = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/repos/{owner}/{repo}/issues/{number}/comments")
                .queryParam("per_page", perPage)
                .build(owner, repo, number);
        return restOperations.getForEntity(commentUri, Comment[].class).getBody();
    }
    
    public Comment[] reviewComments(String owner, String repo, int number) {
        URI reviewCommentUri = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/repos/{owner}/{repo}/pulls/{number}/comments")
                .queryParam("per_page", perPage)
                .build(owner, repo, number);
    	return restOperations.getForEntity(reviewCommentUri, Comment[].class).getBody();
    }
    
}

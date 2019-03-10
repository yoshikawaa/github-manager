package io.github.yoshikawaa.app.githubmanager.web;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.common.collect.ImmutableMap;

import io.github.yoshikawaa.app.githubmanager.analysis.Counter;
import io.github.yoshikawaa.app.githubmanager.analysis.Referancer;
import io.github.yoshikawaa.app.githubmanager.core.web.QueryBuilder;
import io.github.yoshikawaa.app.githubmanager.entity.Comment;
import io.github.yoshikawaa.app.githubmanager.entity.Issue;
import io.github.yoshikawaa.app.githubmanager.entity.Repository;
import io.github.yoshikawaa.app.githubmanager.entity.query.QualityReportQuery;
import io.github.yoshikawaa.app.githubmanager.entity.report.PullRequestReport;
import io.github.yoshikawaa.app.githubmanager.entity.response.IssuesResponse;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/quality-report/{owner}")
@Slf4j
public class QualityReportController extends AbstractRestClientController {

    private static final Issue DEFAULT_ISSUE = new Issue();

    @Autowired
    @Qualifier("issueManageRepos")
    private Map<String, String> issueManageRepos;

    @Autowired
    private Counter<String> counter;

    @Autowired
    private Referancer referencer;

    @GetMapping
    public String view(Model model, @PathVariable("owner") String owner, Authentication authentication,
            QualityReportQuery query) {
        URI reposUri = UriComponentsBuilder.fromUriString(baseUrl)
                .path(owner.equals(authentication.getName()) ? "/users/{org}/repos" : "/orgs/{org}/repos")
                .queryParam("per_page", perPage)
                .build(owner);
        model.addAttribute("repos", restOperations.getForEntity(reposUri, Repository[].class).getBody());
        return "qualityReport";
    }

    @GetMapping(params = "org", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public String orgReport(Model model, @PathVariable("owner") String owner, @RequestParam("repos") List<String> repos,
            @Validated QualityReportQuery query) {
        Map<String, MultiValueMap<Issue, PullRequestReport>> reports = new HashMap<>();

        for (String repo : repos) {
            MultiValueMap<Issue, PullRequestReport> repositoryReport = new LinkedMultiValueMap<>();
            for (Issue pullRequest : getPullRequests(owner, repo, query)) {
                PullRequestReport report = getPullRequestReport(owner, repo, pullRequest.getNumber());
                if (report.getIssueNumbers().isEmpty()) {
                    repositoryReport.add(DEFAULT_ISSUE, report);
                } else {
                    repositoryReport.add(getIssue(owner, repo, report.getIssueNumbers().get(0)), report);
                }
            }
            reports.put(repo, repositoryReport);
        }

        model.addAttribute("reports", reports);
        model.addAttribute("reportName", owner);
        return "qualityReportView";
    }

    @GetMapping(params = "repo", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public String repoReport(Model model, @PathVariable("owner") String owner, @RequestParam("repo") String repo,
            @Validated QualityReportQuery query) {
        Map<String, MultiValueMap<Issue, PullRequestReport>> reports = new HashMap<>();

        MultiValueMap<Issue, PullRequestReport> repositoryReport = new LinkedMultiValueMap<>();
        for (Issue pullRequest : getPullRequests(owner, repo, query)) {
            PullRequestReport report = getPullRequestReport(owner, repo, pullRequest.getNumber());
            if (report.getIssueNumbers().isEmpty()) {
                repositoryReport.add(DEFAULT_ISSUE, report);
            } else {
                repositoryReport.add(getIssue(owner, repo, report.getIssueNumbers().get(0)), report);
            }
        }
        reports.put(repo, repositoryReport);

        model.addAttribute("reports", reports);
        model.addAttribute("reportName", repo);
        return "qualityReportView";
    }

    @GetMapping(path = "/{repo}/{number}", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public String singleReoprt(Model model, @PathVariable("owner") String owner, @PathVariable("repo") String repo,
            @PathVariable("number") int number) {
        MultiValueMap<Issue, PullRequestReport> repositoryReport = new LinkedMultiValueMap<>();

        PullRequestReport report = getPullRequestReport(owner, repo, number);
        if (report.getIssueNumbers().isEmpty()) {
            repositoryReport.add(DEFAULT_ISSUE, report);
        } else {
            repositoryReport.add(getIssue(owner, repo, number), report);
        }

        model.addAttribute("reports", ImmutableMap.of(repo, repositoryReport));
        model.addAttribute("reportName", String.join("#", repo, String.valueOf(number)));
        return "qualityReportView";
    }

    private List<Issue> getPullRequests(String owner, String repo, QualityReportQuery query) {
        query.setRepo(String.join("/", owner, repo));
        query.setType("pr");

        int page = 0;
        List<Issue> pullRequests = new ArrayList<>();
        IssuesResponse res;

        do {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl)
                    .path("/search/issues")
                    .queryParam("q", QueryBuilder.from(query).build())
                    .queryParam("page", page)
                    .queryParam("per_page", perPage);
            URI uri = builder.build().toUri();
            res = restOperations.getForEntity(uri, IssuesResponse.class).getBody();
            pullRequests.addAll(res.getItems());
            page++;
        } while (pullRequests.size() < res.getTotalCount());

        return pullRequests;
    }

    private PullRequestReport getPullRequestReport(String owner, String repo, int number) {
        URI pullUrl = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/repos/{owner}/{repo}/issues/{number}")
                .build(owner, repo, number);
        URI commentUri = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/repos/{owner}/{repo}/issues/{number}/comments")
                .queryParam("per_page", perPage)
                .build(owner, repo, number);
        URI reviewCommentUri = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/repos/{owner}/{repo}/pulls/{number}/comments")
                .queryParam("per_page", perPage)
                .build(owner, repo, number);

        Issue pull = restOperations.getForEntity(pullUrl, Issue.class).getBody();
        List<Comment> comments = Stream
                .concat(Arrays.stream(restOperations.getForEntity(commentUri, Comment[].class).getBody()),
                        Arrays.stream(restOperations.getForEntity(reviewCommentUri, Comment[].class).getBody()))
                .sorted(comparing(Comment::getCreatedAt))
                .collect(toList());
        Map<String, Integer> statusSummary = counter
                .count(Stream.concat(Arrays.asList(pull.getBody()).stream(), comments.stream().map(c -> c.getBody()))
                        .collect(toList()));

        return new PullRequestReport(pull, statusSummary, referencer.numbers(pull.getTitle()));
    }

    private Issue getIssue(String owner, String repo, int number) {
        String issueRepo = issueManageRepos.containsKey(repo) ? issueManageRepos.get(repo) : repo;
        URI issueUrl = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/repos/{owner}/{repo}/issues/{number}")
                .build(owner, issueRepo, number);
        try {
        	return restOperations.getForEntity(issueUrl, Issue.class).getBody();
        } catch (RestClientException e) {
        	log.warn("get issue failed:{}:{}", owner + "/" + repo + "#" + number, e.getMessage());
        	return DEFAULT_ISSUE;
        }
    }

}

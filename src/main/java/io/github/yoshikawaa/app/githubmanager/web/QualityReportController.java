package io.github.yoshikawaa.app.githubmanager.web;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

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

import com.google.common.collect.ImmutableMap;

import io.github.yoshikawaa.app.githubmanager.analysis.CounterService;
import io.github.yoshikawaa.app.githubmanager.analysis.ExtractorService;
import io.github.yoshikawaa.app.githubmanager.api.entity.Comment;
import io.github.yoshikawaa.app.githubmanager.api.entity.Issue;
import io.github.yoshikawaa.app.githubmanager.api.query.ReportSearchQuery;
import io.github.yoshikawaa.app.githubmanager.api.response.IssuesResponse;
import io.github.yoshikawaa.app.githubmanager.web.entity.PullRequestReport;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/quality-report/{owner}")
@Slf4j
public class QualityReportController extends AbstractGithubApiController {

    private static final Issue DEFAULT_ISSUE = new Issue();

    @Autowired
    @Qualifier("issueManageRepos")
    private Map<String, String> issueManageRepos;

    @Autowired
    private CounterService counter;

    @Autowired
    private ExtractorService extractor;

    @GetMapping
    public String view(Model model, @PathVariable("owner") String owner, Authentication authentication,
            ReportSearchQuery query) {
        model.addAttribute("repos", api.repos(owner, authentication));
        return "qualityReport";
    }

    @GetMapping(params = "org", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public String orgReport(Model model, @PathVariable("owner") String owner, @RequestParam("repos") List<String> repos,
            @Validated ReportSearchQuery query) {
        Map<String, MultiValueMap<Issue, PullRequestReport>> reports = new HashMap<>();

        for (String repo : repos) {
            MultiValueMap<Issue, PullRequestReport> repositoryReport = new LinkedMultiValueMap<>();
            for (Issue pullRequest : getPullRequests(owner, repo, query)) {
                PullRequestReport report = getPullRequestReport(owner, repo, pullRequest.getNumber());
                repositoryReport.add(getIssue(owner, repo, report.getRelatedIssueNumbers()), report);
            }
            reports.put(repo, repositoryReport);
        }

        model.addAttribute("reports", reports);
        model.addAttribute("reportName", owner);
        return "qualityReportView";
    }

    @GetMapping(params = "repo", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public String repoReport(Model model, @PathVariable("owner") String owner, @RequestParam("repo") String repo,
            @Validated ReportSearchQuery query) {
        Map<String, MultiValueMap<Issue, PullRequestReport>> reports = new HashMap<>();

        MultiValueMap<Issue, PullRequestReport> repositoryReport = new LinkedMultiValueMap<>();
        for (Issue pullRequest : getPullRequests(owner, repo, query)) {
            PullRequestReport report = getPullRequestReport(owner, repo, pullRequest.getNumber());
            repositoryReport.add(getIssue(owner, repo, report.getRelatedIssueNumbers()), report);
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
        repositoryReport.add(getIssue(owner, repo, report.getRelatedIssueNumbers()), report);

        model.addAttribute("reports", ImmutableMap.of(repo, repositoryReport));
        model.addAttribute("reportName", String.join("#", repo, String.valueOf(number)));
        return "qualityReportView";
    }

    private List<Issue> getPullRequests(String owner, String repo, ReportSearchQuery query) {
        query.setRepo(String.join("/", owner, repo));
        query.setType("pr");

        int page = 0;
        List<Issue> pullRequests = new ArrayList<>();
        IssuesResponse res;
        do {
            res = api.issues(query, page);
            pullRequests.addAll(res.getItems());
            page++;
        } while (pullRequests.size() < res.getTotalCount());

        return pullRequests;
    }

    private PullRequestReport getPullRequestReport(String owner, String repo, int number) {
        Issue pull = api.issue(owner, repo, number);
        List<Comment> comments = Stream
                .concat(Arrays.stream(api.issueComments(owner, repo, number)),
                        Arrays.stream(api.reviewComments(owner, repo, number)))
                .sorted(comparing(Comment::getCreatedAt))
                .collect(toList());
        Map<String, Integer> statusSummary = counter
                .count(Stream.concat(Stream.of(pull.getBody()), comments.stream().map(c -> c.getBody()))
                        .collect(toList()));
        return new PullRequestReport(pull, statusSummary, extractor.numbers(pull.getTitle()));
    }

    private Issue getIssue(String owner, String repo, List<Integer> numbers) {
        String issueRepo = issueManageRepos.containsKey(repo) ? issueManageRepos.get(repo) : repo;
        for (Integer number : numbers) {
            try {
                return api.issue(owner, issueRepo, number);
            } catch (RestClientException e) {
                log.warn("failed to get related issue:{}:{}", owner + "/" + repo + "#" + number, e.getMessage());
            }
        }
        return DEFAULT_ISSUE;
    }

}

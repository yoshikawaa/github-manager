package io.github.yoshikawaa.app.githubmanager.web;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

import io.github.yoshikawaa.app.githubmanager.analysis.Counter;
import io.github.yoshikawaa.app.githubmanager.entity.Comment;
import io.github.yoshikawaa.app.githubmanager.entity.Issue;

@Controller
@RequestMapping("/repos/{owner}/{repo}/issues")
public class IssuesController extends AbstractRestClientController {

    @Autowired
    private Counter<String> counter;

    @GetMapping("/{number}")
    public String pull(Model model, @PathVariable("owner") String owner, @PathVariable("repo") String repo,
            @PathVariable("number") int number) {
        URI issueUrl = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/repos/{owner}/{repo}/issues/{number}")
                .build(owner, repo, number);
        URI commentUri = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/repos/{owner}/{repo}/issues/{number}/comments")
                .queryParam("per_page", perPage)
                .build(owner, repo, number);
        
        Issue issue = restOperations.getForEntity(issueUrl, Issue.class).getBody();
        List<Comment> comments = Arrays.stream(restOperations.getForEntity(commentUri, Comment[].class).getBody())
                .sorted(comparing(Comment::getCreatedAt)).collect(toList());
        Map<String, Integer> statusSummary = counter.count(Stream
                .concat(Arrays.asList(issue.getBody()).stream(),
                        comments.stream().map(c -> c.getBody()))
                .collect(toList()));
        
        model.addAttribute("issue", issue);
        model.addAttribute("comments", comments);
        model.addAttribute("statusSummary", statusSummary);
        return "issue";
    }

}

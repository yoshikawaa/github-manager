package io.github.yoshikawaa.app.githubmanager.web;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

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

import io.github.yoshikawaa.app.githubmanager.analysis.CounterService;
import io.github.yoshikawaa.app.githubmanager.api.entity.Comment;
import io.github.yoshikawaa.app.githubmanager.api.entity.Issue;

@Controller
@RequestMapping("/repos/{owner}/{repo}/issues")
public class IssuesController extends AbstractGithubApiController {

    @Autowired
    private CounterService counter;

    @GetMapping("/{number}")
    public String issue(Model model, @PathVariable("owner") String owner, @PathVariable("repo") String repo,
            @PathVariable("number") int number) {
        Issue issue = api.issue(owner, repo, number);
        List<Comment> comments = Arrays.stream(api.issueComments(owner, repo, number))
                .sorted(comparing(Comment::getCreatedAt))
                .collect(toList());
        Map<String, Integer> statusSummary = counter
                .count(Stream.concat(Stream.of(issue.getBody()), comments.stream().map(c -> c.getBody()))
                        .collect(toList()));

        model.addAttribute("issue", issue);
        model.addAttribute("comments", comments);
        model.addAttribute("statusSummary", statusSummary);
        return "issue";
    }

}

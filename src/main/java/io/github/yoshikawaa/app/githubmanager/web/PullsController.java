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

import io.github.yoshikawaa.app.githubmanager.analysis.CategorizerService;
import io.github.yoshikawaa.app.githubmanager.analysis.CounterService;
import io.github.yoshikawaa.app.githubmanager.api.entity.Comment;
import io.github.yoshikawaa.app.githubmanager.api.entity.Issue;

@Controller
@RequestMapping("/repos/{owner}/{repo}/pulls")
public class PullsController extends AbstractGithubApiController {

    @Autowired
    private CategorizerService categorizer;

    @Autowired
    private CounterService counter;

    @GetMapping("/{number}")
    public String pull(Model model, @PathVariable("owner") String owner, @PathVariable("repo") String repo,
            @PathVariable("number") int number) {
        Issue pull = api.issue(owner, repo, number);
        List<Comment> comments = Stream
                .concat(Arrays.stream(api.issueComments(owner, repo, number)),
                        Arrays.stream(api.reviewComments(owner, repo, number)))
                .sorted(comparing(Comment::getCreatedAt))
                .collect(toList());
        Map<String, Integer> commentSummary = categorizer.categorize(comments);
        Map<String, Integer> statusSummary = counter
                .count(Stream.concat(Stream.of(pull.getBody()), comments.stream().map(c -> c.getBody()))
                        .collect(toList()));

        model.addAttribute("pull", pull);
        model.addAttribute("comments", comments);
        model.addAttribute("commentSummary", commentSummary);
        model.addAttribute("statusSummary", statusSummary);
        return "pull";
    }

}

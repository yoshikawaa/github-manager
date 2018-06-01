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

import io.github.yoshikawaa.app.githubmanager.analysis.Categorizer;
import io.github.yoshikawaa.app.githubmanager.analysis.Counter;
import io.github.yoshikawaa.app.githubmanager.entity.Comment;
import io.github.yoshikawaa.app.githubmanager.entity.Issue;

@Controller
@RequestMapping("/repos/{owner}/{repo}/pulls")
public class PullsController extends AbstractRestClientController {

    @Autowired
    private Categorizer<Comment> categorizer;

    @Autowired
    private Counter<String> counter;

    @GetMapping("/{number}")
    public String pull(Model model, @PathVariable("owner") String owner, @PathVariable("repo") String repo,
            @PathVariable("number") int number) {
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
                .sorted(comparing(Comment::getCreatedAt)).collect(toList());
        Map<String, Integer> commentSummary = categorizer.categorize(comments);
        Map<String, Integer> statusSummary = counter.count(Stream
                .concat(Arrays.asList(pull.getBody()).stream(),
                        comments.stream().map(c -> c.getBody()))
                .collect(toList()));

        model.addAttribute("pull", pull);
        model.addAttribute("comments", comments);
        model.addAttribute("commentSummary", commentSummary);
        model.addAttribute("statusSummary", statusSummary);
        return "pull";
    }

}

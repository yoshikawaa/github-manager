package io.github.yoshikawaa.app.web;

import static java.util.Comparator.comparing;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

import io.github.yoshikawaa.app.analysis.CommentBodyCategorizer;
import io.github.yoshikawaa.app.entity.Comment;
import io.github.yoshikawaa.app.entity.query.SearchQuery;

@Controller
@RequestMapping("/repos/{owner}/{repo}/issues")
public class IssuesController extends AbstractRestClientController {

    @Autowired
    private CommentBodyCategorizer categorizer;
    
    @GetMapping("/{number}")
    public String comments(Model model, @PathVariable("owner") String owner, @PathVariable("repo") String repo,
            @Validated SearchQuery query, @PathVariable("number") int number) {
        URI commentUri = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/repos/{owner}/{repo}/issues/{number}/comments")
                .queryParam("per_page", perPage)
                .build(owner, repo, number);        

        List<Comment> comments = Arrays.asList(restOperations.getForEntity(commentUri, Comment[].class).getBody());

        Map<String, Integer> statistics = categorizer.Categorize(comments);
        statistics.entrySet().forEach(e -> System.out.println(String.format("%s:%s", e.getKey(), e.getValue())));
        comments.stream().sorted(comparing(Comment::getCreatedAt)).forEach(c -> System.out.println(c));
        
        return "redirect:/repos/{owner}/{repo}/issues";
    }

}

package io.github.yoshikawaa.app.web;

import java.net.URI;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

import io.github.yoshikawaa.app.entity.Milestone;

@Controller
@RequestMapping("/repos/{owner}/{repo}/milestones")
public class MilestonesController extends AbstractRestClientController {

    @GetMapping
    public String milestones(Model model, @PathVariable("owner") String owner, @PathVariable("repo") String repo) {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/repos/{owner}/{repo}/milestones")
                .queryParam("state", "all")
                .queryParam("direction", "desc")
                .queryParam("per_page", 100)
                .build(owner, repo);
        model.addAttribute("milestones", restOperations.getForEntity(uri, Milestone[].class).getBody());
        model.addAttribute("owner", owner);
        model.addAttribute("repo", repo);
        return "milestones";
    }

    @PostMapping
    public String milestonesCreate(@PathVariable("owner") String owner, @PathVariable("repo") String repo,
            @Validated Milestone milestone) {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/repos/{owner}/{repo}/milestones")
                .build(owner, repo);
        restOperations.postForEntity(uri, milestone, Milestone.class);
        return "redirect:/repos/{owner}/{repo}/milestones";
    }
    
    @PostMapping(path = "/{number}", params = "update")
    public String milestonesUpdate(@PathVariable("owner") String owner, @PathVariable("repo") String repo,
            @PathVariable("number") int number, @Validated Milestone milestone) {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/repos/{owner}/{repo}/milestones/{number}")
                .build(owner, repo, number);
        restOperations.patchForObject(uri, milestone, Milestone.class);
        return "redirect:/repos/{owner}/{repo}/milestones";
    }

    @PostMapping(path = "/{number}", params = "delete")
    public String milestonesDelete(@PathVariable("owner") String owner, @PathVariable("repo") String repo,
            @PathVariable("number") int number) {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/repos/{owner}/{repo}/milestones/{number}")
                .build(owner, repo, number);
        restOperations.delete(uri);
        return "redirect:/repos/{owner}/{repo}/milestones";
    }

}

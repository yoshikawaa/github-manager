package io.github.yoshikawaa.app.web;

import java.net.URI;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import io.github.yoshikawaa.app.entity.Milestone;
import io.github.yoshikawaa.app.entity.Repository;

@Controller
@RequestMapping("/orgs/{owner}/milestones")
public class OrgMilestonesController extends AbstractRestClientController {

    @GetMapping
    public String milestones(Model model, @PathVariable("owner") String owner, Authentication authentication) {
        URI reposUri = UriComponentsBuilder.fromUriString(baseUrl)
                .path(owner.equals(authentication.getName()) ? "/users/{org}/repos" : "/orgs/{org}/repos")
                .queryParam("per_page", 100)
                .build(owner);
        model.addAttribute("milestonesMap",
                Arrays.stream(restOperations.getForEntity(reposUri, Repository[].class).getBody())
                        .collect(Collectors.toMap(Repository::getName, repo -> {
                            URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                                    .path("/repos/{owner}/{repo}/milestones")
                                    .queryParam("state", "all")
                                    .queryParam("direction", "desc")
                                    .queryParam("per_page", 100)
                                    .build(owner, repo.getName());
                            return restOperations.getForEntity(uri, Milestone[].class).getBody();
                        })));
        model.addAttribute("owner", owner);
        return "orgMilestones";
    }

    @PostMapping
    public String milestonesCreate(@PathVariable("owner") String owner, @RequestParam("repos") String[] repos,
            Milestone milestone) {
        Arrays.stream(repos).forEach(repo -> {
            URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                    .path("/repos/{owner}/{repo}/milestones")
                    .build(owner, repo);
            restOperations.postForEntity(uri, milestone, Milestone.class);
        });
        return "redirect:/orgs/{owner}/milestones";
    }

}

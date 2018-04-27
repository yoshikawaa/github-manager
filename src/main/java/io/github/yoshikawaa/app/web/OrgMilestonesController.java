package io.github.yoshikawaa.app.web;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.util.UriComponentsBuilder;

import io.github.yoshikawaa.app.entity.Milestone;
import io.github.yoshikawaa.app.entity.Repository;

@Controller
@RequestMapping("/orgs/{owner}/milestones")
@SessionAttributes("milestonesMap")
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
    public String milestonesCreate(@ModelAttribute("milestonesMap") Map<String, Milestone[]> milestonesMap,
            @PathVariable("owner") String owner,
            @RequestParam("repos") List<String> repos,
            @Validated Milestone milestone) {
        milestonesMap.entrySet().stream()
            .filter(e -> repos.contains(e.getKey())
                    && !Arrays.stream(e.getValue()).anyMatch(m -> Objects.equals(m.getTitle(), milestone.getTitle())))
            .map(Entry::getKey)
            .forEach(repo -> {
                URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                        .path("/repos/{owner}/{repo}/milestones")
                        .build(owner, repo);
                restOperations.postForEntity(uri, milestone, Milestone.class);
        });
        return "redirect:/orgs/{owner}/milestones?complete";
    }

    @PostMapping(path = "/{title}", params = "update")
    public String milestonesUpdate(@ModelAttribute("milestonesMap") Map<String, Milestone[]> milestonesMap,
            @PathVariable("owner") String owner,
            @RequestParam("repos") List<String> repos,
            @PathVariable("title") String title,
            @Validated Milestone milestone) {
        milestonesMap.entrySet().stream()
            .filter(e -> repos.contains(e.getKey()))
            .forEach(entry -> {
                Arrays.stream(entry.getValue())
                    .filter(m -> Objects.equals(m.getTitle(), title))
                    .map(Milestone::getNumber)
                    .findFirst()
                    .ifPresent(number -> {
                        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                                .path("/repos/{owner}/{repo}/milestones/{number}")
                                .build(owner, entry.getKey(), number);
                        restOperations.patchForObject(uri, milestone, Milestone.class);
                    });
        });
        return "redirect:/orgs/{owner}/milestones?complete";
    }

    @PostMapping(path = "/{title}", params = "delete")
    public String milestonesDelete(@ModelAttribute("milestonesMap") Map<String, Milestone[]> milestonesMap,
            @PathVariable("owner") String owner,
            @RequestParam("repos") List<String> repos,
            @PathVariable("title") String title) {
        milestonesMap.entrySet().stream()
            .filter(e -> repos.contains(e.getKey()))
            .forEach(entry -> {
                Arrays.stream(entry.getValue())
                    .filter(m -> Objects.equals(m.getTitle(), title))
                    .map(Milestone::getNumber)
                    .findFirst()
                    .ifPresent(number -> {
                        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                                .path("/repos/{owner}/{repo}/milestones/{number}")
                                .build(owner, entry.getKey(), number);
                        restOperations.delete(uri);
                    });
        });
        return "redirect:/orgs/{owner}/milestones?complete";
    }

    @GetMapping(params = "complete")
    public String sessionComplete(SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        return "redirect:/orgs/{owner}/milestones";
    }

}

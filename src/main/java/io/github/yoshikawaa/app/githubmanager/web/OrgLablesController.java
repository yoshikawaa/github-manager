package io.github.yoshikawaa.app.githubmanager.web;

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

import io.github.yoshikawaa.app.githubmanager.entity.Label;
import io.github.yoshikawaa.app.githubmanager.entity.Repository;

@Controller
@RequestMapping("/orgs/{owner}/labels")
@SessionAttributes("labelsMap")
public class OrgLablesController extends AbstractRestClientController {

    @GetMapping
    public String labels(Model model, @PathVariable("owner") String owner, Authentication authentication) {
        URI reposUri = UriComponentsBuilder.fromUriString(baseUrl)
                .path(owner.equals(authentication.getName()) ? "/users/{org}/repos" : "/orgs/{org}/repos")
                .queryParam("per_page", perPage)
                .build(owner);
        model.addAttribute("labelsMap",
                Arrays.stream(restOperations.getForEntity(reposUri, Repository[].class).getBody())
                        .collect(Collectors.toMap(Repository::getName, repo -> {
                            URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                                    .path("/repos/{owner}/{repo}/labels")
                                    .queryParam("per_page", perPage)
                                    .build(owner, repo.getName());
                            return restOperations.getForEntity(uri, Label[].class).getBody();
                        })));
        model.addAttribute("owner", owner);
        return "orgLabels";
    }

    @PostMapping
    public String labelsCreate(@ModelAttribute("labelsMap") Map<String, Label[]> labelsMap,
            @PathVariable("owner") String owner,
            @RequestParam("repos") List<String> repos,
            @Validated Label label) {
        labelsMap.entrySet().stream()
            .filter(e -> repos.contains(e.getKey())
                    && !Arrays.stream(e.getValue()).anyMatch(l -> Objects.equals(l.getName(), label.getName())))
            .map(Entry::getKey)
            .forEach(repo -> {
                URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                        .path("/repos/{owner}/{repo}/labels")
                        .build(owner, repo);
                restOperations.postForEntity(uri, label, Label.class);
        });
        return "redirect:/orgs/{owner}/labels?complete";
    }

    @PostMapping(path = "/{name}", params = "update")
    public String labelsUpdate(@ModelAttribute("labelsMap") Map<String, Label[]> labelsMap,
            @PathVariable("owner") String owner,
            @RequestParam("repos") List<String> repos,
            @PathVariable("name") String name,
            @Validated Label label) {
        labelsMap.entrySet().stream()
            .filter(e -> repos.contains(e.getKey())
                    && Arrays.stream(e.getValue()).anyMatch(l -> Objects.equals(l.getName(), name)))
            .map(Entry::getKey)
            .forEach(repo -> {
                URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                        .path("/repos/{owner}/{repo}/labels/{name}")
                        .build(owner, repo, name);
                restOperations.patchForObject(uri, label, Label.class);
        });
        return "redirect:/orgs/{owner}/labels?complete";
    }

    @PostMapping(path = "/{name}", params = "delete")
    public String labelsDelete(@ModelAttribute("labelsMap") Map<String, Label[]> labelsMap, 
            @PathVariable("owner") String owner,
            @RequestParam("repos") List<String> repos,
            @PathVariable("name") String name) {
        labelsMap.entrySet().stream()
            .filter(e -> repos.contains(e.getKey())
                    && Arrays.stream(e.getValue()).anyMatch(l -> Objects.equals(l.getName(), name)))
            .map(Entry::getKey)
            .forEach(repo -> {
                URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                        .path("/repos/{owner}/{repo}/labels/{name}")
                        .build(owner, repo, name);
                restOperations.delete(uri);
        });
        return "redirect:/orgs/{owner}/labels?complete";
    }

    @GetMapping(params = "complete")
    public String sessionComplete(SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        return "redirect:/orgs/{owner}/labels";
    }

}

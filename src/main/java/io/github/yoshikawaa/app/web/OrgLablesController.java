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

import io.github.yoshikawaa.app.entity.Label;
import io.github.yoshikawaa.app.entity.Repository;

@Controller
@RequestMapping("/orgs/{owner}/labels")
public class OrgLablesController extends AbstractRestClientController {

    @GetMapping
    public String labels(Model model, @PathVariable("owner") String owner, Authentication authentication) {
        URI reposUri = UriComponentsBuilder.fromUriString(baseUrl)
                .path(owner.equals(authentication.getName()) ? "/users/{org}/repos" : "/orgs/{org}/repos")
                .queryParam("per_page", 100)
                .build(owner);
        model.addAttribute("labelsMap",
                Arrays.stream(restOperations.getForEntity(reposUri, Repository[].class).getBody())
                        .collect(Collectors.toMap(Repository::getName, repo -> {
                            URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                                    .path("/repos/{owner}/{repo}/labels")
                                    .queryParam("per_page", 100)
                                    .build(owner, repo.getName());
                            return restOperations.getForEntity(uri, Label[].class).getBody();
                        })));
        model.addAttribute("owner", owner);
        return "orgLabels";
    }

    @PostMapping
    public String labelsCreate(@PathVariable("owner") String owner, @RequestParam("repos") String[] repos,
            Label label) {
        Arrays.stream(repos).forEach(repo -> {
            URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                    .path("/repos/{owner}/{repo}/labels")
                    .build(owner, repo);
            restOperations.postForEntity(uri, label, Label.class);
        });
        return "redirect:/orgs/{owner}/labels";
    }

    @PostMapping(path = "/{name}", params = "update")
    public String labelsUpdate(@PathVariable("owner") String owner, @RequestParam("repos") String[] repos,
            @PathVariable("name") String name, Label label) {
        Arrays.stream(repos).forEach(repo -> {
            URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                    .path("/repos/{owner}/{repo}/labels/{name}")
                    .build(owner, repo, name);
            restOperations.patchForObject(uri, label, Label.class);
        });
        return "redirect:/orgs/{owner}/labels";
    }

    @PostMapping(path = "/{name}", params = "delete")
    public String labelsDelete(@PathVariable("owner") String owner, @RequestParam("repos") String[] repos,
            @PathVariable("name") String name) {
        Arrays.stream(repos).forEach(repo -> {
            URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                    .path("/repos/{owner}/{repo}/labels/{name}")
                    .build(owner, repo, name);
            restOperations.delete(uri);
        });
        return "redirect:/orgs/{owner}/labels";
    }

}

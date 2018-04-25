package io.github.yoshikawaa.app;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import io.github.yoshikawaa.app.entity.Label;
import io.github.yoshikawaa.app.entity.Milestone;
import io.github.yoshikawaa.app.entity.Organization;
import io.github.yoshikawaa.app.entity.Repository;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class GithubController {

    @Autowired
    private OAuth2RestTemplate auth2RestTemplate;

    @Value("${app.url.github-api}")
    private String baseUrl;

    @ExceptionHandler(HttpClientErrorException.class)
    public void handleHttpClientErrorException(HttpClientErrorException e) {
        log.warn("Res Code -> {}:{}", e.getStatusCode(), e.getStatusText());
        log.warn("Res Body -> {}", e.getResponseBodyAsString());
        throw e;
    }

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/orgs")
    public String orgs(Model model) {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/user/orgs")
                .queryParam("per_page", 100)
                .build()
                .toUri();
        model.addAttribute("orgs", auth2RestTemplate.getForEntity(uri, Organization[].class).getBody());
        return "orgs";
    }

    @GetMapping("/repos/{owner}")
    public String orgRepos(Model model, @PathVariable("owner") String owner, Authentication authentication) {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .path(owner.equals(authentication.getName()) ? "/users/{org}/repos" : "/orgs/{org}/repos")
                .queryParam("per_page", 100)
                .build(owner);
        model.addAttribute("repos", auth2RestTemplate.getForEntity(uri, Repository[].class).getBody());
        model.addAttribute("owner", owner);
        return "repos";
    }

    @GetMapping("/repos/{owner}/{repo}/labels")
    public String labels(Model model, @PathVariable("owner") String owner, @PathVariable("repo") String repo) {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/repos/{owner}/{repo}/labels")
                .queryParam("per_page", 100)
                .build(owner, repo);
        model.addAttribute("labels", auth2RestTemplate.getForEntity(uri, Label[].class).getBody());
        model.addAttribute("owner", owner);
        model.addAttribute("repo", repo);
        return "labels";
    }

    @PostMapping("/repos/{owner}/{repo}/labels")
    public String labelsCreate(@PathVariable("owner") String owner, @PathVariable("repo") String repo,
            Label label) {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/repos/{owner}/{repo}/labels")
                .build(owner, repo);
        auth2RestTemplate.postForEntity(uri, label, Label.class);
        return "redirect:/repos/{owner}/{repo}/labels";
    }

    @PostMapping(path = "/repos/{owner}/{repo}/labels/{name}", params = "update")
    public String labelsUpdate(@PathVariable("owner") String owner, @PathVariable("repo") String repo,
            @PathVariable("name") String name, Label label) {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/repos/{owner}/{repo}/labels/{name}")
                .build(owner, repo, name);
        auth2RestTemplate.patchForObject(uri, label, Label.class);
        return "redirect:/repos/{owner}/{repo}/labels";
    }

    @PostMapping(path = "/repos/{owner}/{repo}/labels/{name}", params = "delete")
    public String labelsDelete(@PathVariable("owner") String owner, @PathVariable("repo") String repo,
            @PathVariable("name") String name) {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/repos/{owner}/{repo}/labels/{name}")
                .build(owner, repo, name);
        auth2RestTemplate.delete(uri);
        return "redirect:/repos/{owner}/{repo}/labels";
    }

    @GetMapping("/repos/{owner}/{repo}/milestones")
    public String milestones(Model model, @PathVariable("owner") String owner, @PathVariable("repo") String repo) {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/repos/{owner}/{repo}/milestones")
                .queryParam("state", "all")
                .queryParam("direction", "desc")
                .queryParam("per_page", 100)
                .build(owner, repo);
        model.addAttribute("milestones", auth2RestTemplate.getForEntity(uri, Milestone[].class).getBody());
        model.addAttribute("owner", owner);
        model.addAttribute("repo", repo);
        return "milestones";
    }

    @PostMapping("/repos/{owner}/{repo}/milestones")
    public String milestonesCreate(@PathVariable("owner") String owner, @PathVariable("repo") String repo,
            Milestone milestone) {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/repos/{owner}/{repo}/milestones")
                .build(owner, repo);
        auth2RestTemplate.postForEntity(uri, milestone, Milestone.class);
        return "redirect:/repos/{owner}/{repo}/milestones";
    }
    
    @PostMapping(path = "/repos/{owner}/{repo}/milestones/{number}", params = "update")
    public String milestonesUpdate(@PathVariable("owner") String owner, @PathVariable("repo") String repo,
            @PathVariable("number") int number, Milestone milestone) {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/repos/{owner}/{repo}/milestones/{number}")
                .build(owner, repo, number);
        auth2RestTemplate.patchForObject(uri, milestone, Milestone.class);
        return "redirect:/repos/{owner}/{repo}/milestones";
    }

    @PostMapping(path = "/repos/{owner}/{repo}/milestones/{number}", params = "delete")
    public String milestonesDelete(@PathVariable("owner") String owner, @PathVariable("repo") String repo,
            @PathVariable("number") int number) {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/repos/{owner}/{repo}/milestones/{number}")
                .build(owner, repo, number);
        auth2RestTemplate.delete(uri);
        return "redirect:/repos/{owner}/{repo}/milestones";
    }

}

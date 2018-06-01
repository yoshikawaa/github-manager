package io.github.yoshikawaa.app.githubmanager.web;

import java.net.URI;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

import io.github.yoshikawaa.app.githubmanager.entity.Label;

@Controller
@RequestMapping("/repos/{owner}/{repo}/labels")
public class LabelsController extends AbstractRestClientController {

    @GetMapping
    public String labels(Model model, @PathVariable("owner") String owner, @PathVariable("repo") String repo) {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/repos/{owner}/{repo}/labels")
                .queryParam("per_page", perPage)
                .build(owner, repo);
        model.addAttribute("labels", restOperations.getForEntity(uri, Label[].class).getBody());
        model.addAttribute("owner", owner);
        model.addAttribute("repo", repo);
        return "labels";
    }

    @PostMapping
    public String labelsCreate(@PathVariable("owner") String owner, @PathVariable("repo") String repo,
            @Validated Label label) {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/repos/{owner}/{repo}/labels")
                .build(owner, repo);
        restOperations.postForEntity(uri, label, Label.class);
        return "redirect:/repos/{owner}/{repo}/labels";
    }

    @PostMapping(path = "/{name}", params = "update")
    public String labelsUpdate(@PathVariable("owner") String owner, @PathVariable("repo") String repo,
            @PathVariable("name") String name, @Validated Label label) {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/repos/{owner}/{repo}/labels/{name}")
                .build(owner, repo, name);
        restOperations.patchForObject(uri, label, Label.class);
        return "redirect:/repos/{owner}/{repo}/labels";
    }

    @PostMapping(path = "/{name}", params = "delete")
    public String labelsDelete(@PathVariable("owner") String owner, @PathVariable("repo") String repo,
            @PathVariable("name") String name) {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/repos/{owner}/{repo}/labels/{name}")
                .build(owner, repo, name);
        restOperations.delete(uri);
        return "redirect:/repos/{owner}/{repo}/labels";
    }

}

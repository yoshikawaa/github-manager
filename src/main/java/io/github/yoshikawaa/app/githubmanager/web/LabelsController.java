package io.github.yoshikawaa.app.githubmanager.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.github.yoshikawaa.app.githubmanager.api.query.LabelQuery;

@Controller
@RequestMapping("/repos/{owner}/{repo}/labels")
public class LabelsController extends AbstractGithubApiController {

    @GetMapping
    public String labels(Model model, @PathVariable("owner") String owner, @PathVariable("repo") String repo) {
        model.addAttribute("labels", api.labels(owner, repo));
        return "labels";
    }

    @PostMapping
    public String labelsCreate(@PathVariable("owner") String owner, @PathVariable("repo") String repo,
            @Validated LabelQuery label) {
        api.labelsCreate(owner, repo, label);
        return "redirect:/repos/{owner}/{repo}/labels";
    }

    @PostMapping(path = "/{name}", params = "update")
    public String labelsUpdate(@PathVariable("owner") String owner, @PathVariable("repo") String repo,
            @PathVariable("name") String name, @Validated LabelQuery label) {
        api.labelsUpdate(owner, repo, name, label);
        return "redirect:/repos/{owner}/{repo}/labels";
    }

    @PostMapping(path = "/{name}", params = "delete")
    public String labelsDelete(@PathVariable("owner") String owner, @PathVariable("repo") String repo,
            @PathVariable("name") String name) {
        api.labelsDelete(owner, repo, name);
        return "redirect:/repos/{owner}/{repo}/labels";
    }

}

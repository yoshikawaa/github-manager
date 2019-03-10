package io.github.yoshikawaa.app.githubmanager.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.github.yoshikawaa.app.githubmanager.api.entity.Milestone;

@Controller
@RequestMapping("/repos/{owner}/{repo}/milestones")
public class MilestonesController extends AbstractGithubApiController {

    @GetMapping
    public String milestones(Model model, @PathVariable("owner") String owner, @PathVariable("repo") String repo) {
        model.addAttribute("milestones", api.milestones(owner, repo));
        return "milestones";
    }

    @PostMapping
    public String milestonesCreate(@PathVariable("owner") String owner, @PathVariable("repo") String repo,
            @Validated Milestone milestone) {
        api.milestonesCreate(owner, repo, milestone);
        return "redirect:/repos/{owner}/{repo}/milestones";
    }

    @PostMapping(path = "/{number}", params = "update")
    public String milestonesUpdate(@PathVariable("owner") String owner, @PathVariable("repo") String repo,
            @PathVariable("number") int number, @Validated Milestone milestone) {
        api.milestonesUpdate(owner, repo, number, milestone);
        return "redirect:/repos/{owner}/{repo}/milestones";
    }

    @PostMapping(path = "/{number}", params = "delete")
    public String milestonesDelete(@PathVariable("owner") String owner, @PathVariable("repo") String repo,
            @PathVariable("number") int number) {
        api.milestonesDelete(owner, repo, number);
        return "redirect:/repos/{owner}/{repo}/milestones";
    }

}

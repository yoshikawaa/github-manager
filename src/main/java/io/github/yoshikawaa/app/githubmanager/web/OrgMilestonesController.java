package io.github.yoshikawaa.app.githubmanager.web;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import io.github.yoshikawaa.app.githubmanager.api.entity.Milestone;
import io.github.yoshikawaa.app.githubmanager.api.entity.Repository;
import io.github.yoshikawaa.app.githubmanager.api.query.MilestoneQuery;
import io.github.yoshikawaa.app.githubmanager.core.web.UrlUtils;

@Controller
@RequestMapping("/orgs/{owner}/milestones")
@SessionAttributes("milestonesMap")
public class OrgMilestonesController extends AbstractGithubApiController {

    @GetMapping
    public String milestones(Model model, @PathVariable("owner") String owner, Authentication authentication) {
        model.addAttribute("milestonesMap", Arrays.stream(api.repos(owner, authentication))
                .collect(Collectors.toMap(Repository::getName, repo -> api.milestones(owner, repo.getName()))));
        return "orgMilestones";
    }

    @PostMapping
    public String milestonesCreate(@SessionAttribute("milestonesMap") Map<String, Milestone[]> milestonesMap,
            @PathVariable("owner") String owner, @RequestParam("repos") List<String> repos,
            @Validated MilestoneQuery milestone) {
        milestonesMap.entrySet()
                .stream()
                .filter(e -> repos.contains(e.getKey()) && !Arrays.stream(e.getValue())
                        .anyMatch(m -> Objects.equals(m.getTitle(), milestone.getTitle())))
                .map(Entry::getKey)
                .forEach(repo -> api.milestonesCreate(owner, repo, milestone));
        return "redirect:/orgs/{owner}/milestones?complete";
    }

    @PostMapping(path = "/{title}/**", params = "update")
    public String milestonesUpdate(@SessionAttribute("milestonesMap") Map<String, Milestone[]> milestonesMap,
            @PathVariable("owner") String owner, @RequestParam("repos") List<String> repos,
            @PathVariable("title") String title, @Validated MilestoneQuery milestone) {
        // Resolve Title contains "/".
        String realTitle = UrlUtils.pathVariableWithSlash(title);
        milestonesMap.entrySet().stream().filter(e -> repos.contains(e.getKey())).forEach(entry -> {
            Arrays.stream(entry.getValue())
                    .filter(m -> Objects.equals(m.getTitle(), realTitle))
                    .map(Milestone::getNumber)
                    .findFirst()
                    .ifPresent(number -> api.milestonesUpdate(owner, entry.getKey(), number, milestone));
        });
        return "redirect:/orgs/{owner}/milestones?complete";
    }

    @PostMapping(path = "/{title}/**", params = "delete")
    public String milestonesDelete(@SessionAttribute("milestonesMap") Map<String, Milestone[]> milestonesMap,
            @PathVariable("owner") String owner, @RequestParam("repos") List<String> repos,
            @PathVariable("title") String title) {
        // Resolve Title contains "/".
        String realTitle = UrlUtils.pathVariableWithSlash(title);
        milestonesMap.entrySet().stream().filter(e -> repos.contains(e.getKey())).forEach(entry -> {
            Arrays.stream(entry.getValue())
                    .filter(m -> Objects.equals(m.getTitle(), realTitle))
                    .map(Milestone::getNumber)
                    .findFirst()
                    .ifPresent(number -> api.milestonesDelete(owner, entry.getKey(), number));
        });
        return "redirect:/orgs/{owner}/milestones?complete";
    }

    @GetMapping(params = "complete")
    public String sessionComplete(SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        return "redirect:/orgs/{owner}/milestones";
    }

}

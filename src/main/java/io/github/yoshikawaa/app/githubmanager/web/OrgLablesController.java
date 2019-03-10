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

import io.github.yoshikawaa.app.githubmanager.api.entity.Label;
import io.github.yoshikawaa.app.githubmanager.api.entity.Repository;
import io.github.yoshikawaa.app.githubmanager.core.web.UrlUtils;

@Controller
@RequestMapping("/orgs/{owner}/labels")
@SessionAttributes("labelsMap")
public class OrgLablesController extends AbstractGithubApiController {

    @GetMapping
    public String labels(Model model, @PathVariable("owner") String owner, Authentication authentication) {
        model.addAttribute("labelsMap", Arrays.stream(api.repos(owner, authentication))
                .collect(Collectors.toMap(Repository::getName, repo -> api.labels(owner, repo.getName()))));
        return "orgLabels";
    }

    @PostMapping
    public String labelsCreate(@SessionAttribute("labelsMap") Map<String, Label[]> labelsMap,
            @PathVariable("owner") String owner, @RequestParam("repos") List<String> repos, @Validated Label label) {
        labelsMap.entrySet()
                .stream()
                .filter(e -> repos.contains(e.getKey())
                        && !Arrays.stream(e.getValue()).anyMatch(l -> Objects.equals(l.getName(), label.getName())))
                .map(Entry::getKey)
                .forEach(repo -> api.labelsCreate(owner, repo, label));
        return "redirect:/orgs/{owner}/labels?complete";
    }

    @PostMapping(path = "/{name}/**", params = "update")
    public String labelsUpdate(@SessionAttribute("labelsMap") Map<String, Label[]> labelsMap,
            @PathVariable("owner") String owner, @RequestParam("repos") List<String> repos,
            @PathVariable("name") String name, @Validated Label label) {
        // Resolve Name contains "/".
        String realName = UrlUtils.pathVariableWithSlash(name);
        labelsMap.entrySet()
                .stream()
                .filter(e -> repos.contains(e.getKey())
                        && Arrays.stream(e.getValue()).anyMatch(l -> Objects.equals(l.getName(), realName)))
                .map(Entry::getKey)
                .forEach(repo -> api.labelsUpdate(owner, repo, realName, label));
        return "redirect:/orgs/{owner}/labels?complete";
    }

    @PostMapping(path = "/{name}/**", params = "delete")
    public String labelsDelete(@SessionAttribute("labelsMap") Map<String, Label[]> labelsMap,
            @PathVariable("owner") String owner, @RequestParam("repos") List<String> repos,
            @PathVariable("name") String name) {
        // Resolve Name contains "/".
        String realName = UrlUtils.pathVariableWithSlash(name);
        labelsMap.entrySet()
                .stream()
                .filter(e -> repos.contains(e.getKey())
                        && Arrays.stream(e.getValue()).anyMatch(l -> Objects.equals(l.getName(), realName)))
                .map(Entry::getKey)
                .forEach(repo -> api.labelsDelete(owner, repo, realName));
        return "redirect:/orgs/{owner}/labels?complete";
    }

    @GetMapping(params = "complete")
    public String sessionComplete(SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        return "redirect:/orgs/{owner}/labels";
    }

}

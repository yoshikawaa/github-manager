package io.github.yoshikawaa.app.web;

import java.net.URI;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

import io.github.yoshikawaa.app.entity.Repository;

@Controller
@RequestMapping("/repos/{owner}")
public class ReposController extends AbstractRestClientController {

    @GetMapping
    public String repos(Model model, @PathVariable("owner") String owner, Authentication authentication) {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .path(owner.equals(authentication.getName()) ? "/users/{org}/repos" : "/orgs/{org}/repos")
                .queryParam("per_page", perPage)
                .build(owner);
        model.addAttribute("repos", restOperations.getForEntity(uri, Repository[].class).getBody());
        model.addAttribute("owner", owner);
        return "repos";
    }

}

package io.github.yoshikawaa.app.githubmanager.web;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/repos/{owner}")
public class ReposController extends AbstractGithubApiController {

    @GetMapping
    public String repos(Model model, @PathVariable("owner") String owner, Authentication authentication) {
        model.addAttribute("repos", api.repos(owner, authentication));
        return "repos";
    }

}

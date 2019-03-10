package io.github.yoshikawaa.app.githubmanager.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/orgs")
public class OrgsController extends AbstractGithubApiController {

    @GetMapping
    public String orgs(Model model) {
        model.addAttribute("orgs", api.orgs());
        return "orgs";
    }

}

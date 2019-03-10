package io.github.yoshikawaa.app.githubmanager.web;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.github.yoshikawaa.app.githubmanager.api.query.SearchQuery;

@Controller
@RequestMapping("/search/{type:issues|pulls}")
public class SearchController extends AbstractGithubApiController {

    @GetMapping
    public String init(@PathVariable String type, Authentication authentication,
            RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute("q", String.format("is:open is:%s author:%s archived:false ",
                "issues".equals(type) ? "issue" : "pr",
                authentication.getName()));
        redirectAttributes.addAttribute("page", 1);
        redirectAttributes.addAttribute("perPage", api.getPerPage());
        return "redirect:/search/{type}";
    }
    
    @GetMapping(params = "q")
    public String search(Model model, @PathVariable String type, @Validated SearchQuery query) {
        model.addAttribute("page", api.issues(query));
        return "search";
    }
}

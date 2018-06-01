package io.github.yoshikawaa.app.githubmanager.web;

import java.net.URI;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import io.github.yoshikawaa.app.githubmanager.core.pagination.Page;
import io.github.yoshikawaa.app.githubmanager.entity.Issue;
import io.github.yoshikawaa.app.githubmanager.entity.query.SearchQuery;
import io.github.yoshikawaa.app.githubmanager.entity.response.IssuesResponse;

@Controller
@RequestMapping("/search/{type:issues|pulls}")
public class SearchController extends AbstractRestClientController {

    @GetMapping
    public String init(@PathVariable String type, Authentication authentication,
            RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute("q", String.format("is:open is:%s author:%s archived:false ",
                "issues".equals(type) ? "issue" : "pr",
                authentication.getName()));
        redirectAttributes.addAttribute("page", 1);
        redirectAttributes.addAttribute("perPage", perPage);
        return "redirect:/search/{type}";
    }
    
    @GetMapping(params = "q")
    public String search(Model model, @PathVariable String type, @Validated SearchQuery query) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/search/issues")
                .queryParam("q", query.getQ())
                .queryParam("page", query.getPage())
                .queryParam("per_page", query.getPerPage());
        if (StringUtils.hasText(query.getSort())) {
            builder.queryParam("sort", query.getSort());
        }
        if (StringUtils.hasText(query.getOrder())) {
            builder.queryParam("order", query.getOrder());
        }
        URI uri = builder.build().toUri();
        IssuesResponse res = restOperations.getForEntity(uri, IssuesResponse.class).getBody();

        model.addAttribute("page", new Page<Issue>(res.getItems(), query.getPage(), query.getPerPage(), res.getTotalCount()));
        model.addAttribute("type", type);
        return "search";
    }
}

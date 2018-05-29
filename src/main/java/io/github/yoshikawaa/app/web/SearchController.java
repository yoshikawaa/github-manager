package io.github.yoshikawaa.app.web;

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

import io.github.yoshikawaa.app.entity.query.SearchQuery;
import io.github.yoshikawaa.app.entity.response.IssuesResponse;

@Controller
@RequestMapping("/search/{type:issues|pulls}")
public class SearchController extends AbstractRestClientController {

    @GetMapping
    public String init(@PathVariable String type, Authentication authentication, RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute("q", String.format("is:open is:%s author:%s archived:false ",
                "issues".equals(type) ? "issue" : "pr",
                authentication.getName()));
        return "redirect:/search/{type}";
    }
    
    @GetMapping(params = "q")
    public String search(Model model, @Validated SearchQuery query) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/search/issues")
                .queryParam("q", query.getQ())
                .queryParam("per_page", perPage);
        if (StringUtils.hasText(query.getSort())) {
            builder.queryParam("sort", query.getSort());
        }
        if (StringUtils.hasText(query.getOrder())) {
            builder.queryParam("order", query.getOrder());
        }
        URI uri = builder.build().toUri();

        model.addAttribute("issues", restOperations.getForEntity(uri, IssuesResponse.class).getBody().getItems());
        return "search";
    }
}

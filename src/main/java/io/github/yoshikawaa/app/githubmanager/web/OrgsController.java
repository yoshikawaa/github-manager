package io.github.yoshikawaa.app.githubmanager.web;

import java.net.URI;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

import io.github.yoshikawaa.app.githubmanager.entity.Organization;

@Controller
@RequestMapping("/orgs")
public class OrgsController extends AbstractRestClientController {

    @GetMapping
    public String orgs(Model model) {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/user/orgs")
                .queryParam("per_page", perPage)
                .build()
                .toUri();
        model.addAttribute("orgs", restOperations.getForEntity(uri, Organization[].class).getBody());
        return "orgs";
    }

}

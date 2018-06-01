package io.github.yoshikawaa.app.githubmanager.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController extends AbstractRestClientController {

    @GetMapping
    public String home() {
        return "home";
    }

}

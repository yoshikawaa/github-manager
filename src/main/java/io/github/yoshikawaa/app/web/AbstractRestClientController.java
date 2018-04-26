package io.github.yoshikawaa.app.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestOperations;

public class AbstractRestClientController {

    @Autowired
    protected RestOperations restOperations;

    @Value("${app.url.github-api}")
    protected String baseUrl;
}

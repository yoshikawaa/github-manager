package io.github.yoshikawaa.app.githubmanager;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import com.google.common.collect.ImmutableList;

import lombok.Getter;
import lombok.Setter;

@Configuration
public class AdditionalConfig {

    @ApplicationScope
    @Bean("states")
    public List<String> states() {
        return ImmutableList.of("open", "closed");
    }

    @ApplicationScope
    @Bean("sorts")
    public List<String> sorts() {
        return ImmutableList.of("", "comments", "created", "updated");
    }

    @ApplicationScope
    @Bean("orders")
    public List<String> orders() {
        return ImmutableList.of("", "asc", "desc");
    }

    @ApplicationScope
    @Bean("perPages")
    public List<Integer> perPages() {
        return ImmutableList.of(10, 30, 50, 100);
    }

    @ApplicationScope
    @Bean("unavailableRepos")
    public List<String> unavailableRepos(Unavailable unavailable) {
        return Optional.ofNullable(unavailable.getRepositories()).orElse(Collections.emptyList());
    }

    @ApplicationScope
    @Bean("issueManageRepos")
    public Map<String, String> issueManageRepos(IssueManage issueManage) {
        return Optional.ofNullable(issueManage.getRepositories()).orElse(Collections.emptyMap());
    }

    @Component
    @ConfigurationProperties("app.unavailable")
    @Setter
    @Getter
    public static class Unavailable {
        List<String> repositories;
    }

    @Component
    @ConfigurationProperties("app.issuemanage")
    @Setter
    @Getter
    public static class IssueManage {
        Map<String, String> repositories;
    }
}

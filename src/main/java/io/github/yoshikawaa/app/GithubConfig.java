package io.github.yoshikawaa.app;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.ApplicationScope;

import com.google.common.collect.ImmutableList;

@Configuration
public class GithubConfig {
    @ApplicationScope
    @Bean("states")
    public List<String> state() {
        return ImmutableList.of("open", "closed");
    }

}

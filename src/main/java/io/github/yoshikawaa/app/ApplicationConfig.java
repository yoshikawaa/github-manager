package io.github.yoshikawaa.app;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.ApplicationScope;

import com.google.common.collect.ImmutableList;

@Configuration
public class ApplicationConfig {

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
    
}

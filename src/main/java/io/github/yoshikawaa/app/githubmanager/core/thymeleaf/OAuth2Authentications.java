package io.github.yoshikawaa.app.githubmanager.core.thymeleaf;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("deprecation")
@Slf4j
public class OAuth2Authentications {

    public Authentication getUserAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof OAuth2Authentication) {
            return ((OAuth2Authentication) authentication).getUserAuthentication();
        } else {
            log.warn("Authentication is not OAuth2Authentication.");
            return authentication;
        }
    }

}

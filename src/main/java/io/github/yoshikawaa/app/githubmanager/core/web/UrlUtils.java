package io.github.yoshikawaa.app.githubmanager.core.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;

public class UrlUtils {

    public static String pathVariableWithSlash(String pathVariableBase) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        String pathVariableFragment = new AntPathMatcher().extractPathWithinPattern(
                (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE),
                (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE));
        return (StringUtils.hasText(pathVariableFragment)) ? pathVariableBase + "/" + pathVariableFragment
                : pathVariableBase;
    }
}

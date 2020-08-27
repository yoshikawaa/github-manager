package io.github.yoshikawaa.app.githubmanager.analysis;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;

@Component
public class ExtractorService {

    private static final String PREFIX_ISSUE = "#";
    private static final String PREFIX_ISSUE_ALT = "Issues/";
    private static final Pattern NUMBER = Pattern.compile("\\d+");

    public List<Long> numbers(String text) {
        if (text.indexOf(PREFIX_ISSUE) != -1) {
            return Arrays.stream(text.split(PREFIX_ISSUE))
                    .map(t -> obtainNumber(t))
                    .filter(n -> n != null)
                    .collect(Collectors.toList());
        }
        if (text.indexOf(PREFIX_ISSUE_ALT) != -1) {
            return Arrays.stream(text.split(PREFIX_ISSUE_ALT))
                    .map(t -> obtainNumber(t))
                    .filter(n -> n != null)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private Long obtainNumber(String text) {
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        for (int i = 0; i < text.length(); i++) {
            if (!isNumber(text.substring(0, i + 1))) {
                return i != 0 ? Long.parseLong(text.substring(0, i)) : null;
            }
        }
        return Long.parseLong(text);
    }

    private Boolean isNumber(String text) {
        return NUMBER.matcher(text).matches();
    }

}

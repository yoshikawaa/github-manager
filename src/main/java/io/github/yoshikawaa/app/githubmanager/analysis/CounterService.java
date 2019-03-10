package io.github.yoshikawaa.app.githubmanager.analysis;

import java.util.Collection;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableMap;

@Component
public class CounterService {

    public static final String LABEL_CHECKED = "checked";
    public static final String LABEL_TOTAL = "total";
    private static final String KEY_CHECKED = "- [x]";
    private static final String KEY_NON_CHECKED = "- [ ]";

    public Map<String, Integer> count(Collection<String> source) {

        int checked = count(source, KEY_CHECKED);
        int nonChecked = count(source, KEY_NON_CHECKED);

        return ImmutableMap.of(LABEL_CHECKED, checked, LABEL_TOTAL, checked + nonChecked);
    }

    private int count(Collection<String> source, String key) {
        return (int) source.stream().filter(s -> s.contains(key)).count();
    }

}

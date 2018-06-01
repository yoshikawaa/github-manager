package io.github.yoshikawaa.app.githubmanager.analysis;

import java.util.Collection;
import java.util.Map;

public interface Categorizer<T> {
    Map<String, Integer> categorize(Collection<T> source);
}

package io.github.yoshikawaa.app.githubmanager.analysis;

import java.util.Collection;
import java.util.Map;

public interface Counter<T> {
    Map<String, Integer> count(Collection<T> source);
}

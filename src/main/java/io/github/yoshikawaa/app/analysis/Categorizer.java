package io.github.yoshikawaa.app.analysis;

import java.util.Collection;
import java.util.Map;

public interface Categorizer<T> {
    Map<String, Integer> Categorize(Collection<T> source);
}

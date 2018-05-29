package io.github.yoshikawaa.app.analysis;

import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import io.github.yoshikawaa.app.entity.Comment;
import lombok.Setter;

@Component
@ConfigurationProperties("app.analysis.comment")
@Setter
public class CommentBodyCategorizer implements Categorizer<Comment> {

    private Map<String, String> categories;
    private String categoryDefault;

    @Override
    public Map<String, Integer> Categorize(Collection<Comment> comments) {

        Map<String, AtomicInteger> statistics = categories.keySet().stream()
                .collect(toMap(key -> key, key -> new AtomicInteger()));
        statistics.put(categoryDefault, new AtomicInteger());

        comments.forEach(comment -> {
            String category = getCategory(statistics, comment);
            statistics.get(category).getAndIncrement();
            comment.setCategory(category);
        });

        return statistics.entrySet().stream().collect(toMap(e -> e.getKey(), e -> e.getValue().intValue(),
                (e1, e2) -> e1, () -> new HashMap<String, Integer>()));
    }

    private String getCategory(Map<String, AtomicInteger> categories, Comment comment) {
        String body = comment.getBody();
        Optional<String> category = categories.entrySet().stream().map(Entry::getKey).filter(t -> body.contains(t)).findAny();
        if (category.isPresent()) {
            return category.get();
        } else {
            return categoryDefault;
        }
    }
}

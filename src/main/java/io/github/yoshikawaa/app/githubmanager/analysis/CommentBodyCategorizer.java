package io.github.yoshikawaa.app.githubmanager.analysis;

import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import io.github.yoshikawaa.app.githubmanager.entity.Comment;
import lombok.Setter;

@Component
@ConfigurationProperties("app.analysis.comment")
@Setter
public class CommentBodyCategorizer implements Categorizer<Comment> {

    private List<String> categories;
    private String categoryDefault;

    @Override
    public Map<String, Integer> categorize(Collection<Comment> comments) {

        Map<String, AtomicInteger> summary = categories.stream()
                .collect(toMap(entry -> entry, entry -> new AtomicInteger(), (e1, e2) -> e1, ()-> new LinkedHashMap<>()));
        summary.put(categoryDefault, new AtomicInteger());

        comments.forEach(comment -> {
            String category = getCategory(summary, comment);
            summary.get(category).getAndIncrement();
            comment.setCategory(category);
        });

        return summary.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> e.getValue().intValue(), (e1, e2) -> e1, () -> new LinkedHashMap<String, Integer>()));
    }

    private String getCategory(Map<String, AtomicInteger> summary, Comment comment) {
        String body = comment.getBody();
        Optional<String> category = categories.stream().filter(k -> body.contains(k)).findAny();
        return category.isPresent() ? category.get() : categoryDefault;
    }
}

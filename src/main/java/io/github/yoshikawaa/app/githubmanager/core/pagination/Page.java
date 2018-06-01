package io.github.yoshikawaa.app.githubmanager.core.pagination;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.util.Assert;

import lombok.Getter;

@Getter
public class Page<T> {

    private static final int AROUND_RANGE = 2;
    
    private Collection<T> items;
    private int page;
    private int size;
    private int total;
    private int pages;

    private boolean first;
    private boolean last;
    private int previous;
    private int next;
    
    private List<Integer> arounds;

    public Page(Collection<T> items, int page, int size, int total) {
        Assert.isTrue(page > 0, "page number must be greater than or equals 1.");

        this.items = items;
        this.page = page;
        this.size = size;
        this.total = total;
        this.pages = total / size + (total % size == 0 ? 0 : 1);

        this.first = (page == 1);
        this.last = (page == this.pages);
        this.previous = first ? 1 : page - 1;
        this.next = last ? page : page + 1;
        
        int aroundMin = page - AROUND_RANGE > 0 ? page - AROUND_RANGE : 1;
        int aroundMax = page + AROUND_RANGE > pages ? pages : page + AROUND_RANGE;
        this.arounds = IntStream.rangeClosed(aroundMin, aroundMax).boxed().collect(toList());
    }
}

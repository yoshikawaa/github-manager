package io.github.yoshikawaa.app.githubmanager.core.thymeleaf;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;

public class Markdowns {

    private final Parser parser;
    private final HtmlRenderer renderer;

    public Markdowns() {
        this(new MutableDataSet());
    }
    
    public Markdowns(DataHolder options) {
        this.parser = Parser.builder(options).build();
        this.renderer = HtmlRenderer.builder(options).build();
    }

    public String html(String markdown) {
        return renderer.render(parser.parse(markdown));
    }
}

package io.github.yoshikawaa.app.githubmanager.core.thymeleaf;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.processor.AbstractStandardExpressionAttributeTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

import com.vladsch.flexmark.util.options.DataHolder;
import com.vladsch.flexmark.util.options.MutableDataSet;

public class MarkdownTagProcessor extends AbstractStandardExpressionAttributeTagProcessor {

    private static final TemplateMode TEMPLATE_MODE = TemplateMode.HTML;
    private static final String ATTRIBUTE_NAME = "markdown";
    private static final int PRECEDENCE = 1200;

    private final Markdowns renderer;
    
    public MarkdownTagProcessor(String dialectPrefix) {
        this(dialectPrefix, new MutableDataSet());
    }

    public MarkdownTagProcessor(String dialectPrefix, DataHolder options) {
        super(TEMPLATE_MODE, dialectPrefix, ATTRIBUTE_NAME, PRECEDENCE, true);
        this.renderer = new Markdowns(options);
    }

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName,
            String attributeValue, Object expressionResult, IElementTagStructureHandler structureHandler) {

        String markdown = (expressionResult == null? "" : expressionResult.toString());
        String html = renderer.html(markdown);

        structureHandler.setBody(html, false);
        
    }
}

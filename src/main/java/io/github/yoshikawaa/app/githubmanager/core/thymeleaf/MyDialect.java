package io.github.yoshikawaa.app.githubmanager.core.thymeleaf;

import java.util.Set;

import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.standard.processor.StandardXmlNsTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

import com.google.common.collect.ImmutableSet;
import com.vladsch.flexmark.util.options.DataHolder;

public class MyDialect extends AbstractProcessorDialect implements IExpressionObjectDialect {

    private static final String DIALECT_NAME = "My Dialect";
    private static final String DIALECT_PREFIX = "my";
    private static final String EXPRESSION_BYTES = "bytes";
    private static final String EXPRESSION_COLOR = "colors";
    private static final String EXPRESSION_MARKDOWN = "markdowns";
    private static final Set<String> EXPRESSION_NAMES = ImmutableSet.of(EXPRESSION_BYTES, EXPRESSION_COLOR,
            EXPRESSION_MARKDOWN);

    private DataHolder options;

    public MyDialect() {
        super(DIALECT_NAME, DIALECT_PREFIX, StandardDialect.PROCESSOR_PRECEDENCE);
    }

    public MyDialect(DataHolder options) {
        this();
        this.options = options;
    }

    @Override
    public Set<IProcessor> getProcessors(String dialectPrefix) {
        return ImmutableSet.of(
                options == null ? new MarkdownTagProcessor(dialectPrefix)
                        : new MarkdownTagProcessor(dialectPrefix, options),
                new StandardXmlNsTagProcessor(TemplateMode.HTML, dialectPrefix));
    }

    @Override
    public IExpressionObjectFactory getExpressionObjectFactory() {
        return new IExpressionObjectFactory() {
            @Override
            public Set<String> getAllExpressionObjectNames() {
                return EXPRESSION_NAMES;
            }

            @Override
            public Object buildObject(IExpressionContext context, String expressionObjectName) {
                switch (expressionObjectName) {
                case EXPRESSION_BYTES:
                    return new Bytes();
                case EXPRESSION_COLOR:
                    return new Colors();
                default:
                    return options == null ? new Markdowns() : new Markdowns(options);
                }
            }

            @Override
            public boolean isCacheable(String expressionObjectName) {
                return true;
            }
        };
    }
}

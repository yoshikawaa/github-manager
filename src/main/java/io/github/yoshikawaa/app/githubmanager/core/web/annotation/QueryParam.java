package io.github.yoshikawaa.app.githubmanager.core.web.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Target({ FIELD })
@Retention(RUNTIME)
public @interface QueryParam {
    String name() default "";
    boolean requiredValue() default true;
}

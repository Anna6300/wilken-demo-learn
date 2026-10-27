package de.demo.energy.qa;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Links an automated test to a Jira requirement / Xray test key for traceability.
 *
 * <p>In a real project this would be replaced by {@code app.getxray.xray.junit.customjunitxml.annotations.Requirement}
 * from the xray-junit-extensions library, which writes the key into the JUnit XML report that Xray imports.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Requirement {
    String[] value();
}
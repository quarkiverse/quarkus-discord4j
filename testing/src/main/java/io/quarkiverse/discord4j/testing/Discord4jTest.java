package io.quarkiverse.discord4j.testing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.enterprise.inject.Stereotype;

import org.mockito.Answers;

import io.quarkiverse.discord4j.testing.internal.Discord4jTestResource;
import io.quarkus.test.common.QuarkusTestResource;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@QuarkusTestResource(Discord4jTestResource.class)
@Stereotype
public @interface Discord4jTest {

    Answers defaultAnswers() default Answers.RETURNS_DEFAULTS;
}

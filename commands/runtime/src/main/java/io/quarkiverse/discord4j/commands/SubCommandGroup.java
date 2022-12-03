package io.quarkiverse.discord4j.commands;

import java.lang.annotation.*;

/**
 * Declares a subcommand group for subcommand methods in classes annotated with {@code Command}.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SubCommandGroup {
    /**
     * Specifies the name of the subcommand group.
     *
     * @return the subcommand group name
     */
    String value();
}

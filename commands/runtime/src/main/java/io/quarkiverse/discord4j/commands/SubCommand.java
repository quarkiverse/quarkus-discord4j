package io.quarkiverse.discord4j.commands;

import java.lang.annotation.*;

/**
 * Declares a command listener method that listens to subcommands in classes annotated with
 * {@code Command} or {@code SubCommandGroup}.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SubCommand {
    /**
     * Specifies the name of the subcommand.
     *
     * @return the subcommand name
     */
    String value();
}

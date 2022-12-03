package io.quarkiverse.discord4j.commands;

import java.lang.annotation.*;

/**
 * Annotated on a class to indicate a top-level command for nested subcommands, or annotated on a method to
 * declare a command listener method.
 * <p>
 * A command listener method is a non-private, concrete instance method of a bean class returning
 * {@link reactor.core.publisher.Mono}, {@link io.smallrye.mutiny.Uni}, {@link reactor.core.publisher.Flux}, or
 * {@link io.smallrye.mutiny.Multi}.
 * <p>
 * The only parameter of an annotated method must be {@link discord4j.core.event.domain.interaction.ChatInputInteractionEvent},
 * {@link discord4j.core.event.domain.interaction.ChatInputAutoCompleteEvent},
 * {@link discord4j.core.event.domain.interaction.MessageInteractionEvent}, or
 * {@link discord4j.core.event.domain.interaction.UserInteractionEvent}.
 * <p>
 * Any bean may declare multiple command listener methods.
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Command {
    /**
     * Specifies the name of the top-level command.
     *
     * @return the command name
     */
    String value();

    /**
     * Specifies the guild name of the top-level command. The name must have a corresponding {@code GuildCommandsConfig}
     * entry if provided.
     *
     * @return the guild name
     */
    String guild() default "";
}

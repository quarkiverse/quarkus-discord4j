package io.quarkiverse.discord4j.commands.deployment;

import org.jboss.jandex.MethodInfo;

public class CommandDefinition {
    final String name;
    final String guildName;
    final MethodInfo method;
    final String subCommandGroupName;
    final String subCommandName;

    private CommandDefinition(String name, String guildName, MethodInfo method, String subCommandGroupName,
            String subCommandName) {
        this.name = name;
        this.guildName = guildName;
        this.method = method;
        this.subCommandGroupName = subCommandGroupName;
        this.subCommandName = subCommandName;
    }

    public static Builder builder() {
        return new Builder();
    }

    static class Builder {
        private String name;
        private String guildName;
        private MethodInfo method;
        private String subCommandGroupName;
        private String subCommandName;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder guildName(String guildName) {
            this.guildName = guildName.isBlank() ? null : guildName;
            return this;
        }

        public Builder method(MethodInfo method) {
            this.method = method;
            return this;
        }

        public Builder subCommandGroupName(String subCommandGroupName) {
            this.subCommandGroupName = subCommandGroupName;
            return this;
        }

        public Builder subCommandName(String subCommandName) {
            this.subCommandName = subCommandName;
            return this;
        }

        public CommandDefinition build() {
            return new CommandDefinition(name, guildName, method, subCommandGroupName, subCommandName);
        }
    }
}

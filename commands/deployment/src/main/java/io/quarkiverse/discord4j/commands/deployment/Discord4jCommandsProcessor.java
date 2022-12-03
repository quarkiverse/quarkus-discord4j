package io.quarkiverse.discord4j.commands.deployment;

import static io.quarkiverse.discord4j.deployment.Discord4jDotNames.*;
import static io.quarkiverse.discord4j.deployment.Discord4jMethodDescriptors.*;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;

import org.jboss.jandex.*;

import discord4j.core.event.domain.interaction.*;
import io.quarkiverse.discord4j.commands.Command;
import io.quarkiverse.discord4j.commands.SubCommand;
import io.quarkiverse.discord4j.commands.SubCommandGroup;
import io.quarkiverse.discord4j.commands.runtime.Discord4jCommandsFilter;
import io.quarkiverse.discord4j.commands.runtime.Discord4jCommandsRecorder;
import io.quarkiverse.discord4j.commands.runtime.Discord4jCommandsRegistrar;
import io.quarkiverse.discord4j.commands.runtime.config.Discord4jCommandsConfig;
import io.quarkiverse.discord4j.deployment.Discord4jUtils;
import io.quarkiverse.discord4j.deployment.spi.GatewayEventSubscriberFlatMapOperatorBuildItem;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.AutoAddScopeBuildItem;
import io.quarkus.arc.deployment.UnremovableBeanBuildItem;
import io.quarkus.arc.processor.BuiltinScope;
import io.quarkus.deployment.GeneratedClassGizmoAdaptor;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.AdditionalIndexedClassesBuildItem;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.GeneratedClassBuildItem;
import io.quarkus.gizmo.*;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.groups.MultiCreate;
import io.smallrye.mutiny.groups.UniCreate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Discord4jCommandsProcessor {
    private static final DotName COMMAND = DotName.createSimple(Command.class.getName());
    private static final DotName SUB_COMMAND = DotName.createSimple(SubCommand.class.getName());
    private static final DotName SUB_COMMAND_GROUP = DotName.createSimple(SubCommandGroup.class.getName());

    private static final MethodDescriptor DISCORD4J_COMMANDS_FILTER_TEST = MethodDescriptor.ofMethod(
            Discord4jCommandsFilter.class, "test", boolean.class, InteractionCreateEvent.class, String.class,
            Optional.class, Optional.class, Optional.class);

    private static final List<DotName> COMMAND_EVENTS = List.of(
            DotName.createSimple(ChatInputAutoCompleteEvent.class.getName()),
            DotName.createSimple(ChatInputInteractionEvent.class.getName()),
            DotName.createSimple(MessageInteractionEvent.class.getName()),
            DotName.createSimple(UserInteractionEvent.class.getName()));

    @BuildStep
    void beans(BuildProducer<AutoAddScopeBuildItem> autoAddScopes,
            BuildProducer<UnremovableBeanBuildItem> unremovableBeans) {
        autoAddScopes.produce(AutoAddScopeBuildItem.builder()
                .containsAnnotations(COMMAND, SUB_COMMAND)
                .defaultScope(BuiltinScope.APPLICATION)
                .build());

        unremovableBeans.produce(UnremovableBeanBuildItem.beanClassAnnotation(COMMAND));
        unremovableBeans.produce(UnremovableBeanBuildItem.beanClassAnnotation(SUB_COMMAND));
    }

    @BuildStep
    AdditionalIndexedClassesBuildItem index() {
        return new AdditionalIndexedClassesBuildItem(COMMAND.toString());
    }

    @BuildStep
    AdditionalBeanBuildItem additionalBeans() {
        return AdditionalBeanBuildItem.builder()
                .addBeanClasses(Discord4jCommandsRegistrar.class, Discord4jCommandsFilter.class)
                .setUnremovable()
                .build();
    }

    @BuildStep
    @Record(ExecutionTime.STATIC_INIT)
    void registerCommands(Discord4jCommandsRecorder recorder, Discord4jCommandsConfig config) throws IOException {
        recorder.registerCommands(config);
    }

    @BuildStep
    void subscriberOperators(CombinedIndexBuildItem combinedIndex,
            BuildProducer<GeneratedClassBuildItem> generatedClass,
            BuildProducer<GatewayEventSubscriberFlatMapOperatorBuildItem> subscriberOperators) {
        List<CommandDefinition> commands = commands(combinedIndex.getIndex());
        ClassOutput output = new GeneratedClassGizmoAdaptor(generatedClass, true);
        Map<CommandDefinition, String> commandProxies = commandProxies(commands, output);

        for (CommandDefinition command : commands) {
            subscriberOperators.produce(new GatewayEventSubscriberFlatMapOperatorBuildItem(
                    command.method.parameterTypes().get(0).name().toString(),
                    mc -> mc.newInstance(MethodDescriptor.ofConstructor(commandProxies.get(command)))));
        }
    }

    private static List<CommandDefinition> commands(IndexView indexView) {
        List<CommandDefinition> commands = new ArrayList<>();
        for (AnnotationInstance annotation : indexView.getAnnotations(COMMAND)) {
            if (annotation.target().kind() == AnnotationTarget.Kind.METHOD) {
                MethodInfo method = annotation.target().asMethod();
                verifyMethodSignature(method, COMMAND);

                commands.add(CommandDefinition.builder()
                        .name(annotation.value().asString())
                        .guildName(annotation.valueWithDefault(indexView, "guild").asString())
                        .method(method)
                        .build());
            }
        }

        for (AnnotationInstance annotation : indexView.getAnnotations(SUB_COMMAND)) {
            MethodInfo method = annotation.target().asMethod();
            verifyMethodSignature(method, SUB_COMMAND);

            CommandDefinition.Builder builder = CommandDefinition.builder()
                    .method(method)
                    .subCommandName(annotation.value().asString());

            ClassInfo cl = method.declaringClass();
            AnnotationInstance command, subCommandGroup;

            if ((command = cl.classAnnotation(COMMAND)) != null) {
                builder.name(command.value().asString()).guildName(command.valueWithDefault(indexView, "guild").asString());
            } else if ((subCommandGroup = cl.classAnnotation(SUB_COMMAND_GROUP)) != null) {
                cl = indexView.getClassByName(cl.enclosingClass());
                if ((command = cl.classAnnotation(COMMAND)) == null) {
                    throw new IllegalStateException(String.format(
                            "Class annotated with %s must be enclosed in a class annotated with %s",
                            SUB_COMMAND_GROUP, COMMAND));
                }

                builder.name(command.value().asString())
                        .guildName(command.valueWithDefault(indexView, "guild").asString())
                        .subCommandGroupName(subCommandGroup.value().asString());
            } else {
                throw new IllegalStateException(String.format(
                        "Method annotated with %s at %s:%s must be enclosed in a class annotated with %s or %s",
                        SUB_COMMAND, cl.name(), method.name(), COMMAND, SUB_COMMAND_GROUP));
            }

            commands.add(builder.build());
        }

        return commands;
    }

    private static void verifyMethodSignature(MethodInfo method, DotName annotation) {
        if (!Discord4jUtils.isValidReturnType(method.returnType())) {
            throw new IllegalStateException(String.format(
                    "Method annotated with %s at %s:%s must return %s, %s, %s, or %s",
                    annotation, method.declaringClass().name(), method.name(), MONO, UNI, FLUX, MULTI));
        }

        if (method.parameterTypes().size() != 1) {
            throw new IllegalStateException(String.format(
                    "Method annotated with %s at %s:%s must only declare one parameter",
                    annotation, method.declaringClass().name(), method.name()));
        }

        if (!COMMAND_EVENTS.contains(method.parameterTypes().get(0).name())) {
            throw new IllegalStateException(String.format(
                    "The parameter of the method annotated with %s at %s:%s must be one of: %s",
                    annotation, method.declaringClass().name(), method.name(), COMMAND_EVENTS));
        }
    }

    // TODO abstract creating proxies between both modules
    private static Map<CommandDefinition, String> commandProxies(List<CommandDefinition> commands,
            ClassOutput output) {
        Map<CommandDefinition, String> classNames = new HashMap<>();
        for (int i = 0; i < commands.size(); i++) {
            CommandDefinition command = commands.get(i);
            MethodInfo method = command.method;

            String className = method.declaringClass().name().packagePrefix() + ".CommandProxy" + (i + 1);
            classNames.put(command, className);

            ClassCreator cc = ClassCreator.builder()
                    .classOutput(output)
                    .className(className)
                    .interfaces(Function.class)
                    .build();

            MethodCreator mc = cc.getMethodCreator(FUNCTION_APPLY);
            mc.setModifiers(Modifier.PUBLIC);

            ResultHandle filter = Discord4jUtils.getBeanInstance(mc, Discord4jCommandsFilter.class.getName());
            BranchResult branch = mc.ifFalse(mc.invokeVirtualMethod(DISCORD4J_COMMANDS_FILTER_TEST,
                    filter, mc.getMethodParam(0), mc.load(command.name), optional(mc, command.guildName),
                    optional(mc, command.subCommandGroupName), optional(mc, command.subCommandName)));
            BytecodeCreator bc = branch.trueBranch();

            DotName name = method.returnType().name();
            if (name.equals(UNI)) {
                bc.returnValue(bc.invokeVirtualMethod(MethodDescriptor.ofMethod(UniCreate.class, "voidItem", Uni.class),
                        bc.invokeStaticMethod(MethodDescriptor.ofMethod(Uni.class, "createFrom", UniCreate.class))));
            } else if (name.equals(MONO)) {
                bc.returnValue(bc.invokeStaticMethod(MethodDescriptor.ofMethod(Mono.class, "empty", Mono.class)));
            } else if (name.equals(MULTI)) {
                bc.returnValue(bc.invokeVirtualMethod(MethodDescriptor.ofMethod(MultiCreate.class, "empty", Multi.class),
                        bc.invokeStaticMethod(MethodDescriptor.ofMethod(Multi.class, "createFrom", MultiCreate.class))));
            } else {
                bc.returnValue(bc.invokeStaticMethod(MethodDescriptor.ofMethod(Flux.class, "empty", Flux.class)));
            }

            ResultHandle publisher = mc.invokeVirtualMethod(method,
                    Discord4jUtils.getBeanInstance(mc, method.declaringClass().name().toString()),
                    mc.checkCast(mc.getMethodParam(0), method.parameterTypes().get(0).name().toString()));

            mc.returnValue(Discord4jUtils.convertIfUni(method.returnType().name().toString(), mc, publisher));
            cc.close();
        }

        return classNames;
    }

    private static ResultHandle optional(BytecodeCreator bc, String value) {
        return bc.invokeStaticMethod(MethodDescriptor.ofMethod(Optional.class, "ofNullable", Optional.class, Object.class),
                value != null ? bc.load(value) : bc.loadNull());
    }
}

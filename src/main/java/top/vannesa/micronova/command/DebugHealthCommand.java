package top.vannesa.micronova.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import top.vannesa.micronova.health.BodyPart;
import top.vannesa.micronova.health.HealthComponent;

public class DebugHealthCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) ->
                        registerInternal(dispatcher)
        );
    }

    private static void registerInternal(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal("micronova_health")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("amount", FloatArgumentType.floatArg())
                                .executes(context -> {
                                    ServerPlayerEntity player = context.getSource().getPlayer();
                                    float amount = FloatArgumentType.getFloat(context, "amount");

                                    ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
                                    // Apply damage to all body parts
                                    java.util.List<BodyPart> allParts = java.util.Arrays.asList(BodyPart.values());
                                    top.vannesa.micronova.health.PlayerHealthManager.applyDamageMultiple(serverPlayer, allParts, amount);

                                    return 1;
                                })
                        )
        );
    }
}

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

                                    // ⚠️ 这里是 debug 阶段的“假 HealthComponent”
                                    // 后续会被真正的 Player-attached Component 替换
                                    HealthComponent health = new HealthComponent();
                                    health.damage(BodyPart.CHEST, amount);

                                    return 1;
                                })
                        )
        );
    }
}

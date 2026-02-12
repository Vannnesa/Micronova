package top.vannesa.micronova.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import top.vannesa.micronova.health.BodyPart;
import top.vannesa.micronova.health.HealthComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
                        // Syntax: /micronova_health <bodypart> <amount>
                        .then(CommandManager.argument("bodypart", StringArgumentType.word())
                                .then(CommandManager.argument("amount", FloatArgumentType.floatArg())
                                        .executes(context -> {
                                            ServerPlayerEntity player = context.getSource().getPlayer();
                                            String bodyPart = StringArgumentType.getString(context, "bodypart");
                                            float amount = FloatArgumentType.getFloat(context, "amount");

                                            return applyDamage(player, bodyPart, amount);
                                        })
                                )
                        )
        );
    }

    private static int applyDamage(ServerPlayerEntity player, String bodyPartStr, float amount) {
        if (player == null) return 0;

        List<BodyPart> targetParts = new ArrayList<>();

        // Parse body part string
        String lowerPart = bodyPartStr.toLowerCase();
        if (lowerPart.equals("all")) {
            targetParts.addAll(Arrays.asList(BodyPart.values()));
        } else if (lowerPart.equals("chest")) {
            targetParts.add(BodyPart.CHEST);
        } else if (lowerPart.equals("head")) {
            targetParts.add(BodyPart.HEAD);
        } else if (lowerPart.equals("left_arm")) {
            targetParts.add(BodyPart.LEFT_ARM);
        } else if (lowerPart.equals("right_arm")) {
            targetParts.add(BodyPart.RIGHT_ARM);
        } else if (lowerPart.equals("left_leg")) {
            targetParts.add(BodyPart.LEFT_LEG);
        } else if (lowerPart.equals("right_leg")) {
            targetParts.add(BodyPart.RIGHT_LEG);
        } else {
            // Try to parse as BodyPart enum name
            try {
                targetParts.add(BodyPart.valueOf(lowerPart.toUpperCase()));
            } catch (IllegalArgumentException e) {
                player.sendMessage(net.minecraft.text.Text.literal(
                    "Unknown body part: " + bodyPartStr + ". Use: all, chest, head, left_arm, right_arm, left_leg, right_leg"
                ), false);
                return 0;
            }
        }

        top.vannesa.micronova.health.PlayerHealthManager.applyDamageMultiple(player, targetParts, amount);
        return 1;
    }
}

package top.vannesa.micronova.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import top.vannesa.micronova.health.DummyHealthManager;
import top.vannesa.micronova.health.HealthComponent;
import top.vannesa.micronova.health.PlayerHealthManager;

/**
 * Debug command to spawn a dummy that inherits the executing player's attributes.
 * Intended for temporary debugging only — delete this command after testing.
 */
public final class DebugSpawnDummyCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) -> registerInternal(dispatcher)
        );
    }

    private static void registerInternal(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal("micronova_spawn_dummy")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("distance", IntegerArgumentType.integer(1, 16))
                                .executes(context -> {
                                    ServerPlayerEntity player = context.getSource().getPlayer();
                                    int dist = IntegerArgumentType.getInteger(context, "distance");

                                    BlockPos pos = player.getBlockPos().offset(player.getHorizontalFacing(), dist);
                                    ArmorStandEntity stand = new ArmorStandEntity(player.getWorld(), pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
                                    stand.setCustomName(Text.of("Dummy - " + player.getEntityName()));
                                    stand.setCustomNameVisible(true);
                                    stand.setNoGravity(true);
                                    // persistent call not available across all MC versions; skip
                                    stand.setInvisible(false);

                                    // copy armor from player
                                    for (EquipmentSlot slot : EquipmentSlot.values()) {
                                        try {
                                            stand.equipStack(slot, player.getEquippedStack(slot).copy());
                                        } catch (Exception ignored) {}
                                    }

                                    player.getWorld().spawnEntity(stand);

                                    // clone player's health component into dummy's manager (debug-only)
                                    HealthComponent src = PlayerHealthManager.get(player);
                                    DummyHealthManager.createFor(stand, src);

                                    context.getSource().sendFeedback(() -> Text.of("Spawned dummy at " + pos.toShortString()), false);
                                    return 1;
                                })
                        )
        );
    }
}

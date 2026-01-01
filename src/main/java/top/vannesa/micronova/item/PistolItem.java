package top.vannesa.micronova.item;

import io.netty.buffer.Unpooled;
// Client networking is handled in client-only code (PistolClient)
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import top.vannesa.micronova.network.ModPackets;

public class PistolItem extends Item {

    public PistolItem(Settings settings) {
        super(settings);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand);
        return TypedActionResult.consume(user.getStackInHand(hand));
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, net.minecraft.entity.LivingEntity user, int remainingUseTicks) {
        // No-op: firing is done via client keybinding which sends a packet to server.
        // This avoids referencing client-only classes from common code.
    }
}

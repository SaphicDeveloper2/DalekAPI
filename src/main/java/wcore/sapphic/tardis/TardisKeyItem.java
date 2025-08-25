package wcore.sapphic.tardis;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;

public class TardisKeyItem extends Item {

    public TardisKeyItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        // Ensure we are on the server side before doing anything
        if (!context.getLevel().isClientSide) {
            // Check if the block clicked is a TARDIS exterior
            if (context.getLevel().getBlockState(context.getClickedPos()).getBlock() instanceof TardisExteriorBlock) {
                BlockEntity blockEntity = context.getLevel().getBlockEntity(context.getClickedPos());
                if (blockEntity instanceof TardisExteriorBlockEntity tardis) {
                    // Toggle the lock state
                    tardis.setLocked(!tardis.isLocked());

                    // Send a message to the player to inform them of the new state
                    if (tardis.isLocked()) {
                        context.getPlayer().sendSystemMessage(Component.literal("TARDIS locked."));
                    } else {
                        context.getPlayer().sendSystemMessage(Component.literal("TARDIS unlocked."));
                    }
                    return InteractionResult.SUCCESS; // Signal that the action was successful
                }
            }
        }
        return super.useOn(context); // If not a TARDIS, do the default action
    }
}
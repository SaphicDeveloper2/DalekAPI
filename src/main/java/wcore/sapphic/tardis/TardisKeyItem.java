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
        if (context.getLevel().getBlockState(context.getClickedPos()).getBlock() instanceof TardisExteriorBlock) {
            BlockEntity blockEntity = context.getLevel().getBlockEntity(context.getClickedPos());
            if (blockEntity instanceof TardisExteriorBlockEntity tardis) {
                if (!context.getLevel().isClientSide) {
                    tardis.setLocked(!tardis.isLocked());
                } else {
                    if (tardis.isLocked()) {
                        context.getPlayer().sendSystemMessage(Component.literal("TARDIS unlocked."));
                    } else {
                        context.getPlayer().sendSystemMessage(Component.literal("TARDIS locked."));
                    }
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.useOn(context);
    }
}
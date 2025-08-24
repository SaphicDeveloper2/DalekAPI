package wcore.sapphic.items;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.teabs.teabsdoctorwhomod.procedures.TenthScrewdriverRightclickedOnBlockProcedure;

public class BaseSonicItem extends Item {

    public BaseSonicItem(Properties properties) {
        super(properties);
    }

    public BaseSonicItem() {
        // A default constructor with some sensible properties for a sonic device.
        super(new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        // This method calls the onSonicUse method, which contains the behavior.
        // Classes extending this one can override onSonicUse to change the behavior.
        onSonicUse(context.getLevel(), context.getClickedPos(), context.getPlayer());
        return InteractionResult.SUCCESS;
    }

    /**
     * This method is called when the sonic item is used on a block.
     * It has a default implementation that calls the procedure from the original SonicPenItem.
     * <p>
     * You can override this method in your own sonic item class to define custom behavior.
     *
     * @param world  The world the item was used in.
     * @param pos    The position of the block that was clicked.
     * @param player The player who used the item.
     */
    protected void onSonicUse(Level world, BlockPos pos, Player player) {
        // This is the default behavior, calling the procedure from the user's original code.
        TenthScrewdriverRightclickedOnBlockProcedure.execute(world, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), player);
    }
}

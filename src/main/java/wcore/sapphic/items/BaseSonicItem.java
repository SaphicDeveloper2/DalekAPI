package wcore.sapphic.items;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import net.teabs.teabsdoctorwhomod.procedures.TenthScrewdriverRightclickedOnBlockProcedure;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class BaseSonicItem extends Item {

    public BaseSonicItem(Properties properties) {
        super(properties);
    }

    public BaseSonicItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
    }

    // This is now the primary interaction method for all sonics.
    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        // Apply cooldown and play sound here so it happens for all sonics that use this base logic.
        assert context.getPlayer() != null;
        context.getPlayer().getCooldowns().addCooldown(this, 20);
        if (!context.getLevel().isClientSide()) {
            playUseSound(context.getLevel(), context.getPlayer());
        }

        // Call the specific onSonicUse logic.
        onSonicUse(context.getLevel(), context.getClickedPos(), context.getPlayer(), context.getItemInHand());
        return InteractionResult.SUCCESS;
    }

    /**
     * This method is called when the sonic item is used on a block.
     * It contains the default behavior for all sonics.
     * Subclasses can override this to provide unique functionality.
     */
    protected void onSonicUse(Level world, BlockPos pos, Player player, ItemStack stack) {
        TenthScrewdriverRightclickedOnBlockProcedure.execute(world, pos.getX(), pos.getY(), pos.getZ(), player);
    }

    /**
     * Plays the default sound for a sonic screwdriver.
     * This can be overridden by subclasses to provide custom sounds.
     */
    protected void playUseSound(Level world, Player player) {
        world.playSound(null, player.getX(), player.getY(), player.getZ(),
                Objects.requireNonNull(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("dalekapi:sonic_11_use"))),
                SoundSource.PLAYERS, 1.0f, 1.0f);
    }

    @Override
    public boolean isFoil(@NotNull ItemStack p_41453_) {
        return true;
    }

    protected abstract void onSonicUse(Level world, BlockPos pos, Player player);
}


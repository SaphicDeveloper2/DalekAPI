package wcore.sapphic.items;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import net.teabs.teabsdoctorwhomod.procedures.TenthScrewdriverRightclickedOnBlockProcedure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wcore.sapphic.datapack.SonicManager;
import wcore.sapphic.datapack.definition.SonicDefinition;

import java.util.Objects;

/**
 * The abstract base class for all Sonic Screwdriver items in the API.
 * It handles the core interaction logic.
 */
public abstract class BaseSonicItem extends Item {

    public BaseSonicItem(Properties properties) {
        super(properties);
    }

    public BaseSonicItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || context.getLevel().isClientSide) {
            return InteractionResult.PASS;
        }

        ResourceLocation defId = getSonicDefinitionId(context.getItemInHand());
        if (defId == null) {
            return InteractionResult.FAIL;
        }

        SonicDefinition definition = SonicManager.INSTANCE.getDefinitions().get(defId);
        if (definition == null) {
            player.sendSystemMessage(Component.literal("Error: Sonic definition not found for ID: " + defId));
            return InteractionResult.FAIL;
        }

        // The old behavior and event calls have been removed as they are deprecated.
        // We now directly call the core sonic functionality.

        player.getCooldowns().addCooldown(this, 20);
        playUseSound(context.getLevel(), player);
        onSonicUse(context.getLevel(), context.getClickedPos(), player, context.getItemInHand());

        return InteractionResult.SUCCESS;
    }

    /**
     * The default fallback action for a sonic screwdriver. This calls the legacy procedure from the other mod.
     */
    protected void onSonicUse(Level world, BlockPos pos, Player player, ItemStack stack) {
        TenthScrewdriverRightclickedOnBlockProcedure.execute(world, pos.getX(), pos.getY(), pos.getZ(), player);
    }

    protected void playUseSound(Level world, Player player) {
        world.playSound(null, player.blockPosition(),
                Objects.requireNonNull(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("dalekapi:sonic_11_use"))),
                SoundSource.PLAYERS, 1.0f, 1.0f);
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return true;
    }

    @Nullable
    public abstract ResourceLocation getSonicDefinitionId(ItemStack stack);
}

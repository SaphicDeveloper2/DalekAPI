package wcore.sapphic.items;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.registries.ForgeRegistries;
import net.teabs.teabsdoctorwhomod.procedures.TenthScrewdriverRightclickedOnBlockProcedure;
import org.jetbrains.annotations.NotNull;
import wcore.sapphic.datapack.SonicManager;
import wcore.sapphic.datapack.definition.SonicDefinition;
import wcore.sapphic.renderers.PackagedSonicItemRenderer;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * The sole implementation for all Sonic Screwdrivers loaded from the Packs system.
 * It is dynamically registered for each definition found in the packs.
 */
public class PackagedSonicItem extends Item {

    private final ResourceLocation definitionId;

    public PackagedSonicItem(Properties properties, ResourceLocation definitionId) {
        super(properties);
        this.definitionId = definitionId;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || context.getLevel().isClientSide) {
            return InteractionResult.PASS;
        }

        SonicDefinition definition = SonicManager.INSTANCE.getDefinitions().get(this.definitionId);
        if (definition == null) {
            player.sendSystemMessage(Component.literal("Error: Sonic definition not found for ID: " + this.definitionId));
            return InteractionResult.FAIL;
        }

        player.getCooldowns().addCooldown(this, 20);
        playUseSound(context.getLevel(), player);
        onSonicUse(context.getLevel(), context.getClickedPos(), player, context.getItemInHand());

        return InteractionResult.SUCCESS;
    }

    /**
     * The default action for a sonic screwdriver. This calls the legacy procedure from the other mod.
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

    @Override
    public Component getName(ItemStack stack) {
        SonicDefinition def = SonicManager.INSTANCE.getDefinitions().get(this.definitionId);
        if (def != null) {
            return Component.translatable(def.getDisplayName());
        }
        // Fallback to a generated name if the definition is somehow missing post-registration
        return super.getName(stack);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return new PackagedSonicItemRenderer();
            }
        });
    }
}


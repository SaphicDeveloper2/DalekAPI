package wcore.sapphic.items;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ForgeRegistries;
import wcore.sapphic.api.events.SonicUseOnBlockEvent;
import wcore.sapphic.datapack.SonicManager;
import wcore.sapphic.datapack.definition.SonicDefinition;

import javax.annotation.Nullable;

public class DataSonicItem extends BaseSonicItem {
    public static final String SONIC_DEFINITION_KEY = "SonicDefinition";

    public DataSonicItem() {
        super();
    }

    @Nullable
    private SonicDefinition getDefinition(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        if (nbt != null && nbt.contains(SONIC_DEFINITION_KEY)) {
            return SonicManager.SONICS.get(new ResourceLocation(nbt.getString(SONIC_DEFINITION_KEY)));
        }
        return null;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        SonicDefinition definition = getDefinition(context.getItemInHand());

        if (definition != null) {
            boolean isCancelled = MinecraftForge.EVENT_BUS.post(new SonicUseOnBlockEvent(context.getPlayer(), context, definition));
            if (isCancelled) {
                return InteractionResult.sidedSuccess(context.getLevel().isClientSide());
            }
        }

        return super.useOn(context);
    }

    @Override
    protected void playUseSound(Level world, Player player) {
        ItemStack stack = player.getItemInHand(player.getUsedItemHand());
        SonicDefinition definition = getDefinition(stack);

        if (definition != null && definition.getUseSound() != null) {
            SoundEvent customSound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(definition.getUseSound()));
            if (customSound != null) {
                world.playSound(null, player.getX(), player.getY(), player.getZ(), customSound, SoundSource.PLAYERS, 1.0f, 1.0f);
                return;
            }
        }
        super.playUseSound(world, player);
    }

    @Override
    protected void onSonicUse(Level world, BlockPos pos, Player player) {

    }

    @Override
    public boolean isRepairable(ItemStack stack) {
        SonicDefinition definition = getDefinition(stack);
        return definition != null && definition.getRepairMaterial() != null;
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        SonicDefinition definition = getDefinition(toRepair);
        if (definition != null && definition.getRepairMaterial() != null) {
            return Ingredient.of(ForgeRegistries.ITEMS.getValue(definition.getRepairMaterial())).test(repair);
        }
        return super.isValidRepairItem(toRepair, repair);
    }
}
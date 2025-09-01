package wcore.sapphic.items;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import wcore.sapphic.renderers.DataSonicItemRenderer;

import java.util.function.Consumer;

public class DataSonicItem extends BaseSonicItem {

    public DataSonicItem() {
        super();
    }

    @Override
    public Component getName(ItemStack pStack) {
        if (pStack.hasTag() && pStack.getTag().contains("sonic_id")) {
            ResourceLocation id = new ResourceLocation(pStack.getTag().getString("sonic_id"));
            return Component.translatable("item." + id.getNamespace() + "." + id.getPath());
        }
        return super.getName(pStack);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return new DataSonicItemRenderer();
            }
        });
    }
}
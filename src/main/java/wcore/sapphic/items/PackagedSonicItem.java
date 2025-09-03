package wcore.sapphic.items;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;
import wcore.sapphic.datapack.SonicManager;
import wcore.sapphic.datapack.definition.SonicDefinition;
import wcore.sapphic.renderers.PackagedSonicItemRenderer;

import java.util.function.Consumer;

/**
 * An item representing a single, unique sonic screwdriver defined in a data pack.
 * Its identity is its registry name, not NBT data.
 */
public class PackagedSonicItem extends BaseSonicItem {

    private final ResourceLocation definitionId;

    public PackagedSonicItem(Properties properties, ResourceLocation definitionId) {
        super(properties);
        this.definitionId = definitionId;
    }

    @Override
    public Component getName(ItemStack stack) {
        // Look up the display name from the definition tied to this specific item
        SonicDefinition def = SonicManager.INSTANCE.getDefinitions().get(this.definitionId);
        if (def != null && def.getDisplayName() != null) {
            return Component.translatable(def.getDisplayName());
        }
        // Fallback to the item's own name if something goes wrong
        return super.getName(stack);
    }

    /**
     * This implementation provides the definition ID that this item was created with.
     * This is how the base class knows which sonic this is.
     */
    @Override
    @Nullable
    public ResourceLocation getSonicDefinitionId(ItemStack stack) {
        return this.definitionId;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                // Use the new renderer designed for these packaged items
                return new PackagedSonicItemRenderer();
            }
        });
    }
}

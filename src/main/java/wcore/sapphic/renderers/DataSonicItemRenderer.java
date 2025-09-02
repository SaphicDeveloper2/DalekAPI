package wcore.sapphic.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import wcore.sapphic.datapack.SonicManager;
import wcore.sapphic.datapack.definition.SonicDefinition;
import wcore.sapphic.items.DataSonicItem;

public class DataSonicItemRenderer extends BlockEntityWithoutLevelRenderer {

    public DataSonicItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack pStack, ItemDisplayContext pDisplayContext, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        BakedModel modelToRender = null;

        // Use the static key from DataSonicItem for consistency.
        if (pStack.hasTag() && pStack.getTag().contains(DataSonicItem.SONIC_DEFINITION_KEY)) {
            ResourceLocation id = new ResourceLocation(pStack.getTag().getString(DataSonicItem.SONIC_DEFINITION_KEY));
            // Access the static map directly from the manager.
            SonicDefinition def = SonicManager.SONICS.get(id);

            // Check the definition and its model location.
            if (def != null && def.getModelLocation() != null) {
                try {
                    // The model location in the JSON is the model's ID. We create a ModelResourceLocation to get it.
                    ModelResourceLocation mrl = new ModelResourceLocation(new ResourceLocation(def.getModelLocation()), "inventory");
                    modelToRender = Minecraft.getInstance().getModelManager().getModel(mrl);
                } catch (Exception e) {
                    // If the model fails to load, we'll fall back to the default model.
                }
            }
        }

        // If a custom model wasn't found or loaded, get the default model for the item.
        if (modelToRender == null) {
            modelToRender = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getItemModel(pStack);
        }

        // Render the final determined model.
        Minecraft.getInstance().getItemRenderer().render(pStack, pDisplayContext, false, pPoseStack, pBuffer, pPackedLight, pPackedOverlay, modelToRender);
    }
}

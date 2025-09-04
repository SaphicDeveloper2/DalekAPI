package wcore.sapphic.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import wcore.sapphic.datapack.SonicManager;
import wcore.sapphic.datapack.definition.SonicDefinition;

public class PackagedSonicItemRenderer extends BlockEntityWithoutLevelRenderer {

    public PackagedSonicItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ModelManager modelManager = itemRenderer.getItemModelShaper().getModelManager();
        BakedModel model = modelManager.getMissingModel(); // Default to missing model

        ResourceLocation defId = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (defId == null) {
            // This should not happen for a registered item, but good to have a guard.
            renderModel(itemRenderer, modelManager.getMissingModel(), stack, displayContext, poseStack, buffer, packedLight, packedOverlay);
            return;
        }

        SonicDefinition def = SonicManager.INSTANCE.getDefinitions().get(defId);

        if (def != null && def.getModel() != null) {
            // Get the model from the vanilla model manager.
            // If the definition's model JSON uses a custom loader (like forge:obj),
            // the model manager will handle it automatically.
            model = modelManager.getModel(def.getModel());
        }

        // If the custom model failed to load or was not defined, fallback to the default 2D item model.
        if (model == modelManager.getMissingModel()) {
            model = itemRenderer.getModel(stack, null, null, 0);
        }

        renderModel(itemRenderer, model, stack, displayContext, poseStack, buffer, packedLight, packedOverlay);
    }

    private void renderModel(ItemRenderer itemRenderer, BakedModel model, ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        // Models are rendered from the center, so translate to the corner first.
        poseStack.translate(0.5D, 0.5D, 0.5D);
        itemRenderer.render(stack, displayContext, false, poseStack, buffer, packedLight, packedOverlay, model);
        poseStack.popPose();
    }
}
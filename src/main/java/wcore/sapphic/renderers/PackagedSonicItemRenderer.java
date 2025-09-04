package wcore.sapphic.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import wcore.sapphic.datapack.SonicManager;
import wcore.sapphic.datapack.definition.SonicDefinition;
import wcore.sapphic.packs.PackAssetManager;

public class PackagedSonicItemRenderer extends BlockEntityWithoutLevelRenderer {

    public PackagedSonicItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        BakedModel model;

        // Get the definition ID directly from the item's registry name.
        // This is the correct approach now that each sonic is a unique item.
        ResourceLocation defId = ForgeRegistries.ITEMS.getKey(stack.getItem());

        SonicDefinition def = (defId != null) ? SonicManager.INSTANCE.getDefinitions().get(defId) : null;

        if (def != null && def.getModel() != null) {
            // Ask the PackAssetManager for the pre-baked model.
            BakedModel packModel = PackAssetManager.INSTANCE.getBakedModel(def.getModel());
            model = (packModel != null) ? packModel : itemRenderer.getModel(stack, null, null, 0);
        } else {
            // Fallback to the default item model if no custom one is found
            model = itemRenderer.getModel(stack, null, null, 0);
        }

        renderModel(itemRenderer, model, stack, displayContext, poseStack, buffer, packedLight, packedOverlay);
    }

    private void renderModel(ItemRenderer itemRenderer, BakedModel model, ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        itemRenderer.render(stack, displayContext, false, poseStack, buffer, packedLight, packedOverlay, model);
        poseStack.popPose();
    }
}


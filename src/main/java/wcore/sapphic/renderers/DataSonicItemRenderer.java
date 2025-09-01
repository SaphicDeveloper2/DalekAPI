package wcore.sapphic.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import wcore.sapphic.datapack.SonicManager;
import wcore.sapphic.datapack.definition.SonicDefinition;

public class DataSonicItemRenderer extends BlockEntityWithoutLevelRenderer {

    public DataSonicItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack pStack, ItemDisplayContext pDisplayContext, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        if (pStack.hasTag() && pStack.getTag().contains("sonic_id")) {
            ResourceLocation id = new ResourceLocation(pStack.getTag().getString("sonic_id"));
            SonicDefinition def = SonicManager.INSTANCE.getSonic(id);

            if (def != null && def.itemModel != null) {
                BakedModel model = Minecraft.getInstance().getModelManager().getModel(def.itemModel);
                Minecraft.getInstance().getItemRenderer().render(pStack, pDisplayContext, false, pPoseStack, pBuffer, pPackedLight, pPackedOverlay, model);
            }
        }
    }
}
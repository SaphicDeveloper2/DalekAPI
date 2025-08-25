package wcore.sapphic.tardis;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.teabs.teabsdoctorwhomod.client.model.Modeltardis; // Import from the other mod


public class TardisExteriorRenderer implements BlockEntityRenderer<TardisExteriorBlockEntity> {

    // IMPORTANT: You need the texture from the other mod.
    // The path is usually 'MODID:textures/entity/MODELNAME.png'
    private static final ResourceLocation TEXTURE = new ResourceLocation("teabs_doctor_who_mod", "textures/entity/tardis.png");
    private final TardisWrapperModel model;

    public TardisExteriorRenderer(BlockEntityRendererProvider.Context context) {
        // Bake the layer from the OTHER mod's ModelLayerLocation
        this.model = new TardisWrapperModel(context.bakeLayer(Modeltardis.LAYER_LOCATION));
    }

    @Override
    public void render(TardisExteriorBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));

        // Render the model
        model.renderToBuffer(poseStack, bufferSource.getBuffer(RenderType.entityCutout(TEXTURE)), packedLight, packedOverlay, 1.0f, 1.0f, 1.0f, 1.0f);

        poseStack.popPose();
    }
}

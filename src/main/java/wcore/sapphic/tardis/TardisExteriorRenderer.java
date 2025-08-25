package wcore.sapphic.tardis;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.teabs.teabsdoctorwhomod.client.model.Modeltardis; // This import is still needed for the model layer

/**
 * This class is responsible for rendering the TARDIS exterior model.
 * It handles loading the model and applying the correct texture to it.
 */
public class TardisExteriorRenderer implements BlockEntityRenderer<TardisExteriorBlockEntity> {

    // The texture is now located within this mod's assets.
    // The path is 'MODID:textures/entity/MODELNAME.png'
    private static final ResourceLocation TEXTURE = new ResourceLocation("dalekapi", "textures/entity/tardis.png");
    private final TardisWrapperModel model;

    /**
     * Constructor for the renderer.
     * It bakes the model layer provided by the other mod.
     * @param context The context provided by the BlockEntityRendererProvider.
     */
    public TardisExteriorRenderer(BlockEntityRendererProvider.Context context) {
        // We still bake the layer from the OTHER mod's ModelLayerLocation because we are using its model shape.
        this.model = new TardisWrapperModel(context.bakeLayer(Modeltardis.LAYER_LOCATION));
    }

    /**
     * This method is called every frame to render the block entity.
     * @param blockEntity The block entity instance being rendered.
     * @param partialTick The fraction of a tick that has passed since the last full tick.
     * @param poseStack The PoseStack for transformations.
     * @param bufferSource The buffer source to get the right render buffer.
     * @param packedLight The packed light value at the block's position.
     * @param packedOverlay The packed overlay value.
     */
    @Override
    public void render(TardisExteriorBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        // Push a new matrix onto the stack to isolate transformations
        poseStack.pushPose();

        // Center the model within the block space and correct its orientation.
        // Models are often 16x16x16, so we translate by 0.5 on x and z.
        // The y-translation of 1.5 is because models are often rendered from the "feet" up, so this moves it down to sit on the block.
        poseStack.translate(0.5, 1.5, 0.5);
        // Rotate the model 180 degrees on the Z-axis to correct its default orientation.
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));

        // Render the model to the buffer with our own texture.
        // RenderType.entityCutout allows for transparent parts of the texture.
        model.renderToBuffer(
                poseStack,
                bufferSource.getBuffer(RenderType.entityCutout(TEXTURE)), // Use our texture
                packedLight,
                packedOverlay,
                1.0f, 1.0f, 1.0f, 1.0f // RGBA color tint (white, no tint)
        );

        // Pop the matrix from the stack to revert our transformations
        poseStack.popPose();
    }
}

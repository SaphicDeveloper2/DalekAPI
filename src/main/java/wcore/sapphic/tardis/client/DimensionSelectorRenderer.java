package wcore.sapphic.tardis.client; // Corrected package

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.state.BlockState;
import wcore.sapphic.tardis.flight.DimensionSelector.DimensionSelectorBlock;
import wcore.sapphic.tardis.flight.DimensionSelector.DimensionSelectorBlockEntity;
import wcore.sapphic.tardis.flight.DimensionSelectorFactory;
// No longer importing from the 'flight' package
import wcore.sapphic.tardis.client.DimensionSelectorRenderer;

public class DimensionSelectorRenderer implements BlockEntityRenderer<DimensionSelectorBlockEntity> {

    private final DimensionSelectorModel model;

    public DimensionSelectorRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new DimensionSelectorModel();
    }

    @Override
    public void render(DimensionSelectorBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();

        BlockState currentState = blockEntity.getBlockState();

        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(-currentState.getValue(DimensionSelectorBlock.FACING).toYRot()));

        if (!currentState.getValue(DimensionSelectorBlock.UP)) {
            poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        }

        poseStack.translate(-0.5, -0.5, -0.5);

        int dimensionIndex = currentState.getValue(DimensionSelectorBlock.DIMENSION_INDEX);
        DimensionSelectorFactory.DimensionEntry dimensionEntry = DimensionSelectorFactory.ALL_DIMENSIONS.get(dimensionIndex);
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(dimensionEntry.textureLocation));

        model.render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();
    }
}
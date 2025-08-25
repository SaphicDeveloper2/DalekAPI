package wcore.sapphic.tardis;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.teabs.teabsdoctorwhomod.client.model.Modeltardis; // Import from the other mod

public class TardisWrapperModel extends Model {

    private final Modeltardis<net.minecraft.world.entity.Entity> tardisModel;

    public TardisWrapperModel(ModelPart root) {
        super(RenderType::entityCutoutNoCull);
        // Create an instance of the model from the other mod
        this.tardisModel = new Modeltardis<>(root);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        // Delegate the rendering call to the original model
        tardisModel.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}

package wcore.sapphic.tardis.client; // Corrected package

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class DimensionSelectorModel extends Model {

    public DimensionSelectorModel() {
        super(RenderType::entityCutoutNoCull);
    }

    public void render(PoseStack poseStack, VertexConsumer consumer, int light, int overlay,
                       float red, float green, float blue, float alpha) {
        PoseStack.Pose pose = poseStack.last();
        drawPlanet(pose, consumer, light, overlay, red, green, blue, alpha);
        drawBase(pose, consumer, light, overlay, red, green, blue, alpha);
    }

    // All the draw methods and vertex data remain the same as the last version, which is correct.
    // ... (drawPlanet and drawBase methods are here)
    private void drawPlanet(PoseStack.Pose pose, VertexConsumer consumer, int light, int overlay, float r, float g, float b, float a) {
        addQuad(pose, consumer, 12, 6, 4, 4, 6, 4, 4, 14, 4, 12, 14, 4, 4, 0, 6, 2, new Vector3f(0, 0, -1), light, overlay, r, g, b, a);
        addQuad(pose, consumer, 12, 6, 12, 12, 6, 4, 12, 14, 4, 12, 14, 12, 4, 2, 6, 4, new Vector3f(1, 0, 0), light, overlay, r, g, b, a);
        addQuad(pose, consumer, 4, 6, 12, 12, 6, 12, 12, 14, 12, 4, 14, 12, 4, 4, 6, 6, new Vector3f(0, 0, 1), light, overlay, r, g, b, a);
        addQuad(pose, consumer, 4, 6, 4, 4, 6, 12, 4, 14, 12, 4, 14, 4, 6, 0, 8, 2, new Vector3f(-1, 0, 0), light, overlay, r, g, b, a);
        addQuad(pose, consumer, 12, 14, 4, 4, 14, 4, 4, 14, 12, 12, 14, 12, 6, 2, 8, 4, new Vector3f(0, 1, 0), light, overlay, r, g, b, a);
        addQuad(pose, consumer, 4, 6, 4, 12, 6, 4, 12, 6, 12, 4, 6, 12, 4, 8, 6, 6, new Vector3f(0, -1, 0), light, overlay, r, g, b, a);
    }

    private void drawBase(PoseStack.Pose pose, VertexConsumer consumer, int light, int overlay, float r, float g, float b, float a) {
        addQuad(pose, consumer, 15, 0, 1, 10, 0, 1, 10, 2, 1, 15, 2, 1, 7.25f, 6.25f, 8.5f, 6.75f, new Vector3f(0, 0, -1), light, overlay, r, g, b, a);
        addQuad(pose, consumer, 15, 0, 6, 15, 0, 1, 15, 2, 1, 15, 2, 6, 7.25f, 6.75f, 8.5f, 7.25f, new Vector3f(1, 0, 0), light, overlay, r, g, b, a);
        addQuad(pose, consumer, 10, 0, 6, 15, 0, 6, 15, 2, 6, 10, 2, 6, 7.25f, 7.25f, 8.5f, 7.75f, new Vector3f(0, 0, 1), light, overlay, r, g, b, a);
        addQuad(pose, consumer, 10, 0, 1, 10, 0, 6, 10, 2, 6, 10, 2, 1, 6, 7.75f, 7.25f, 8.25f, new Vector3f(-1, 0, 0), light, overlay, r, g, b, a);
        addQuad(pose, consumer, 15, 2, 1, 10, 2, 1, 10, 2, 6, 15, 2, 6, 6, 4, 7.25f, 5.25f, new Vector3f(0, 1, 0), light, overlay, r, g, b, a);
        addQuad(pose, consumer, 10, 0, 1, 15, 0, 1, 15, 0, 6, 10, 0, 6, 6, 6.5f, 7.25f, 5.25f, new Vector3f(0, -1, 0), light, overlay, r, g, b, a);
        addQuad(pose, consumer, 6, 0, 1, 1, 0, 1, 1, 2, 1, 6, 2, 1, 7.25f, 7.75f, 8.5f, 8.25f, new Vector3f(0, 0, -1), light, overlay, r, g, b, a);
        addQuad(pose, consumer, 6, 0, 6, 6, 0, 1, 6, 2, 1, 6, 2, 6, 0, 8, 1.25f, 8.5f, new Vector3f(1, 0, 0), light, overlay, r, g, b, a);
        addQuad(pose, consumer, 1, 0, 6, 6, 0, 6, 6, 2, 6, 1, 2, 6, 8, 0, 9.25f, 0.5f, new Vector3f(0, 0, 1), light, overlay, r, g, b, a);
        addQuad(pose, consumer, 1, 0, 1, 1, 0, 6, 1, 2, 6, 1, 2, 1, 8, 0.5f, 9.25f, 1, new Vector3f(-1, 0, 0), light, overlay, r, g, b, a);
        addQuad(pose, consumer, 6, 2, 1, 1, 2, 1, 1, 2, 6, 6, 2, 6, 6, 6.5f, 7.25f, 7.75f, new Vector3f(0, 1, 0), light, overlay, r, g, b, a);
        addQuad(pose, consumer, 1, 0, 1, 6, 0, 1, 6, 0, 6, 1, 0, 6, 7.25f, 5.25f, 8.5f, 4, new Vector3f(0, -1, 0), light, overlay, r, g, b, a);
        addQuad(pose, consumer, 10.5f, 0, 5.5f, 5.5f, 0, 5.5f, 5.5f, 3, 5.5f, 10.5f, 3, 5.5f, 7.25f, 7.75f, 8.5f, 8.25f, new Vector3f(0, 0, -1), light, overlay, r, g, b, a);
        addQuad(pose, consumer, 10.5f, 0, 10.5f, 10.5f, 0, 5.5f, 10.5f, 3, 5.5f, 10.5f, 3, 10.5f, 0, 8, 1.25f, 8.5f, new Vector3f(1, 0, 0), light, overlay, r, g, b, a);
        addQuad(pose, consumer, 5.5f, 0, 10.5f, 10.5f, 0, 10.5f, 10.5f, 3, 10.5f, 5.5f, 3, 10.5f, 8, 0, 9.25f, 0.5f, new Vector3f(0, 0, 1), light, overlay, r, g, b, a);
        addQuad(pose, consumer, 5.5f, 0, 5.5f, 5.5f, 0, 10.5f, 5.5f, 3, 10.5f, 5.5f, 3, 5.5f, 8, 0.5f, 9.25f, 1, new Vector3f(-1, 0, 0), light, overlay, r, g, b, a);
        addQuad(pose, consumer, 10.5f, 3, 5.5f, 5.5f, 3, 5.5f, 5.5f, 3, 10.5f, 10.5f, 3, 10.5f, 6, 6.5f, 7.25f, 7.75f, new Vector3f(0, 1, 0), light, overlay, r, g, b, a);
        addQuad(pose, consumer, 5.5f, 0, 5.5f, 10.5f, 0, 5.5f, 10.5f, 0, 10.5f, 5.5f, 0, 10.5f, 7.25f, 5.25f, 8.5f, 4, new Vector3f(0, -1, 0), light, overlay, r, g, b, a);
        addQuad(pose, consumer, 5, 1, 2, 2, 1, 2, 2, 3, 2, 5, 3, 2, 8, 2.5f, 8.75f, 3, new Vector3f(0, 0, -1), light, overlay, r, g, b, a);
        addQuad(pose, consumer, 5, 1, 5, 5, 1, 2, 5, 3, 2, 5, 3, 5, 2.75f, 8, 3.5f, 8.5f, new Vector3f(1, 0, 0), light, overlay, r, g, b, a);
        addQuad(pose, consumer, 2, 1, 5, 5, 1, 5, 5, 3, 5, 2, 3, 5, 8, 3, 8.75f, 3.5f, new Vector3f(0, 0, 1), light, overlay, r, g, b, a);
        addQuad(pose, consumer, 2, 1, 2, 2, 1, 5, 2, 3, 5, 2, 3, 2, 3.5f, 8, 4.25f, 8.5f, new Vector3f(-1, 0, 0), light, overlay, r, g, b, a);
        addQuad(pose, consumer, 5, 3, 2, 2, 3, 2, 2, 3, 5, 5, 3, 5, 8, 1, 8.75f, 1.75f, new Vector3f(0, 1, 0), light, overlay, r, g, b, a);
        addQuad(pose, consumer, 2, 1, 2, 5, 1, 2, 5, 1, 5, 2, 1, 5, 1.25f, 8.75f, 2, 8, new Vector3f(0, -1, 0), light, overlay, r, g, b, a);
        addQuad(pose, consumer, 14, 1, 2, 11, 1, 2, 11, 3, 2, 14, 3, 2, 8, 3.5f, 8.75f, 4, new Vector3f(0, 0, -1), light, overlay, r, g, b, a);
        addQuad(pose, consumer, 14, 1, 5, 14, 1, 2, 14, 3, 2, 14, 3, 5, 4.25f, 8, 5, 8.5f, new Vector3f(1, 0, 0), light, overlay, r, g, b, a);
        addQuad(pose, consumer, 11, 1, 5, 14, 1, 5, 14, 3, 5, 11, 3, 5, 5, 8, 5.75f, 8.5f, new Vector3f(0, 0, 1), light, overlay, r, g, b, a);
        addQuad(pose, consumer, 11, 1, 2, 11, 1, 5, 11, 3, 5, 11, 3, 2, 5.75f, 8.25f, 6.5f, 8.75f, new Vector3f(-1, 0, 0), light, overlay, r, g, b, a);
        addQuad(pose, consumer, 14, 3, 2, 11, 3, 2, 11, 3, 5, 14, 3, 5, 8, 1.75f, 8.75f, 2.5f, new Vector3f(0, 1, 0), light, overlay, r, g, b, a);
        addQuad(pose, consumer, 11, 1, 2, 14, 1, 2, 14, 1, 5, 11, 1, 5, 2, 8.75f, 2.75f, 8, new Vector3f(0, -1, 0), light, overlay, r, g, b, a);
        addQuad(pose, consumer, 16, 0, 0, 0, 0, 0, 0, 1, 0, 16, 1, 0, 7.25f, 5.25f, 11.25f, 5.5f, new Vector3f(0, 0, -1), light, overlay, r, g, b, a);
        addQuad(pose, consumer, 16, 0, 16, 16, 0, 0, 16, 1, 0, 16, 1, 16, 7.25f, 5.5f, 11.25f, 5.75f, new Vector3f(1, 0, 0), light, overlay, r, g, b, a);
        addQuad(pose, consumer, 0, 0, 16, 16, 0, 16, 16, 1, 16, 0, 1, 16, 7.25f, 5.75f, 11.25f, 6, new Vector3f(0, 0, 1), light, overlay, r, g, b, a);
        addQuad(pose, consumer, 0, 0, 0, 0, 0, 16, 0, 1, 16, 0, 1, 0, 7.25f, 6, 11.25f, 6.25f, new Vector3f(-1, 0, 0), light, overlay, r, g, b, a);
        addQuad(pose, consumer, 16, 1, 0, 0, 1, 0, 0, 1, 16, 16, 1, 16, 0, 0, 4, 4, new Vector3f(0, 1, 0), light, overlay, r, g, b, a);
        addQuad(pose, consumer, 0, 0, 0, 16, 0, 0, 16, 0, 16, 0, 0, 16, 0, 8, 4, 4, new Vector3f(0, -1, 0), light, overlay, r, g, b, a);
    }

    private void addQuad(PoseStack.Pose pose, VertexConsumer consumer,
                         float x1, float y1, float z1, float x2, float y2, float z2,
                         float x3, float y3, float z3, float x4, float y4, float z4,
                         float u1, float v1, float u2, float v2,
                         Vector3f normal, int light, int overlay,
                         float r, float g, float b, float a) {

        Matrix4f posMat = pose.pose();
        Matrix3f normMat = pose.normal();
        float textureSize = 64.0f;

        addVertex(posMat, normMat, consumer, x1, y1, z1, r, g, b, a, u1 / textureSize, v2 / textureSize, normal, light, overlay);
        addVertex(posMat, normMat, consumer, x2, y2, z2, r, g, b, a, u2 / textureSize, v2 / textureSize, normal, light, overlay);
        addVertex(posMat, normMat, consumer, x3, y3, z3, r, g, b, a, u2 / textureSize, v1 / textureSize, normal, light, overlay);
        addVertex(posMat, normMat, consumer, x4, y4, z4, r, g, b, a, u1 / textureSize, v1 / textureSize, normal, light, overlay);
    }

    private void addVertex(Matrix4f posMat, Matrix3f normMat, VertexConsumer consumer,
                           float x, float y, float z, float r, float g, float b, float a,
                           float u, float v, Vector3f normal, int light, int overlay) {

        consumer.vertex(posMat, x / 16f, (16f - y) / 16f, z / 16f)
                .color(r, g, b, a)
                .uv(u, v)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(normMat, normal.x(), normal.y(), normal.z())
                .endVertex();
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer consumer, int light, int overlay, float red, float green, float blue, float alpha) {
        render(poseStack, consumer, light, overlay, red, green, blue, alpha);
    }
}

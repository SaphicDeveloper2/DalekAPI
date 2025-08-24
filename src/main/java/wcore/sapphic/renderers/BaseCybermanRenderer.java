// Place this in your renderer package, e.g., com.yourmod.client.renderer.entity

package wcore.sapphic.renderers;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.teabs.teabsdoctorwhomod.client.model.ModelCybusCyberman;

/**
 * A generic renderer for any Cyberman -like entity that uses the Modelcybuscyberman model.
 * It is configured with a specific texture upon creation.
 */
public class BaseCybermanRenderer<T extends Mob> extends MobRenderer<T, ModelCybusCyberman<T>> {

    private final ResourceLocation texture;

    public BaseCybermanRenderer(EntityRendererProvider.Context context, ResourceLocation texture) {
        super(context, new ModelCybusCyberman<>(context.bakeLayer(ModelCybusCyberman.LAYER_LOCATION)), 0.5F);
        this.texture = texture; // Store the texture provided for this specific Cyberman type
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return this.texture; // Always return the texture this renderer was created with
    }
}

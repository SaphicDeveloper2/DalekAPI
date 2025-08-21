// Place this in your renderer package, e.g., com.yourmod.client.renderer.entity

package wcore.sapphic.renderers;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.teabs.teabsdoctorwhomod.client.model.Modelbronze_dalek;

/**
 * A generic renderer for any Dalek-like entity that uses the Modelbronze_dalek model.
 * It is configured with a specific texture upon creation.
 */
public class BaseDalekRenderer<T extends Mob> extends MobRenderer<T, Modelbronze_dalek<T>> {

    private final ResourceLocation texture;

    public BaseDalekRenderer(EntityRendererProvider.Context context, ResourceLocation texture) {
        super(context, new Modelbronze_dalek<>(context.bakeLayer(Modelbronze_dalek.LAYER_LOCATION)), 0.5F);
        this.texture = texture; // Store the texture provided for this specific Dalek type
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return this.texture; // Always return the texture this renderer was created with
    }
}

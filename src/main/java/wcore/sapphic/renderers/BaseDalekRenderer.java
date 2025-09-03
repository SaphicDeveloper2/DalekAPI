package wcore.sapphic.renderers;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.teabs.teabsdoctorwhomod.client.model.Modelbronze_dalek;
import wcore.sapphic.ai.AbstractDalekEntity;

public class BaseDalekRenderer<T extends AbstractDalekEntity> extends MobRenderer<T, Modelbronze_dalek<T>> {

    // This is the constructor the new system needs.
    // It gets the model from the other mod's layer definition.
    public BaseDalekRenderer(EntityRendererProvider.Context context) {
        super(context, new Modelbronze_dalek<>(context.bakeLayer(Modelbronze_dalek.LAYER_LOCATION)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        // This now correctly asks the entity for its texture,
        // allowing data-driven variants to work.
        return entity.getTexture();
    }
}

package wcore.sapphic.renderers;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.teabs.teabsdoctorwhomod.client.model.ModelCybusCyberman;
import wcore.sapphic.ai.AbstractCybermanEntity;

public class BaseCybermanRenderer<T extends AbstractCybermanEntity> extends MobRenderer<T, ModelCybusCyberman<T>> {

    // The new constructor for the modern registration system.
    // It gets the model from the other mod's layer definition.
    public BaseCybermanRenderer(EntityRendererProvider.Context context) {
        super(context, new ModelCybusCyberman<>(context.bakeLayer(ModelCybusCyberman.LAYER_LOCATION)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        // Asks the specific Cyberman entity being rendered for its texture.
        return entity.getTexture();
    }
}

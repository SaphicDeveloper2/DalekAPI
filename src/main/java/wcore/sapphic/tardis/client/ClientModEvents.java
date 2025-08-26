package wcore.sapphic.tardis.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.teabs.teabsdoctorwhomod.client.model.Modeltardis;
import wcore.sapphic.tardis.TardisRegistry;
import wcore.sapphic.tardis.TardisExteriorRenderer;
import net.sapphic.dalekapi.DalekapiMod;
import wcore.sapphic.tardis.client.DimensionSelectorRenderer;

@Mod.EventBusSubscriber(modid = DalekapiMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(TardisRegistry.TARDIS_EXTERIOR_BLOCK_ENTITY.get(), TardisExteriorRenderer::new);
        event.registerBlockEntityRenderer(TardisRegistry.DIMENSION_SELECTOR_BLOCK_ENTITY.get(), DimensionSelectorRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(final EntityRenderersEvent.RegisterLayerDefinitions event) {
        // The DimensionSelectorModel layer definition is no longer needed because the new model doesn't use it.
        event.registerLayerDefinition(Modeltardis.LAYER_LOCATION, Modeltardis::createBodyLayer);
    }
}
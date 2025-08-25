package wcore.sapphic.tardis.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.teabs.teabsdoctorwhomod.client.model.Modeltardis; // Import from the other mod
import wcore.sapphic.tardis.TardisRegistry;
import wcore.sapphic.tardis.TardisExteriorRenderer;
import net.sapphic.dalekapi.DalekapiMod;

@Mod.EventBusSubscriber(modid = DalekapiMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        // Register the Block Entity Renderer
        event.registerBlockEntityRenderer(TardisRegistry.TARDIS_EXTERIOR_BLOCK_ENTITY.get(), TardisExteriorRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(final EntityRenderersEvent.RegisterLayerDefinitions event) {
        // Register the Layer Definition using the one from the other mod's model class
        event.registerLayerDefinition(Modeltardis.LAYER_LOCATION, Modeltardis::createBodyLayer);
    }
}

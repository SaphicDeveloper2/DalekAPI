package wcore.sapphic.api.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryManager;
import wcore.sapphic.api.behavior.CybermanBehavior;
import wcore.sapphic.api.behavior.DalekBehavior;
import wcore.sapphic.api.behavior.SonicBehavior;
import net.sapphic.dalekapi.DalekapiMod;

/**
 * Declares and creates Forge custom registries for behavior hooks.
 * These registries are separate from datapack definitions and are keyed by ResourceLocation.
 */
@Mod.EventBusSubscriber(modid = DalekapiMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class BehaviorRegistries {

    public static final ResourceLocation SONIC_BEHAVIORS_NAME = new ResourceLocation(DalekapiMod.MODID, "sonic_behaviors");
    public static final ResourceLocation DALEK_BEHAVIORS_NAME = new ResourceLocation(DalekapiMod.MODID, "dalek_behaviors");
    public static final ResourceLocation CYBERMAN_BEHAVIORS_NAME = new ResourceLocation(DalekapiMod.MODID, "cyberman_behaviors");

    private BehaviorRegistries() {}

    @SubscribeEvent
    public static void onNewRegistry(NewRegistryEvent event) {
        event.create(new RegistryBuilder<SonicBehavior>()
                .setName(SONIC_BEHAVIORS_NAME)
                .disableSaving() // behaviors are code, not data
                .disableSync()   // purely logical hooks, no network sync
        );

        event.create(new RegistryBuilder<DalekBehavior>()
                .setName(DALEK_BEHAVIORS_NAME)
                .disableSaving()
                .disableSync()
        );

        event.create(new RegistryBuilder<CybermanBehavior>()
                .setName(CYBERMAN_BEHAVIORS_NAME)
                .disableSaving()
                .disableSync()
        );
    }

    public static IForgeRegistry<SonicBehavior> sonicRegistry() {
        return RegistryManager.ACTIVE.getRegistry(SONIC_BEHAVIORS_NAME);
    }

    public static IForgeRegistry<DalekBehavior> dalekRegistry() {
        return RegistryManager.ACTIVE.getRegistry(DALEK_BEHAVIORS_NAME);
    }

    public static IForgeRegistry<CybermanBehavior> cybermanRegistry() {
        return RegistryManager.ACTIVE.getRegistry(CYBERMAN_BEHAVIORS_NAME);
    }
}

package wcore.sapphic.api.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.sapphic.dalekapi.DalekapiMod;
import wcore.sapphic.api.registry.types.CybermanType;
import wcore.sapphic.api.registry.types.DalekType;
import wcore.sapphic.api.registry.types.SonicType;

/**
 * Declares and registers custom registries for Java-defined "Type" specifications.
 * This class uses DeferredRegister, the modern Forge standard, to create these registries
 * and provides the static access points needed by the rest of the mod.
 */
public final class TypesRegistries {

    private TypesRegistries() {
    }

    // --- Registry Keys ---
    // These define the unique names/keys for our custom registries.

    public static final ResourceKey<Registry<DalekType>> DALEK_TYPES_KEY =
            ResourceKey.createRegistryKey(new ResourceLocation(DalekapiMod.MODID, "dalek_types"));

    public static final ResourceKey<Registry<CybermanType>> CYBERMAN_TYPES_KEY =
            ResourceKey.createRegistryKey(new ResourceLocation(DalekapiMod.MODID, "cyberman_types"));

    public static final ResourceKey<Registry<SonicType>> SONIC_TYPES_KEY =
            ResourceKey.createRegistryKey(new ResourceLocation(DalekapiMod.MODID, "sonic_types"));


    // --- Deferred Registers ---
    // These are the core of the modern registration system. They hold our custom types
    // until Forge is ready to register them. Other classes will access these static fields.

    public static final DeferredRegister<DalekType> DALEK_TYPES =
            DeferredRegister.create(DALEK_TYPES_KEY, DalekapiMod.MODID);

    public static final DeferredRegister<CybermanType> CYBERMAN_TYPES =
            DeferredRegister.create(CYBERMAN_TYPES_KEY, DalekapiMod.MODID);

    public static final DeferredRegister<SonicType> SONIC_TYPES =
            DeferredRegister.create(SONIC_TYPES_KEY, DalekapiMod.MODID);


    /**
     * Attaches all the DeferredRegister instances in this class to the mod's event bus.
     * This method MUST be called from your main mod class's constructor.
     *
     * @param modEventBus The mod event bus from FML.
     */
    public static void register(final IEventBus modEventBus) {
        DALEK_TYPES.register(modEventBus);
        CYBERMAN_TYPES.register(modEventBus);
        SONIC_TYPES.register(modEventBus);
    }
}

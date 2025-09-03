package wcore.sapphic.api.registry;

import net.minecraft.resources.ResourceLocation;
import wcore.sapphic.api.behavior.CybermanBehavior;
import wcore.sapphic.api.behavior.DalekBehavior;
import wcore.sapphic.api.behavior.SonicBehavior;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import wcore.sapphic.api.registry.types.DalekType;
import wcore.sapphic.api.registry.types.CybermanType;
import wcore.sapphic.api.registry.types.SonicType;

/**
 * Factory: Central registry for extensible behaviors.
 * Behaviors can be provided via Forge custom registries or by runtime registration,
 * keeping datapack definitions separate and identified by matching IDs.
 */
public final class Factory {
    private Factory() {}

    private static final Map<ResourceLocation, SonicBehavior> SONIC = new ConcurrentHashMap<>();
    private static final Map<ResourceLocation, DalekBehavior> DALEK = new ConcurrentHashMap<>();
    private static final Map<ResourceLocation, CybermanBehavior> CYBERMAN = new ConcurrentHashMap<>();

    private static SonicBehavior DEFAULT_SONIC = new SonicBehavior() {};
    private static DalekBehavior DEFAULT_DALEK = new DalekBehavior() {};
    private static CybermanBehavior DEFAULT_CYBERMAN = new CybermanBehavior() {};

    public static void setDefaultSonicBehavior(SonicBehavior behavior) {
        DEFAULT_SONIC = Objects.requireNonNull(behavior);
    }

    public static void setDefaultDalekBehavior(DalekBehavior behavior) {
        DEFAULT_DALEK = Objects.requireNonNull(behavior);
    }

    public static void setDefaultCybermanBehavior(CybermanBehavior behavior) {
        DEFAULT_CYBERMAN = Objects.requireNonNull(behavior);
    }

    public static SonicBehavior registerSonicBehavior(ResourceLocation id, SonicBehavior behavior) {
        return SONIC.put(Objects.requireNonNull(id), Objects.requireNonNull(behavior));
    }

    public static DalekBehavior registerDalekBehavior(ResourceLocation id, DalekBehavior behavior) {
        return DALEK.put(Objects.requireNonNull(id), Objects.requireNonNull(behavior));
    }

    public static CybermanBehavior registerCybermanBehavior(ResourceLocation id, CybermanBehavior behavior) {
        return CYBERMAN.put(Objects.requireNonNull(id), Objects.requireNonNull(behavior));
    }

    public static SonicBehavior getSonicBehavior(ResourceLocation id) {
        // 1) Forge registry lookup (preferred)
        IForgeRegistry<SonicBehavior> reg = BehaviorRegistries.sonicRegistry();
        if (reg != null) {
            SonicBehavior fromReg = reg.getValue(id);
            if (fromReg != null) return fromReg;
        }
        // 2) Fallback to runtime-registered map (compat)
        return SONIC.getOrDefault(id, DEFAULT_SONIC);
    }

    public static DalekBehavior getDalekBehavior(ResourceLocation id) {
        IForgeRegistry<DalekBehavior> reg = BehaviorRegistries.dalekRegistry();
        if (reg != null) {
            DalekBehavior fromReg = reg.getValue(id);
            if (fromReg != null) return fromReg;
        }
        return DALEK.getOrDefault(id, DEFAULT_DALEK);
    }

    public static CybermanBehavior getCybermanBehavior(ResourceLocation id) {
        IForgeRegistry<CybermanBehavior> reg = BehaviorRegistries.cybermanRegistry();
        if (reg != null) {
            CybermanBehavior fromReg = reg.getValue(id);
            if (fromReg != null) return fromReg;
        }
        return CYBERMAN.getOrDefault(id, DEFAULT_CYBERMAN);
    }

    // -------- Java registration path for Type specs (Dalek/Cyberman/Sonic) --------

    /**
     * Create a DeferredRegister for DalekType entries under the given modid.
     * Use it in your mod constructor and call register(eventBus).
     */
    public static DeferredRegister<DalekType> createDalekTypeRegister(String modid) {
        return DeferredRegister.create(TypesRegistries.DALEK_TYPES_KEY, modid);
    }

    /**
     * Create a DeferredRegister for CybermanType entries under the given modid.
     */
    public static DeferredRegister<CybermanType> createCybermanTypeRegister(String modid) {
        return DeferredRegister.create(TypesRegistries.CYBERMAN_TYPES_KEY, modid);
    }

    /**
     * Create a DeferredRegister for SonicType entries under the given modid.
     */
    public static DeferredRegister<SonicType> createSonicTypeRegister(String modid) {
        return DeferredRegister.create(TypesRegistries.SONIC_TYPES_KEY, modid);
    }
}

package wcore.sapphic.init;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.sapphic.dalekapi.DalekapiMod;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, DalekapiMod.MODID);

    // We no longer need a static item for sonics, as they are now fully dynamic.
    // The DeferredRegister is kept for any other static items you might add later.

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}

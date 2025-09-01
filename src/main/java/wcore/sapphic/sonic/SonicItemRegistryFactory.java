package wcore.sapphic.sonic;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class SonicItemRegistryFactory {

    private static DeferredRegister<Item> itemRegister = null;
    private static String modId = "dalekapi";

    public SonicItemRegistryFactory(String modId) {
        SonicItemRegistryFactory.modId = modId;
        itemRegister = DeferredRegister.create(ForgeRegistries.ITEMS, modId);
    }

    /**
     * A simple way to register a sonic item.
     * The item model and block model JSON files must be present in the resources directory.
     *
     * @param name         The registry name for the sonic item (e.g., "sonic_pen").
     * @param itemSupplier The constructor for your sonic item class (e.g., SonicPenItem::new).
     * @return A RegistryObject for the created Item.
     */
    public static <T extends Item> RegistryObject<T> registerSonic(String name, Supplier<T> itemSupplier) {
        return itemRegister.register(name, itemSupplier);
    }

    public void register(IEventBus modEventBus) {
        itemRegister.register(modEventBus);
    }
}
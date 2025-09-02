package wcore.sapphic.init;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sapphic.dalekapi.DalekapiMod;
import wcore.sapphic.items.DataSonicItem;
import wcore.sapphic.items.ExampleSonicItem;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, DalekapiMod.MODID);

    public static final RegistryObject<Item> EXAMPLE_SONIC = ITEMS.register("example_sonic", () -> new ExampleSonicItem(new Item.Properties()));

    public static final RegistryObject<Item> DATA_SONIC = ITEMS.register("data_sonic", DataSonicItem::new);

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
package wcore.sapphic.init;

import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sapphic.dalekapi.DalekapiMod;
import wcore.sapphic.items.DataSonicItem;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, DalekapiMod.MODID);

    // The "template" item for all data-driven sonics
    public static final RegistryObject<Item> DATA_SONIC_ITEM = ITEMS.register("data_sonic_item", DataSonicItem::new);

    // The spawn egg for the data-driven Dalek template entity
    public static final RegistryObject<Item> DALEK_SPAWN_EGG = ITEMS.register("dalek_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.DATA_DALEK, 0xC0C0C0, 0x808080, new Item.Properties()));

    // The spawn egg for the data-driven Cyberman template entity
    public static final RegistryObject<Item> CYBERMAN_SPAWN_EGG = ITEMS.register("cyberman_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.DATA_CYBERMAN, 0xA9A9A9, 0x000000, new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
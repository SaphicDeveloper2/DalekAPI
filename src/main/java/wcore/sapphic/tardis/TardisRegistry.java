package wcore.sapphic.tardis;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sapphic.dalekapi.DalekapiMod;

public class TardisRegistry {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, DalekapiMod.MODID);

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, DalekapiMod.MODID);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, DalekapiMod.MODID);

    // TARDIS Exterior
    public static final RegistryObject<Block> TARDIS_EXTERIOR_BLOCK = BLOCKS.register("tardis_exterior",
            () -> new TardisExteriorBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_BLUE)
                            .strength(-1.0F, 3600000.0F) // unbreakable
                            .noOcclusion()
            ));

    public static final RegistryObject<Item> TARDIS_EXTERIOR_ITEM = ITEMS.register("tardis_exterior",
            () -> new BlockItem(TARDIS_EXTERIOR_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<BlockEntityType<TardisExteriorBlockEntity>> TARDIS_EXTERIOR_BLOCK_ENTITY =
            BLOCK_ENTITY_TYPES.register("tardis_exterior",
                    () -> BlockEntityType.Builder.of(TardisExteriorBlockEntity::new,
                            TARDIS_EXTERIOR_BLOCK.get()).build(null));

    // TARDIS Interior Door
    public static final RegistryObject<Block> TARDIS_DOOR_BLOCK = BLOCKS.register("tardis_door",
            () -> new TardisDoorBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.WOOD)
                            .strength(1.5F, 6.0F) // normal hardness/resistance
            ));

    public static final RegistryObject<Item> TARDIS_DOOR_ITEM = ITEMS.register("tardis_door",
            () -> new BlockItem(TARDIS_DOOR_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<BlockEntityType<TardisDoorBlockEntity>> TARDIS_DOOR_BLOCK_ENTITY =
            BLOCK_ENTITY_TYPES.register("tardis_door",
                    () -> BlockEntityType.Builder.of(TardisDoorBlockEntity::new,
                            TARDIS_DOOR_BLOCK.get()).build(null));

    // TARDIS Key
    public static final RegistryObject<Item> TARDIS_KEY_ITEM = ITEMS.register("tardis_key",
            () -> new TardisKeyItem(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
        BLOCK_ENTITY_TYPES.register(eventBus);
        eventBus.addListener(TardisRegistry::addCreative);
    }

    private static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(TARDIS_EXTERIOR_ITEM);
        }
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(TARDIS_KEY_ITEM);
        }
    }
}

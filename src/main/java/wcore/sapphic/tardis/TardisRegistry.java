package wcore.sapphic.tardis;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
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
import wcore.sapphic.tardis.flight.DimensionSelector.DimensionSelectorBlock;
import wcore.sapphic.tardis.flight.DimensionSelector.DimensionSelectorBlockEntity;
import wcore.sapphic.tardis.flight.DimensionSelectorFactory;

public class TardisRegistry {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, DalekapiMod.MODID);

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, DalekapiMod.MODID);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, DalekapiMod.MODID);

    // --- DIMENSION SELECTOR REGISTRATION ---
    public static final RegistryObject<Block> DIMENSION_SELECTOR =
            BLOCKS.register("dimension_selector", () -> new DimensionSelectorBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_BLUE)
                            .strength(1.0F, 10.0F)
                            .noOcclusion() // Needed for proper rendering of the custom model
            ));

    public static final RegistryObject<Item> DIMENSION_SELECTOR_ITEM = ITEMS.register("dimension_selector",
            () -> new BlockItem(DIMENSION_SELECTOR.get(), new Item.Properties()));

    public static final RegistryObject<BlockEntityType<DimensionSelectorBlockEntity>> DIMENSION_SELECTOR_BLOCK_ENTITY =
            BLOCK_ENTITY_TYPES.register("dimension_selector",
                    () -> BlockEntityType.Builder.of(DimensionSelectorBlockEntity::new,
                            DIMENSION_SELECTOR.get()).build(null));

    // Use the factory to add the default dimensions to the shared list
    static {
        // --- TEXTURE PATHS CORRECTED TO USE THE 'entity' FOLDER ---
        String basePath = "textures/entity/dimension_selector/";
        DimensionSelectorFactory.addDimension(Level.OVERWORLD, new ResourceLocation(DalekapiMod.MODID, basePath + "dimension_selector_overworld.png"));
        DimensionSelectorFactory.addDimension(Level.END, new ResourceLocation(DalekapiMod.MODID, basePath + "dimension_selector_end.png"));
        DimensionSelectorFactory.addDimension(
                ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, new ResourceLocation("teabs_doctor_who_mod", "gallifrey")),
                new ResourceLocation(DalekapiMod.MODID, basePath + "dimension_selector_gallifrey.png")
        );
        DimensionSelectorFactory.addDimension(
                ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, new ResourceLocation("teabs_doctor_who_mod", "skaro")),
                new ResourceLocation(DalekapiMod.MODID, basePath + "dimension_selector_skaro.png")
        );
    }
    // --- END DIMENSION SELECTOR REGISTRATION ---

    // TARDIS Exterior
    public static final RegistryObject<Block> TARDIS_EXTERIOR_BLOCK = BLOCKS.register("tardis_exterior",
            () -> new TardisExteriorBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_BLUE)
                            .strength(-1.0F, 3600000.0F)
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
                            .strength(1.5F, 6.0F)
            ));

    public static final RegistryObject<Item> TARDIS_DOOR_ITEM = ITEMS.register("tardis_door",
            () -> new BlockItem(TARDIS_DOOR_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<BlockEntityType<TardisDoorBlockEntity>> TARDIS_DOOR_BLOCK_ENTITY =
            BLOCK_ENTITY_TYPES.register("tardis_door",
                    () -> BlockEntityType.Builder.of(TardisDoorBlockEntity::new,
                            TARDIS_DOOR_BLOCK.get()).build(null));

    // TARDIS Key
    public static final RegistryObject<Item> TARDIS_KEY_ITEM =
            ITEMS.register("tardis_key",
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
            event.accept(DIMENSION_SELECTOR_ITEM);
        }
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(TARDIS_KEY_ITEM);
        }
    }
}
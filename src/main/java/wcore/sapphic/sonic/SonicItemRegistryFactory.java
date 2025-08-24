package wcore.sapphic.sonic;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SonicItemRegistryFactory {

    private static DeferredRegister<Item> itemRegister = null;
    private static String modId = "dalekapi";

    // This map will store the JSON for the item model file.
    private static final Map<String, String> SONIC_ITEM_MODELS = new HashMap<>();
    // This map will store the JSON for the 3D block model file.
    private static final Map<String, String> SONIC_BLOCK_MODELS = new HashMap<>();


    public SonicItemRegistryFactory(String modId) {
        SonicItemRegistryFactory.modId = modId;
        itemRegister = DeferredRegister.create(ForgeRegistries.ITEMS, modId);
    }

    /**
     * A simple way to register a sonic item with a placeholder 3D model.
     *
     * @param name         The registry name for the sonic item (e.g., "sonic_pen").
     * @param itemSupplier The constructor for your sonic item class (e.g., SonicPenItem::new).
     * @return A RegistryObject for the created Item.
     */
    public static <T extends Item> RegistryObject<T> registerSonic(String name, Supplier<T> itemSupplier) {
        // This is a placeholder 3D model. Replace its contents with your model from Blockbench.
        String defaultBlockModelJson = "{\n" +
                "  \"credit\": \"Made with Blockbench\",\n" +
                "  \"textures\": {\n" +
                "    \"0\": \"" + modId + ":block/sonics/" + name + "\",\n" +
                "    \"particle\": \"" + modId + ":block/sonics/" + name + "\"\n" +
                "  },\n" +
                "  \"elements\": [\n" +
                "    {\n" +
                "      \"from\": [7, 0, 7],\n" +
                "      \"to\": [9, 8, 9],\n" +
                "      \"faces\": {\n" +
                "        \"north\": {\"uv\": [0, 0, 2, 8], \"texture\": \"#0\"},\n" +
                "        \"east\": {\"uv\": [0, 0, 2, 8], \"texture\": \"#0\"},\n" +
                "        \"south\": {\"uv\": [0, 0, 2, 8], \"texture\": \"#0\"},\n" +
                "        \"west\": {\"uv\": [0, 0, 2, 8], \"texture\": \"#0\"},\n" +
                "        \"up\": {\"uv\": [0, 0, 2, 2], \"texture\": \"#0\"},\n" +
                "        \"down\": {\"uv\": [0, 0, 2, 2], \"texture\": \"#0\"}\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        return registerSonic(name, itemSupplier, defaultBlockModelJson);
    }

    /**
     * An advanced way to register a sonic item with a custom 3D model.
     *
     * @param name          The registry name for the sonic item.
     * @param itemSupplier  The constructor for your sonic item class.
     * @param blockModelJson The JSON string for the 3D model (from Blockbench).
     * @return A RegistryObject for the created Item.
     */
    public static <T extends Item> RegistryObject<T> registerSonic(String name, Supplier<T> itemSupplier, String blockModelJson) {
        // The item model now just points to the block model.
        String itemModelJson = "{\n" +
                "  \"parent\": \"" + modId + ":block/sonics/" + name + "\"\n" +
                "}";

        SONIC_ITEM_MODELS.put(name, itemModelJson);
        SONIC_BLOCK_MODELS.put(name, blockModelJson);
        return itemRegister.register(name, itemSupplier);
    }

    public void register(IEventBus modEventBus) {
        itemRegister.register(modEventBus);
    }

    /**
     * In a real mod, you would call this during the data generation event
     * to write the model files.
     */
    public static void generateModels() {
        // This is where you would add logic to write the contents of the maps
        // to their respective files.
        //
        // SONIC_ITEM_MODELS.forEach((name, json) -> {
        //     // Write to '.../assets/your_mod_id/models/item/' + name + '.json'
        // });
        //
        // SONIC_BLOCK_MODELS.forEach((name, json) -> {
        //     // Write to '.../assets/your_mod_id/models/block/sonics/' + name + '.json'
        // });

        SONIC_ITEM_MODELS.clear();
        SONIC_BLOCK_MODELS.clear();
    }
}

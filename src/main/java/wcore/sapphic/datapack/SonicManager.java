package wcore.sapphic.datapack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;
import wcore.sapphic.datapack.definition.SonicDefinition;
import java.util.HashMap;
import java.util.Map;

public class SonicManager extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().create();
    public static final Map<ResourceLocation, SonicDefinition> SONICS = new HashMap<>();

    public SonicManager() {
        super(GSON, "sonics");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        SONICS.clear();
        pObject.forEach((location, element) -> {
            try {
                SonicDefinition definition = GSON.fromJson(element, SonicDefinition.class);
                definition.setId(location);
                SONICS.put(location, definition);
            } catch (Exception e) {
                LOGGER.error("Couldn't parse sonic definition {}", location, e);
            }
        });
        LOGGER.info("Loaded {} sonic definitions.", SONICS.size());
    }
}
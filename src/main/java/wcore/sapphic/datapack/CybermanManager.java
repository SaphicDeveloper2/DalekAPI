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
import wcore.sapphic.datapack.definition.CybermanDefinition;
import java.util.HashMap;
import java.util.Map;

public class CybermanManager extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().create();
    public static final Map<ResourceLocation, CybermanDefinition> CYBERMEN = new HashMap<>();

    public CybermanManager() {
        super(GSON, "cybermen");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        CYBERMEN.clear();
        pObject.forEach((location, element) -> {
            try {
                CybermanDefinition definition = GSON.fromJson(element, CybermanDefinition.class);
                definition.setId(location);
                CYBERMEN.put(location, definition);
            } catch (Exception e) {
                LOGGER.error("Couldn't parse Cyberman definition {}", location, e);
            }
        });
        LOGGER.info("Loaded {} Cyberman definitions.", CYBERMEN.size());
    }
}
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
import wcore.sapphic.datapack.definition.DalekDefinition;
import java.util.HashMap;
import java.util.Map;

public class DalekManager extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().create();
    public static final Map<ResourceLocation, DalekDefinition> DALEKS = new HashMap<>();

    public DalekManager() {
        super(GSON, "daleks");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        DALEKS.clear();
        pObject.forEach((location, element) -> {
            try {
                DalekDefinition definition = GSON.fromJson(element, DalekDefinition.class);
                definition.setId(location);
                DALEKS.put(location, definition);
            } catch (Exception e) {
                LOGGER.error("Couldn't parse Dalek definition {}", location, e);
            }
        });
        LOGGER.info("Loaded {} Dalek definitions.", DALEKS.size());
    }
}
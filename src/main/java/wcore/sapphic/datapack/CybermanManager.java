package wcore.sapphic.datapack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wcore.sapphic.datapack.definition.CybermanDefinition;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class CybermanManager implements PreparableReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogManager.getLogger();
    public static final CybermanManager INSTANCE = new CybermanManager();
    private static final String FOLDER_NAME = "cybermen";

    private Map<ResourceLocation, CybermanDefinition> cybermen = new HashMap<>();

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier pPreparationBarrier, ResourceManager pResourceManager, ProfilerFiller pPreparationsProfiler, ProfilerFiller pReloadProfiler, Executor pBackgroundExecutor, Executor pGameExecutor) {
        CompletableFuture<Map<ResourceLocation, CybermanDefinition>> future = CompletableFuture.supplyAsync(() -> {
            Map<ResourceLocation, CybermanDefinition> loadedCybermen = new HashMap<>();
            Map<ResourceLocation, Resource> resources = pResourceManager.listResources(FOLDER_NAME, (location) -> location.getPath().endsWith(".json"));

            for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
                ResourceLocation fullLocation = entry.getKey();
                try (InputStream inputStream = entry.getValue().open(); Reader reader = new InputStreamReader(inputStream)) {
                    String path = fullLocation.getPath();
                    path = path.substring(FOLDER_NAME.length() + 1, path.length() - ".json".length());
                    ResourceLocation definitionId = new ResourceLocation(fullLocation.getNamespace(), path);
                    CybermanDefinition definition = GSON.fromJson(reader, CybermanDefinition.class);
                    loadedCybermen.put(definitionId, definition);
                } catch (Exception e) {
                    LOGGER.error("Error loading cyberman definition {}", fullLocation, e);
                }
            }
            return loadedCybermen;
        }, pBackgroundExecutor);

        return future.thenCompose(pPreparationBarrier::wait).thenAcceptAsync(loadedData -> {
            this.cybermen = loadedData;
            LOGGER.info("Loaded {} cyberman definitions", this.cybermen.size());
        }, pGameExecutor);
    }

    public Map<ResourceLocation, CybermanDefinition> getCybermen() {
        return cybermen;
    }

    public CybermanDefinition getCyberman(ResourceLocation location) {
        return cybermen.get(location);
    }
}
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
import wcore.sapphic.datapack.definition.SonicDefinition;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class SonicManager implements PreparableReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogManager.getLogger();
    public static final SonicManager INSTANCE = new SonicManager();
    private static final String FOLDER_NAME = "sonics";

    private Map<ResourceLocation, SonicDefinition> sonics = new HashMap<>();

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier pPreparationBarrier, ResourceManager pResourceManager, ProfilerFiller pPreparationsProfiler, ProfilerFiller pReloadProfiler, Executor pBackgroundExecutor, Executor pGameExecutor) {

        CompletableFuture<Map<ResourceLocation, SonicDefinition>> future = CompletableFuture.supplyAsync(() -> {
            Map<ResourceLocation, SonicDefinition> loadedSonics = new HashMap<>();

            // Find all json files in the 'sonics' folder across all data packs
            Map<ResourceLocation, Resource> resources = pResourceManager.listResources(FOLDER_NAME, (location) -> location.getPath().endsWith(".json"));

            for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
                ResourceLocation fullLocation = entry.getKey();

                try (InputStream inputStream = entry.getValue().open(); Reader reader = new InputStreamReader(inputStream)) {
                    // The path will be like "sonics/my_sonic.json". We need to strip the folder and extension
                    // to get the actual ID, which is "my_sonic".
                    String path = fullLocation.getPath();
                    path = path.substring(FOLDER_NAME.length() + 1, path.length() - ".json".length());

                    ResourceLocation definitionId = new ResourceLocation(fullLocation.getNamespace(), path);
                    SonicDefinition definition = GSON.fromJson(reader, SonicDefinition.class);
                    loadedSonics.put(definitionId, definition);
                } catch (Exception e) {
                    LOGGER.error("Error loading sonic definition {}", fullLocation, e);
                }
            }
            return loadedSonics;
        }, pBackgroundExecutor);

        // This ensures the main thread waits for the background loading to complete before applying the data
        return future.thenCompose(pPreparationBarrier::wait).thenAcceptAsync(loadedData -> {
            // This part runs on the main game thread
            this.sonics = loadedData;
            LOGGER.info("Loaded {} sonic definitions", this.sonics.size());
        }, pGameExecutor);
    }

    public Map<ResourceLocation, SonicDefinition> getSonics() {
        return this.sonics;
    }

    public SonicDefinition getSonic(ResourceLocation location) {
        return this.sonics.get(location);
    }
}
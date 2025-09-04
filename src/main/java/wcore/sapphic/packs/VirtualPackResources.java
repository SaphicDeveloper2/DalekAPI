package wcore.sapphic.packs;

import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.resources.IoSupplier;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Set;

/**
 * A wrapper around a PackResources object (zip or folder).
 * Its primary purpose is to intercept requests for pack metadata. If a real pack.mcmeta
 * exists, it's used. If not, this class generates a default one in memory, making
 * any folder or zip a valid-looking pack for Minecraft's systems.
 * (User-corrected version)
 */
public class VirtualPackResources implements PackResources {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final PackResources delegate;
    private final PackMetadataSection generatedMeta;

    public VirtualPackResources(String packId, Path source) {
        // PathPackResources now handles both folders and zips in 1.20.1
        this.delegate = new PathPackResources(packId, source, true);

        // Pack format 15 = 1.20.1
        this.generatedMeta = new PackMetadataSection(
                Component.literal("Generated DalekAPI Pack"),
                15
        );
    }

    @Nullable
    @Override
    public <T> T getMetadataSection(MetadataSectionSerializer<T> deserializer) throws IOException {
        try {
            // Try to get the real metadata first. If a pack.mcmeta exists, we'll use it.
            T realMetadata = delegate.getMetadataSection(deserializer);
            if (realMetadata != null) {
                return realMetadata;
            }
        } catch (Exception e) {
            LOGGER.debug("No real metadata found for '{}'. Falling back to generated metadata.", deserializer.getMetadataSectionName());
        }

        // If Minecraft is asking for "pack" metadata and we couldn't find a real one, provide our generated version.
        if ("pack".equals(deserializer.getMetadataSectionName())) {
            @SuppressWarnings("unchecked")
            T packMeta = (T) this.generatedMeta;
            return packMeta;
        }

        return null;
    }

    // ---- Delegate all other methods to the wrapped PackResources ----

    @Override
    public String packId() {
        return delegate.packId();
    }

    @Override
    public void close() {
        delegate.close();
    }

    @Override
    public IoSupplier<InputStream> getRootResource(String... elements) {
        return delegate.getRootResource(elements);
    }

    @Override
    public IoSupplier<InputStream> getResource(PackType type, ResourceLocation location) {
        return delegate.getResource(type, location);
    }

    @Override
    public void listResources(PackType type, String namespace, String path, ResourceOutput output) {
        delegate.listResources(type, namespace, path, output);
    }

    @Override
    public Set<String> getNamespaces(PackType type) {
        return delegate.getNamespaces(type);
    }
}


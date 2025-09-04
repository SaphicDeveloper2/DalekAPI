package wcore.sapphic.packs;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Manages the dynamic loading of textures from user-created packs.
 * This is used by entities to get their textures at runtime.
 */
public final class PackAssetManager {
    public static final PackAssetManager INSTANCE = new PackAssetManager();
    private static final Logger LOGGER = LogManager.getLogger("DalekAPI PackAssetManager");

    private final Map<ResourceLocation, Path> texturePaths = new ConcurrentHashMap<>();
    private final Map<ResourceLocation, ResourceLocation> registeredTextures = new ConcurrentHashMap<>();

    private PackAssetManager() {}

    /**
     * Scans a pack's assets directory specifically for texture files.
     */
    public void loadAssetsFromPack(Path packDir, String packId) {
        Path assetsDir = packDir.resolve("assets");
        if (Files.notExists(assetsDir)) return;

        try (Stream<Path> namespaces = Files.list(assetsDir)) {
            namespaces.forEach(namespaceDir -> {
                if (Files.isDirectory(namespaceDir)) {
                    String namespace = namespaceDir.getFileName().toString();
                    Path texturesDir = namespaceDir.resolve("textures");
                    if (Files.exists(texturesDir)) {
                        try (Stream<Path> walk = Files.walk(texturesDir)) {
                            walk.filter(p -> !Files.isDirectory(p) && p.toString().endsWith(".png")).forEach(path -> {
                                String relative = texturesDir.relativize(path).toString().replace("\\", "/");
                                ResourceLocation id = new ResourceLocation(namespace, relative.replace(".png", ""));
                                texturePaths.put(id, path);
                            });
                        } catch (IOException e) {
                            LOGGER.error("Failed to scan textures for pack: {}", packId, e);
                        }
                    }
                }
            });
        } catch (IOException e) {
            LOGGER.error("Failed to list namespaces for pack: " + packId, e);
        }
    }

    /**
     * Gets a dynamically loaded texture. If not already loaded, it reads the PNG from the
     * pack, registers it with Minecraft's TextureManager, and returns the new location.
     */
    public ResourceLocation getTexture(ResourceLocation originalLocation) {
        return registeredTextures.computeIfAbsent(originalLocation, loc -> {
            Path texturePath = texturePaths.get(loc);
            if (texturePath == null) {
                LOGGER.warn("Could not find pack texture path for: {}", loc);
                return TextureManager.INTENTIONAL_MISSING_TEXTURE;
            }
            try (InputStream stream = Files.newInputStream(texturePath)) {
                NativeImage image = NativeImage.read(stream);
                DynamicTexture texture = new DynamicTexture(image);
                // Create a unique location for the generated texture to avoid conflicts
                ResourceLocation texLoc = new ResourceLocation(loc.getNamespace(), "generated/textures/" + loc.getPath());
                Minecraft.getInstance().getTextureManager().register(texLoc, texture);
                return texLoc;
            } catch (IOException e) {
                LOGGER.error("Failed to load dynamic texture: {}", loc, e);
                return TextureManager.INTENTIONAL_MISSING_TEXTURE;
            }
        });
    }

    public void clear() {
        texturePaths.clear();
        registeredTextures.clear(); // Note: This doesn't un-register textures, just clears our cache
    }

    public void logCounts() {
        LOGGER.info("... Asset Manager found {} texture paths.", texturePaths.size());
    }
}


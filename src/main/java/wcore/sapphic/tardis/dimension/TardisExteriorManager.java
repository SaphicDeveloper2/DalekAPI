package wcore.sapphic.tardis.dimension;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TardisExteriorManager extends SavedData {
    private static final String FILE_NAME = "tardis_exterior_manager";

    // We store the dimension key as a string so it can be saved to NBT
    private final Map<UUID, TARDISLocation> exteriorLocations = new HashMap<>();

    public static class TARDISLocation {
        public final ResourceKey<Level> dimension;
        public final BlockPos pos;

        public TARDISLocation(ResourceKey<Level> dimension, BlockPos pos) {
            this.dimension = dimension;
            this.pos = pos;
        }
    }

    public static TardisExteriorManager get(ServerLevel level) {
        // This manager, like the interior one, is stored on the overworld.
        DimensionDataStorage storage = level.getServer().overworld().getDataStorage();
        return storage.computeIfAbsent(TardisExteriorManager::load, TardisExteriorManager::new, FILE_NAME);
    }

    public void setExteriorLocation(UUID tardisId, ResourceKey<Level> dimension, BlockPos pos) {
        exteriorLocations.put(tardisId, new TARDISLocation(dimension, pos));
        setDirty();
    }

    public void removeExteriorLocation(UUID tardisId) {
        exteriorLocations.remove(tardisId);
        setDirty();
    }

    public TARDISLocation getExteriorLocation(UUID tardisId) {
        return exteriorLocations.get(tardisId);
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        ListTag list = new ListTag();
        exteriorLocations.forEach((uuid, loc) -> {
            CompoundTag entry = new CompoundTag();
            entry.putUUID("id", uuid);
            entry.putString("dim", loc.dimension.location().toString());
            entry.put("pos", NbtUtils.writeBlockPos(loc.pos));
            list.add(entry);
        });
        nbt.put("exteriors", list);
        return nbt;
    }

    public static TardisExteriorManager load(CompoundTag nbt) {
        TardisExteriorManager manager = new TardisExteriorManager();
        ListTag list = nbt.getList("exteriors", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            UUID id = entry.getUUID("id");
            ResourceKey<Level> dim = ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, new ResourceLocation(entry.getString("dim")));
            BlockPos pos = NbtUtils.readBlockPos(entry.getCompound("pos"));
            manager.exteriorLocations.put(id, new TARDISLocation(dim, pos));
        }
        return manager;
    }
}
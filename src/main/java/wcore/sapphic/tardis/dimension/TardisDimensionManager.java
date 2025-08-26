package wcore.sapphic.tardis.dimension;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class TardisDimensionManager extends SavedData {
    private static final String FILE_NAME = "tardis_dimension_manager";
    private int nextAvailableX = 0;

    // The single map holding all data for every TARDIS interior.
    private final Map<UUID, TARDISInteriorData> interiorDataMap = new HashMap<>();

    /**
     * A data-holding class that contains all information about a single TARDIS interior.
     * This is similar to the TARDISLocation class in your exterior manager.
     */
    public static class TARDISInteriorData {
        public final BlockPos placementCenter;
        public BlockPos spawnPoint; // Can be null until the interior is built
        public boolean isInitialized = false;
        public boolean isAwaitingDoor = false;

        public TARDISInteriorData(BlockPos placementCenter) {
            this.placementCenter = placementCenter;
        }

        // Methods to save and load this specific object's data
        public CompoundTag save() {
            CompoundTag nbt = new CompoundTag();
            nbt.put("placementCenter", NbtUtils.writeBlockPos(placementCenter));
            if (spawnPoint != null) {
                nbt.put("spawnPoint", NbtUtils.writeBlockPos(spawnPoint));
            }
            nbt.putBoolean("isInitialized", isInitialized);
            nbt.putBoolean("isAwaitingDoor", isAwaitingDoor);
            return nbt;
        }

        public static TARDISInteriorData load(CompoundTag nbt) {
            BlockPos placementCenter = NbtUtils.readBlockPos(nbt.getCompound("placementCenter"));
            TARDISInteriorData data = new TARDISInteriorData(placementCenter);
            if (nbt.contains("spawnPoint")) {
                data.spawnPoint = NbtUtils.readBlockPos(nbt.getCompound("spawnPoint"));
            }
            data.isInitialized = nbt.getBoolean("isInitialized");
            data.isAwaitingDoor = nbt.getBoolean("isAwaitingDoor");
            return data;
        }
    }

    public TardisDimensionManager() {}

    public static TardisDimensionManager get(ServerLevel level) {
        DimensionDataStorage storage = level.getServer().overworld().getDataStorage();
        return storage.computeIfAbsent(TardisDimensionManager::load, TardisDimensionManager::new, FILE_NAME);
    }

    /**
     * Gets the data object for a TARDIS. Creates a new one if it doesn't exist.
     */
    public TARDISInteriorData getOrCreateInteriorData(UUID tardisId) {
        return interiorDataMap.computeIfAbsent(tardisId, id -> {
            BlockPos newCenter = new BlockPos(nextAvailableX, 100, 0);
            nextAvailableX += 1000;
            setDirty();
            return new TARDISInteriorData(newCenter);
        });
    }

    /**
     * Helper method to get data for an existing TARDIS.
     */
    public Optional<TARDISInteriorData> getInteriorData(UUID tardisId) {
        return Optional.ofNullable(interiorDataMap.get(tardisId));
    }

    /**
     * Finds which TARDIS territory a position is in.
     */
    public Optional<UUID> findTardisForPos(BlockPos posInQuestion) {
        for (Map.Entry<UUID, TARDISInteriorData> entry : interiorDataMap.entrySet()) {
            if (posInQuestion.distSqr(entry.getValue().placementCenter) < 500 * 500) {
                return Optional.of(entry.getKey());
            }
        }
        return Optional.empty();
    }

    public void setDirty() {
        super.setDirty();
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        ListTag dataList = new ListTag();
        interiorDataMap.forEach((uuid, data) -> {
            CompoundTag entry = new CompoundTag();
            entry.putUUID("id", uuid);
            entry.put("data", data.save());
            dataList.add(entry);
        });
        nbt.put("interiorDataMap", dataList);
        nbt.putInt("nextX", nextAvailableX);
        return nbt;
    }

    public static TardisDimensionManager load(CompoundTag nbt) {
        TardisDimensionManager manager = new TardisDimensionManager();
        ListTag dataList = nbt.getList("interiorDataMap", Tag.TAG_COMPOUND);
        for (int i = 0; i < dataList.size(); i++) {
            CompoundTag entry = dataList.getCompound(i);
            UUID id = entry.getUUID("id");
            TARDISInteriorData data = TARDISInteriorData.load(entry.getCompound("data"));
            manager.interiorDataMap.put(id, data);
        }
        manager.nextAvailableX = nbt.getInt("nextX");
        return manager;
    }
}
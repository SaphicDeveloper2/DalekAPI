package wcore.sapphic.tardis.dimension;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.*;

public class TardisDimensionManager extends SavedData {
    private static final String FILE_NAME = "tardis_dimension_manager";

    private final Map<UUID, BlockPos> tardisInteriors = new HashMap<>();
    private int nextAvailableX = 0;
    private final Set<UUID> interiorsAwaitingDoor = new HashSet<>();
    // --- NEW: A set to track which interiors have been built ---
    private final Set<UUID> initializedInteriors = new HashSet<>();

    public static TardisDimensionManager get(ServerLevel level) {
        DimensionDataStorage storage = level.getServer().overworld().getDataStorage();
        return storage.computeIfAbsent(TardisDimensionManager::load, TardisDimensionManager::new, FILE_NAME);
    }

    public BlockPos getOrCreateInteriorPos(UUID tardisId) {
        return tardisInteriors.computeIfAbsent(tardisId, id -> {
            BlockPos newPos = new BlockPos(nextAvailableX, 100, 0);
            nextAvailableX += 1000;
            setDirty();
            return newPos;
        });
    }

    // --- NEW METHODS for tracking initialization ---
    public boolean isInteriorInitialized(UUID tardisId) {
        return initializedInteriors.contains(tardisId);
    }

    public void markInteriorAsInitialized(UUID tardisId) {
        initializedInteriors.add(tardisId);
        setDirty();
    }


    // --- (Existing methods for door linking) ---
    public Optional<UUID> findTardisForPos(BlockPos posInQuestion) {
        for (Map.Entry<UUID, BlockPos> entry : tardisInteriors.entrySet()) {
            if (posInQuestion.distSqr(entry.getValue()) < 200 * 200) {
                return Optional.of(entry.getKey());
            }
        }
        return Optional.empty();
    }

    public boolean isAwaitingDoor(UUID tardisId) {
        return interiorsAwaitingDoor.contains(tardisId);
    }

    public void markDoorAsBroken(UUID tardisId) {
        interiorsAwaitingDoor.add(tardisId);
        setDirty();
    }

    public void markDoorAsReplaced(UUID tardisId) {
        interiorsAwaitingDoor.remove(tardisId);
        setDirty();
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        // ... (save tardisInteriors and nextAvailableX as before)
        ListTag interiorsList = new ListTag();
        tardisInteriors.forEach((uuid, pos) -> {
            CompoundTag entry = new CompoundTag();
            entry.putUUID("id", uuid);
            entry.put("pos", NbtUtils.writeBlockPos(pos));
            interiorsList.add(entry);
        });
        nbt.put("interiors", interiorsList);
        nbt.putInt("nextX", nextAvailableX);

        // ... (save interiorsAwaitingDoor as before)
        ListTag awaitingDoorsList = new ListTag();
        interiorsAwaitingDoor.forEach(uuid -> {
            CompoundTag idTag = new CompoundTag();
            idTag.putUUID("id", uuid);
            awaitingDoorsList.add(idTag);
        });
        nbt.put("awaitingDoors", awaitingDoorsList);

        // --- NEW: Save the initialized interiors list ---
        ListTag initializedList = new ListTag();
        initializedInteriors.forEach(uuid -> {
            CompoundTag idTag = new CompoundTag();
            idTag.putUUID("id", uuid);
            initializedList.add(idTag);
        });
        nbt.put("initializedInteriors", initializedList);

        return nbt;
    }

    public static TardisDimensionManager load(CompoundTag nbt) {
        TardisDimensionManager manager = new TardisDimensionManager();
        // ... (load tardisInteriors, nextAvailableX, and interiorsAwaitingDoor as before)
        ListTag interiorsList = nbt.getList("interiors", Tag.TAG_COMPOUND);
        for (int i = 0; i < interiorsList.size(); i++) {
            CompoundTag entry = interiorsList.getCompound(i);
            manager.tardisInteriors.put(entry.getUUID("id"), NbtUtils.readBlockPos(entry.getCompound("pos")));
        }
        manager.nextAvailableX = nbt.getInt("nextX");
        ListTag awaitingDoorsList = nbt.getList("awaitingDoors", Tag.TAG_COMPOUND);
        for (int i = 0; i < awaitingDoorsList.size(); i++) {
            manager.interiorsAwaitingDoor.add(awaitingDoorsList.getCompound(i).getUUID("id"));
        }

        // --- NEW: Load the initialized interiors list ---
        ListTag initializedList = nbt.getList("initializedInteriors", Tag.TAG_COMPOUND);
        for (int i = 0; i < initializedList.size(); i++) {
            manager.initializedInteriors.add(initializedList.getCompound(i).getUUID("id"));
        }

        return manager;
    }
}
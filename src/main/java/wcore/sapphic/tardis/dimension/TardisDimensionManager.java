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
import java.util.UUID;

public class TardisDimensionManager extends SavedData {
    private static final String FILE_NAME = "tardis_dimension_manager";

    private final Map<UUID, BlockPos> tardisInteriors = new HashMap<>();
    private int nextAvailableX = 0;

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

    @Override
    public CompoundTag save(CompoundTag nbt) {
        ListTag list = new ListTag();
        tardisInteriors.forEach((uuid, pos) -> {
            CompoundTag entry = new CompoundTag();
            entry.putUUID("id", uuid);
            entry.put("pos", NbtUtils.writeBlockPos(pos));
            list.add(entry);
        });
        nbt.put("interiors", list);
        nbt.putInt("nextX", nextAvailableX);
        return nbt;
    }

    public static TardisDimensionManager load(CompoundTag nbt) {
        TardisDimensionManager manager = new TardisDimensionManager();
        ListTag list = nbt.getList("interiors", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            manager.tardisInteriors.put(entry.getUUID("id"), NbtUtils.readBlockPos(entry.getCompound("pos")));
        }
        manager.nextAvailableX = nbt.getInt("nextX");
        return manager;
    }
}
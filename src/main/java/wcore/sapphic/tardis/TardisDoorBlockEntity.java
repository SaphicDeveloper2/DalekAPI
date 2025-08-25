package wcore.sapphic.tardis;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public class TardisDoorBlockEntity extends BlockEntity {

    private UUID tardisId;

    public TardisDoorBlockEntity(BlockPos pos, BlockState state) {
        super(TardisRegistry.TARDIS_DOOR_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (this.tardisId != null) {
            tag.putUUID("tardisId", this.tardisId);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.hasUUID("tardisId")) {
            this.tardisId = tag.getUUID("tardisId");
        }
    }

    public UUID getTardisId() {
        return tardisId;
    }

    public void setTardisId(UUID tardisId) {
        this.tardisId = tardisId;
        setChanged();
    }
}
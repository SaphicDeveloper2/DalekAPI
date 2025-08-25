package wcore.sapphic.tardis;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public class TardisExteriorBlockEntity extends BlockEntity {

    private boolean isLocked = false;
    private UUID tardisId; // Each TARDIS now has a unique ID

    public TardisExteriorBlockEntity(BlockPos pos, BlockState state) {
        super(TardisRegistry.TARDIS_EXTERIOR_BLOCK_ENTITY.get(), pos, state);
    }

    // Ensures a TARDIS gets an ID the first time it's created
    public UUID getTardisId() {
        if (this.tardisId == null) {
            this.tardisId = UUID.randomUUID();
            setChanged();
        }
        return this.tardisId;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("isLocked", this.isLocked);
        if (this.tardisId != null) {
            tag.putUUID("tardisId", this.tardisId);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.isLocked = tag.getBoolean("isLocked");
        if (tag.hasUUID("tardisId")) {
            this.tardisId = tag.getUUID("tardisId");
        }
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        this.isLocked = locked;
        setChanged();
    }
}
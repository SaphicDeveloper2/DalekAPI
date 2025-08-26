package wcore.sapphic.tardis;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import wcore.sapphic.tardis.flight.TardisFlightState;

import java.util.UUID;

public class TardisExteriorBlockEntity extends BlockEntity {

    private boolean isLocked = false;
    private UUID tardisId;

    // --- ADDED FLIGHT DATA ---
    private TardisFlightState flightState = TardisFlightState.LANDED;
    private ResourceKey<Level> targetDimension;
    // --- END ADDED FLIGHT DATA ---

    public TardisExteriorBlockEntity(BlockPos pos, BlockState state) {
        super(TardisRegistry.TARDIS_EXTERIOR_BLOCK_ENTITY.get(), pos, state);
    }

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
        // --- ADDED NBT SAVE ---
        tag.putString("flightState", this.flightState.name());
        if (this.targetDimension != null) {
            tag.putString("targetDimension", this.targetDimension.location().toString());
        }
        // --- END ADDED NBT SAVE ---
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.isLocked = tag.getBoolean("isLocked");
        if (tag.hasUUID("tardisId")) {
            this.tardisId = tag.getUUID("tardisId");
        }
        // --- ADDED NBT LOAD ---
        if (tag.contains("flightState")) {
            this.flightState = TardisFlightState.valueOf(tag.getString("flightState"));
        }
        if (tag.contains("targetDimension")) {
            this.targetDimension = ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, new ResourceLocation(tag.getString("targetDimension")));
        }
        // --- END ADDED NBT LOAD ---
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        this.isLocked = locked;
        setChanged();
    }

    // --- ADDED GETTERS AND SETTERS ---
    public TardisFlightState getFlightState() {
        return flightState;
    }

    public void setFlightState(TardisFlightState flightState) {
        this.flightState = flightState;
        setChanged();
    }

    public ResourceKey<Level> getTargetDimension() {
        return targetDimension;
    }

    public void setTargetDimension(ResourceKey<Level> targetDimension) {
        this.targetDimension = targetDimension;
        setChanged();
    }
    // --- END ADDED GETTERS AND SETTERS ---
}
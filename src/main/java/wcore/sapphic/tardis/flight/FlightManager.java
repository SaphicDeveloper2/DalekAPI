package wcore.sapphic.tardis.flight;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class FlightManager {

    private TardisFlightState flightState = TardisFlightState.LANDED;
    private int flightTicks = 0;
    private ResourceKey<Level> targetDimension;

    public TardisFlightState getFlightState() {
        return flightState;
    }

    public void setFlightState(TardisFlightState flightState) {
        this.flightState = flightState;
    }

    public int getFlightTicks() {
        return flightTicks;
    }

    public void setFlightTicks(int flightTicks) {
        this.flightTicks = flightTicks;
    }

    public void tick() {
        if (flightState != TardisFlightState.LANDED) {
            flightTicks++;
        }
    }

    public ResourceKey<Level> getTargetDimension() {
        return targetDimension;
    }

    public void setTargetDimension(ResourceKey<Level> targetDimension) {
        this.targetDimension = targetDimension;
    }

    public void save(CompoundTag tag) {
        tag.putString("flightState", flightState.name());
        tag.putInt("flightTicks", flightTicks);
        if (targetDimension != null) {
            tag.putString("targetDimension", targetDimension.location().toString());
        }
    }

    public void load(CompoundTag tag) {
        this.flightState = TardisFlightState.valueOf(tag.getString("flightState"));
        this.flightTicks = tag.getInt("flightTicks");
        if (tag.contains("targetDimension")) {
            this.targetDimension = ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, new net.minecraft.resources.ResourceLocation(tag.getString("targetDimension")));
        }
    }
}
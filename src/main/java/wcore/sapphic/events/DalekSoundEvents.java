package wcore.sapphic.events;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

// This class now lives in your API.
// Its purpose is to provide easy access to the sounds from Teab's mod.
public class DalekSoundEvents {

    private static SoundEvent createExisting(String soundName) {
        // We know the mod ID is "teabs_doctor_who_mod", so we can hard-code it.
        return SoundEvent.createVariableRangeEvent(new ResourceLocation("teabs_doctor_who_mod", soundName));
    }

    // References to the sounds from Teab's Doctor Who Mod
    public static final SoundEvent DALEK_AMBIENT = createExisting("entity.dalek.ambient");
    public static final SoundEvent DALEK_HURT = createExisting("entity.dalek.hurt");
    public static final SoundEvent DALEK_DEATH = createExisting("entity.dalek.death");
    public static final SoundEvent DALEK_EXTERMINATE = createExisting("entity.dalek.exterminate");

}
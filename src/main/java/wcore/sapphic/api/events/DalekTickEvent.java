package wcore.sapphic.api.events;

import net.minecraftforge.event.entity.living.LivingEvent;
import wcore.sapphic.ai.AbstractDalekEntity;
import wcore.sapphic.datapack.definition.DalekDefinition;

public class DalekTickEvent extends LivingEvent {
    private final AbstractDalekEntity dalek;
    private final DalekDefinition definition;

    public DalekTickEvent(AbstractDalekEntity dalek, DalekDefinition definition) {
        super(dalek);
        this.dalek = dalek;
        this.definition = definition;
    }

    @Override
    public AbstractDalekEntity getEntity() {
        return dalek;
    }

    public DalekDefinition getDefinition() {
        return definition;
    }
}
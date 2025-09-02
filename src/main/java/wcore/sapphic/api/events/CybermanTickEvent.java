package wcore.sapphic.api.events;

import net.minecraftforge.event.entity.living.LivingEvent;
import wcore.sapphic.ai.AbstractCybermanEntity;
import wcore.sapphic.datapack.definition.CybermanDefinition;

public class CybermanTickEvent extends LivingEvent {
    private final AbstractCybermanEntity cyberman;
    private final CybermanDefinition definition;

    public CybermanTickEvent(AbstractCybermanEntity cyberman, CybermanDefinition definition) {
        super(cyberman);
        this.cyberman = cyberman;
        this.definition = definition;
    }

    @Override
    public AbstractCybermanEntity getEntity() {
        return cyberman;
    }

    public CybermanDefinition getDefinition() {
        return definition;
    }
}
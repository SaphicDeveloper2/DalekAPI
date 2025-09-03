package wcore.sapphic.api.behavior;

import wcore.sapphic.ai.AbstractCybermanEntity;
import wcore.sapphic.datapack.definition.CybermanDefinition;
import wcore.sapphic.entities.DataCybermanEntity;

/**
 * Behavior hook for data-driven Cyberman entities.
 * Modders can register implementations per CybermanDefinition id via Factory.
 */
public interface CybermanBehavior {

    /**
     * Called each server tick for data-driven cybermen.
     */
    default void onTick(DataCybermanEntity entity, CybermanDefinition definition) {}

    /**
     * Called after the entity's base goals are registered, allowing behavior to add/modify goals.
     */
    default void configureGoals(AbstractCybermanEntity entity, CybermanDefinition definition) {}
}

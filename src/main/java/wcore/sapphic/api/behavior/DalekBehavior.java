package wcore.sapphic.api.behavior;

import wcore.sapphic.ai.AbstractDalekEntity;
import wcore.sapphic.datapack.definition.DalekDefinition;
import wcore.sapphic.entities.DataDalekEntity;

/**
 * Behavior hook for data-driven Dalek entities.
 * Modders can register implementations per DalekDefinition id via Factory.
 */
public interface DalekBehavior {

    /**
     * Called each server tick for data-driven daleks.
     */
    default void onTick(DataDalekEntity entity, DalekDefinition definition) {}

    /**
     * Called after the entity's base goals are registered, allowing behavior to add/modify goals.
     */
    default void configureGoals(AbstractDalekEntity entity, DalekDefinition definition) {}
}

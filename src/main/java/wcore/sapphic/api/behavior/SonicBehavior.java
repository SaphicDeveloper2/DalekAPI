package wcore.sapphic.api.behavior;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import wcore.sapphic.datapack.definition.SonicDefinition;

/**
 * Behavior hook for data-driven Sonic items.
 * Modders can register implementations per SonicDefinition id via Factory.
 */
public interface SonicBehavior {

    /**
     * Called when a sonic is used on a block.
     * Return:
     * - InteractionResult.PASS to allow base/fallback sonic behavior to run.
     * - A consuming result (e.g., SUCCESS, CONSUME, FAIL) to override base behavior.
     */
    default InteractionResult onUseOnBlock(UseOnContext context, SonicDefinition definition) {
        return InteractionResult.PASS;
    }
}

package wcore.sapphic.api.events;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import wcore.sapphic.datapack.definition.SonicDefinition;

/**
 * Fired when a player uses a data-driven sonic screwdriver on a block.
 * This event is fired before the default sonic logic in BaseSonicItem is executed.
 * <p>
 * This event is {@link Cancelable}.
 * <p>
 * If this event is canceled, the default sonic screwdriver logic (from BaseSonicItem.onSonicUse)
 * will not be executed. This allows addon mods to completely override the sonic's behavior
 * for specific sonic definitions.
 */
@Cancelable
public class SonicUseOnBlockEvent extends PlayerEvent {
    private final UseOnContext context;
    private final SonicDefinition definition;

    public SonicUseOnBlockEvent(Player player, UseOnContext context, SonicDefinition definition) {
        super(player);
        this.context = context;
        this.definition = definition;
    }

    /**
     * @return The full context of the item usage, including level, block position, player, and item stack.
     */
    public UseOnContext getContext() {
        return context;
    }

    /**
     * @return The SonicDefinition of the specific sonic being used, loaded from a data pack.
     */
    public SonicDefinition getDefinition() {
        return definition;
    }
}

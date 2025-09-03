package wcore.sapphic.api.easy;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

/**
 * A simple, configurable mob that defers AI/tick behavior to EasyEntityRegistry.
 * Use this when you want to define entities entirely via the API without writing a custom class.
 */
public class GenericMobEntity extends PathfinderMob {

    public GenericMobEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        EasyEntityRegistry.getGoalsConfigurer(this.getType()).ifPresent(cfg -> cfg.configure(this));
    }

    @Override
    public void tick() {
        super.tick();
        EasyEntityRegistry.getTickHandler(this.getType()).ifPresent(h -> h.tick(this));
    }

    public interface GoalsConfigurer<T extends Mob> {
        void configure(T mob);
    }

    public interface TickHandler<T extends Mob> {
        void tick(T mob);
    }
}

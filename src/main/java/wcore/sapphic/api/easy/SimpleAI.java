package wcore.sapphic.api.easy;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.npc.Villager;

/**
 * Tiny DSL for common mob AI setup.
 * Use inside EasyEntityRegistry.registerSimpleMob goals configurer.
 */
public class SimpleAI<T extends Mob> {

    private final T mob;

    public SimpleAI(T mob) {
        this.mob = mob;
    }

    public SimpleAI<T> floatGoal(int priority) {
        mob.goalSelector.addGoal(priority, new FloatGoal(mob));
        return this;
    }

    public SimpleAI<T> randomStroll(int priority, double speed) {
        mob.goalSelector.addGoal(priority, new WaterAvoidingRandomStrollGoal((PathfinderMob) mob, speed));
        return this;
    }

    public SimpleAI<T> randomLook(int priority) {
        mob.goalSelector.addGoal(priority, new RandomLookAroundGoal(mob));
        return this;
    }

    public SimpleAI<T> lookAtPlayer(int priority, double range) {
        mob.goalSelector.addGoal(priority, new LookAtPlayerGoal(mob, Player.class, (float) range));
        return this;
    }

    public SimpleAI<T> meleeAttack(int priority, double speed, boolean pauseWhenIdle) {
        mob.goalSelector.addGoal(priority, new MeleeAttackGoal((PathfinderMob) mob, speed, pauseWhenIdle));
        return this;
    }

    public SimpleAI<T> hurtByTarget(int priority) {
        mob.targetSelector.addGoal(priority, new HurtByTargetGoal((PathfinderMob) mob));
        return this;
    }

    public SimpleAI<T> targetPlayers(int priority, boolean mustSee, boolean mustReach) {
        mob.targetSelector.addGoal(priority, new NearestAttackableTargetGoal<>(mob, Player.class, mustSee, mustReach));
        return this;
    }

    public SimpleAI<T> targetVillagers(int priority, boolean mustSee, boolean mustReach) {
        mob.targetSelector.addGoal(priority, new NearestAttackableTargetGoal<>(mob, Villager.class, mustSee, mustReach));
        return this;
    }
}

package wcore.sapphic.api.easy;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * Fluent helper to build common mob attributes.
 */
public class MobAttributes {
    private final AttributeSupplier.Builder builder;

    public MobAttributes() {
        this.builder = Mob.createMobAttributes();
    }

    public MobAttributes maxHealth(double v) {
        builder.add(Attributes.MAX_HEALTH, v);
        return this;
    }

    public MobAttributes moveSpeed(double v) {
        builder.add(Attributes.MOVEMENT_SPEED, v);
        return this;
    }

    public MobAttributes attackDamage(double v) {
        builder.add(Attributes.ATTACK_DAMAGE, v);
        return this;
    }

    public MobAttributes followRange(double v) {
        builder.add(Attributes.FOLLOW_RANGE, v);
        return this;
    }

    public MobAttributes armor(double v) {
        builder.add(Attributes.ARMOR, v);
        return this;
    }

    public MobAttributes knockbackResistance(double v) {
        builder.add(Attributes.KNOCKBACK_RESISTANCE, v);
        return this;
    }

    public AttributeSupplier.Builder build() {
        return builder;
    }
}

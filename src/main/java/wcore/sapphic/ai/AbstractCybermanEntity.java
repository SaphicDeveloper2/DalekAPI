//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package wcore.sapphic.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

// NOTE: Create this class for your Cyberman projectile
// import net.teabs.teabsdoctorwhomod.entity.projectile.CybermanLaserProjectileEntity;

public abstract class AbstractCybermanEntity extends Monster implements RangedAttackMob {

    private static final Logger LOGGER = LogManager.getLogger();

    public AbstractCybermanEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.setMaxUpStep(1.5F);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        // --- GOAL SELECTOR (Actions) ---
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false) {
            @Override
            protected double getAttackReachSqr(LivingEntity entity) {
                return (this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth());
            }
        });

        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0D, 20, 15.0F));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(9, new FloatGoal(this));


        // --- TARGET SELECTOR (Who to Attack) ---
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true, true));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Villager.class, true, true));

        addTargetGoalByClassName(3, "net.teabs.teabsdoctorwhomod.entity.TimeWarDalekEntity");
        addTargetGoalByClassName(4, "net.teabs.teabsdoctorwhomod.entity.TimeWarSupremeDalekEntity");
        addTargetGoalByClassName(6, "net.teabs.teabsdoctorwhomod.entity.TimeLordEntity");

        this.targetSelector.addGoal(14, new HurtByTargetGoal(this).setAlertOthers());
    }

    @Override
    public void performRangedAttack(@NotNull LivingEntity target, float distanceFactor) {
        // NOTE: You need to implement your own projectile and sound events.
        // Example:
        // this.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("your_mod:cyber_gun_shoot")), 1.0F, 1.0F);
        // CybermanLaserProjectileEntity.shoot(this, target);
    }

    // --- SOUNDS ---
    // The sounds from your CybusCyberman are now the default for all Cybermen.
    // Any specific Cyberman class can override these methods to provide unique sounds.
    // For example, in a 'MondasianCybermanEntity' class, you could override getAmbientSound()
    // to return a different, creepier sound.

    @Override
    protected SoundEvent getAmbientSound() {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("teabs_doctor_who_mod:cyberman_deleted"));
    }

    @Override
    public void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("teabs_doctor_who_mod:cyberman_stomp")), 0.15F, 1.0F);
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.iron_golem.hurt"));
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.iron_golem.hurt"));
    }

    /**
     * Helper method to add attack goals for entities specified by their class name.
     */
    private void addTargetGoalByClassName(int priority, String className) {
        try {
            Class<? extends LivingEntity> targetClass = (Class<? extends LivingEntity>) Class.forName(className);
            this.targetSelector.addGoal(priority, new NearestAttackableTargetGoal<>(this, targetClass, true, true));
        } catch (ClassNotFoundException e) {
            LOGGER.warn("Could not find class {} to add as a Cyberman target.", className);
        } catch (ClassCastException e) {
            LOGGER.error("Class {} is not a valid LivingEntity and cannot be a Cyberman target.", className, e);
        }
    }

    // --- ATTRIBUTES ---
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D);
    }
}
package wcore.sapphic.ai;

// Import necessary classes
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// We now import the projectile entity class directly to use its 'shoot' method.
// NOTE: The package path below is assumed. You may need to adjust it if it's different in the mod's files.
import net.teabs.teabsdoctorwhomod.entity.DalekGunstickProjectileEntity;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractDalekEntity extends Monster implements RangedAttackMob {

    private static final Logger LOGGER = LogManager.getLogger();

    public AbstractDalekEntity(EntityType<? extends Monster> type, Level level) {
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

        this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.25D, 5, 10.0F) {
            @Override
            public boolean canContinueToUse() {
                return this.canUse();
            }
        });

        this.goalSelector.addGoal(15, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(16, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(17, new FloatGoal(this));


        // --- TARGET SELECTOR (Who to Attack) ---
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true, true));
        this.targetSelector.addGoal(9, new NearestAttackableTargetGoal<>(this, Villager.class, true, true));

        // Target all custom entities from the Doctor Who mod using reflection
        addTargetGoalByClassName(3, "net.teabs.teabsdoctorwhomod.entity.TimeLordEntity");
        addTargetGoalByClassName(4, "net.teabs.teabsdoctorwhomod.entity.TimeLadyEntity");
        addTargetGoalByClassName(5, "net.teabs.teabsdoctorwhomod.entity.TimeLordGuardEntity");
        addTargetGoalByClassName(6, "net.teabs.teabsdoctorwhomod.entity.ShaboganFemaleEntity");
        addTargetGoalByClassName(7, "net.teabs.teabsdoctorwhomod.entity.ShaboganMaleEntity");
        addTargetGoalByClassName(8, "net.teabs.teabsdoctorwhomod.entity.CybusCybermanEntity");
        addTargetGoalByClassName(10, "net.teabs.teabsdoctorwhomod.entity.ThalFemaleEntity");
        addTargetGoalByClassName(11, "net.teabs.teabsdoctorwhomod.entity.ThalMaleEntity");
        addTargetGoalByClassName(12, "net.teabs.teabsdoctorwhomod.entity.KaledOfficerEntity");
        addTargetGoalByClassName(13, "net.teabs.teabsdoctorwhomod.entity.KaledScientistEntity");

        this.targetSelector.addGoal(14, new HurtByTargetGoal(this));
    }

    @Override
    public void performRangedAttack(@NotNull LivingEntity target, float distanceFactor) {
        // Firing the projectile exactly as the original mod does.
        DalekGunstickProjectileEntity.shoot(this, target);
    }

    private void addTargetGoalByClassName(int priority, String className) {
        try {
            Class<? extends LivingEntity> targetClass = (Class<? extends LivingEntity>) Class.forName(className);
            this.targetSelector.addGoal(priority, new NearestAttackableTargetGoal<>(this, targetClass, true, true));
        } catch (ClassNotFoundException e) {
            LOGGER.warn("Could not find class {} to add as a Dalek target. Is Teab's Doctor Who Mod installed correctly?", className);
        } catch (ClassCastException e) {
            LOGGER.error("Class {} is not a valid LivingEntity and cannot be a Dalek target.", className, e);
        }
    }

    // --- ATTRIBUTES ADDED HERE ---
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 80.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D);
    }
}
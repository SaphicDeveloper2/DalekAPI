package wcore.sapphic.ai;

// Minecraft & Forge Imports
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
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
import net.minecraftforge.network.NetworkHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

// We now import the projectile entity class directly to use its 'shoot' method.
// NOTE: The package path below is assumed. You may need to adjust it if it's different in the mod's files.
import net.teabs.teabsdoctorwhomod.entity.DalekGunstickProjectileEntity;

/**
 * An abstract base class for all Dalek entities.
 * This class provides the core AI, attributes, sound handling, and networking
 * required for a Dalek to function correctly in the world.
 *
 * To create a new Dalek, extend this class and provide a custom model in the renderer.
 */
public abstract class AbstractDalekEntity extends Monster implements RangedAttackMob {

    private static final Logger LOGGER = LogManager.getLogger();

    // TODO: Register these SoundEvents in your main mod file and provide the sound files.
    public static final SoundEvent DALEK_AMBIENT = new SoundEvent(new ResourceLocation("teabsdoctorwhomod", "dalek.ambient"));
    public static final SoundEvent DALEK_HURT = new SoundEvent(new ResourceLocation("teabsdoctorwhomod", "dalek.hurt"));
    public static final SoundEvent DALEK_DEATH = new SoundEvent(new ResourceLocation("teabsdoctorwhomod", "dalek.death"));
    public static final SoundEvent DALEK_EXTERMINATE = new SoundEvent(new ResourceLocation("teabsdoctorwhomod", "dalek.exterminate"));

    public AbstractDalekEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        // This makes the Dalek's step height higher, allowing it to move over blocks like stairs.
        this.setMaxUpStep(1.5F);
        // This prevents the Dalek from despawning naturally.
        this.setPersistenceRequired();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        // --- GOAL SELECTOR (Actions) ---
        // These goals determine what the Dalek does. Lower numbers have higher priority.
        this.goalSelector.addGoal(1, new FloatGoal(this)); // Highest priority: float if in water/lava.
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.25D, 20, 15.0F)); // Priority 2: Use ranged attack. 20 ticks between shots.
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2, false)); // Priority 3: Use melee if the target is too close.
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.8)); // Wander around when idle.
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this)); // Look around when idle.

        // --- TARGET SELECTOR (Who to Attack) ---
        // These goals determine which entity the Dalek will target.
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this)); // Highest priority: attack whatever attacked it.
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true, true));
        this.targetSelector.addGoal(8, new NearestAttackableTargetGoal<>(this, Villager.class, true, true));

        // Target all custom entities from the Doctor Who mod using reflection for compatibility.
        addTargetGoalByClassName(3, "net.teabs.teabsdoctorwhomod.entity.TimeLordEntity");
        addTargetGoalByClassName(4, "net.teabs.teabsdoctorwhomod.entity.TimeLadyEntity");
        addTargetGoalByClassName(5, "net.teabs.teabsdoctorwhomod.entity.TimeLordGuardEntity");
        addTargetGoalByClassName(6, "net.teabs.teabsdoctorwhomod.entity.ShaboganFemaleEntity");
        addTargetGoalByClassName(7, "net.teabs.teabsdoctorwhomod.entity.ShaboganMaleEntity");
        addTargetGoalByClassName(8, "net.teabs.teabsdoctorwhomod.entity.CybusCybermanEntity");
        addTargetGoalByClassName(9, "net.teabs.teabsdoctorwhomod.entity.ThalFemaleEntity");
        addTargetGoalByClassName(10, "net.teabs.teabsdoctorwhomod.entity.ThalMaleEntity");
        addTargetGoalByClassName(11, "net.teabs.teabsdoctorwhomod.entity.KaledOfficerEntity");
        addTargetGoalByClassName(12, "net.teabs.teabsdoctorwhomod.entity.KaledScientistEntity");
    }

    /**
     * Defines the base attributes for all Daleks.
     * @return An AttributeSupplier.Builder with default Dalek stats.
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 80.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D);
    }

    /**
     * This is the method called by the RangedAttackGoal. It defines the shooting logic.
     * @param target The entity to shoot at.
     * @param distanceFactor The power of the shot, based on distance.
     */
    @Override
    public void performRangedAttack(@NotNull LivingEntity target, float distanceFactor) {
        // Play the iconic "Exterminate!" sound when firing.
        // this.playSound(DALEK_EXTERMINATE, 1.0F, 1.0F);
        DalekGunstickProjectileEntity.shoot(this, target);
    }

    /**
     * A helper method to dynamically add target goals for entities from other mods
     * without needing a hard dependency.
     * @param priority The AI priority for this goal.
     * @param className The full class name of the entity to target.
     */
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

    // --- SOUNDS ---
    @Override
    protected SoundEvent getAmbientSound() {
        // return DALEK_AMBIENT;
        return SoundEvents.IRON_GOLEM_STEP; // Placeholder
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource damageSourceIn) {
        // return DALEK_HURT;
        return SoundEvents.IRON_GOLEM_HURT; // Placeholder
    }

    @Override
    protected SoundEvent getDeathSound() {
        // return DALEK_DEATH;
        return SoundEvents.IRON_GOLEM_DEATH; // Placeholder
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }


    // --- MOVEMENT & DAMAGE ---
    /**
     * Daleks do not take fall damage.
     */
    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, @NotNull DamageSource source) {
        return false;
    }

    // --- NETWORKING ---
    /**
     * Ensures the entity is spawned correctly on the client when created on the server.
     *
     * @return The entity spawn packet.
     */
    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}

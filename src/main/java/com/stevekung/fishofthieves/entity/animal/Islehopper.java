package com.stevekung.fishofthieves.entity.animal;

import java.util.*;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Maps;
import com.stevekung.fishofthieves.FOTItems;
import com.stevekung.fishofthieves.FOTSoundEvents;
import com.stevekung.fishofthieves.FishOfThieves;
import com.stevekung.fishofthieves.entity.GlowFish;
import com.stevekung.fishofthieves.entity.ThievesFish;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class Islehopper extends AbstractFish implements GlowFish
{
    private static final EntityDataAccessor<Integer> TYPE = SynchedEntityData.defineId(Islehopper.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> TROPHY = SynchedEntityData.defineId(Islehopper.class, EntityDataSerializers.BOOLEAN);
    private static final Map<FishVariant, ResourceLocation> GLOW_BY_TYPE = Util.make(Maps.newHashMap(), map ->
    {
        map.put(Variant.AMETHYST, new ResourceLocation(FishOfThieves.MOD_ID, "textures/entity/islehopper/amethyst_glow.png"));
    });

    public Islehopper(EntityType<? extends Islehopper> entityType, Level level)
    {
        super(entityType, level);
        this.refreshDimensions();
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(TYPE, 0);
        this.entityData.define(TROPHY, false);
    }

    @Override
    public ItemStack getBucketItemStack()
    {
        return new ItemStack(FOTItems.ISLEHOPPER_BUCKET);
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return FOTSoundEvents.ISLEHOPPER_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource)
    {
        return FOTSoundEvents.ISLEHOPPER_HURT;
    }

    @Override
    protected SoundEvent getFlopSound()
    {
        return FOTSoundEvents.ISLEHOPPER_FLOP;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound)
    {
        super.addAdditionalSaveData(compound);
        compound.putInt(VARIANT_TAG, this.getVariant().ordinal());
        compound.putBoolean(TROPHY_TAG, this.isTrophy());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound)
    {
        super.readAdditionalSaveData(compound);
        this.setVariant(Variant.BY_ID[compound.getInt(VARIANT_TAG)]);
        this.setTrophy(compound.getBoolean(TROPHY_TAG));
    }

    @Override
    public void saveToBucketTag(ItemStack itemStack)
    {
        super.saveToBucketTag(itemStack);
        this.saveToBucket(itemStack, this.getVariant().ordinal(), this.getVariant().getName());
    }

    @Override
    public void loadFromBucketTag(CompoundTag compound)
    {
        super.loadFromBucketTag(compound);
        this.loadFromBucket(Variant.BY_ID[compound.getInt(VARIANT_TAG)].ordinal(), compound);
    }

    @Override
    public boolean skipAttackInteraction(Entity entity)
    {
        var multiplier = this.isTrophy() ? 2 : 1;

        if (entity instanceof ServerPlayer serverPlayer && entity.hurt(DamageSource.mobAttack(this), multiplier))
        {
            if (!this.isSilent())
            {
                serverPlayer.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.PUFFER_FISH_STING, 0.0f));
            }
            serverPlayer.addEffect(new MobEffectInstance(MobEffects.POISON, 60 * multiplier, 0), this);
        }
        return false;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag)
    {
        spawnData = super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
        return this.defaultFinalizeSpawn(this, reason, spawnData, dataTag, Variant.getSpawnVariant(this));
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key)
    {
        if (TROPHY.equals(key))
        {
            this.refreshDimensions();
        }
        super.onSyncedDataUpdated(key);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose)
    {
        return this.isTrophy() ? super.getDimensions(pose) : EntityDimensions.fixed(0.3F, 0.2F);
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions size)
    {
        return this.isTrophy() ? 0.29F : 0.15F;
    }

    @Override
    public boolean canGlow()
    {
        return this.getVariant() == Variant.AMETHYST;
    }

    @Override
    public float getGlowBrightness(float ageInTicks)
    {
        return Mth.clamp(1.0F + Mth.cos(ageInTicks * 0.05f), 0.5F, 1.0F);
    }

    @Override
    public Variant getVariant()
    {
        return Variant.BY_ID[this.entityData.get(TYPE)];
    }

    @Override
    public void setVariant(int id)
    {
        this.entityData.set(TYPE, id);
    }

    @Override
    public Map<FishVariant, ResourceLocation> getGlowTextureByType()
    {
        return GLOW_BY_TYPE;
    }

    @Override
    public boolean isTrophy()
    {
        return this.entityData.get(TROPHY);
    }

    @Override
    public void setTrophy(boolean trophy)
    {
        this.entityData.set(TROPHY, trophy);
    }

    public void setVariant(Variant variant)
    {
        this.setVariant(variant.ordinal());
    }

    public enum Variant implements ThievesFish.FishVariant
    {
        STONE,
        MOSS(context ->
        {
            var category = getBiomeCategory(context.level(), context.blockPos());
            return category == Biome.BiomeCategory.JUNGLE || category == Biome.BiomeCategory.SWAMP || getBiomeKeys(context.level(), context.blockPos()) == Biomes.LUSH_CAVES;
        }),
        HONEY(context ->
        {
            var optional = lookForBlock(context.blockPos(), 5, blockPos2 ->
            {
                var blockState = context.level().getBlockState(blockPos2);
                var beehiveOptional = context.level().getBlockEntity(blockPos2, BlockEntityType.BEEHIVE);
                var isBeehive = blockState.is(BlockTags.BEEHIVES);
                return isBeehive && BeehiveBlockEntity.getHoneyLevel(blockState) == 5 && beehiveOptional.isPresent() && !beehiveOptional.get().isEmpty();
            });
            return optional.isPresent();
        }),
        RAVEN(context -> context.blockPos().getY() < 0 && context.level().random.nextInt(100) == 0),
        AMETHYST(context -> lookForGeode(context.blockPos(), 2, 16, blockPos2 -> context.level().getBlockState(blockPos2).is(BlockTags.CRYSTAL_SOUND_BLOCKS)));

        public static final Variant[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(Variant::ordinal)).toArray(Variant[]::new);
        private final ThievesFish.Condition condition;

        Variant(ThievesFish.Condition condition)
        {
            this.condition = condition;
        }

        Variant()
        {
            this(context -> true);
        }

        public String getName()
        {
            return this.name().toLowerCase(Locale.ROOT);
        }

        public static Variant getSpawnVariant(LivingEntity livingEntity)
        {
            var variants = Arrays.stream(BY_ID).filter(variant -> variant.condition.spawn(new ThievesFish.SpawnConditionContext((ServerLevel) livingEntity.level, livingEntity.blockPosition()))).toArray(Variant[]::new);
            return Util.getRandom(variants, livingEntity.getRandom());
        }

        private static ResourceKey<Biome> getBiomeKeys(ServerLevel level, BlockPos blockPos)
        {
            var optional = level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getResourceKey(level.getBiome(blockPos));
            return optional.isPresent() ? optional.get() : Biomes.OCEAN;
        }

        private static Biome.BiomeCategory getBiomeCategory(ServerLevel level, BlockPos blockPos)
        {
            return level.getBiome(blockPos).getBiomeCategory();
        }

        private static Optional<BlockPos> lookForBlock(BlockPos blockPos, int range, Predicate<BlockPos> posFilter)
        {
            return BlockPos.findClosestMatch(blockPos, range, range, posFilter);
        }

        private static boolean lookForGeode(BlockPos blockPos, int range, int maxSize, Predicate<BlockPos> posFilter)
        {
            var size = 0;

            for (var blockPos2 : BlockPos.withinManhattan(blockPos, range, range, range))
            {
                if (!posFilter.test(blockPos2))
                {
                    continue;
                }
                size++;
            }
            return size >= maxSize;
        }
    }
}
package com.stevekung.fishofthieves.entity.animal;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;

import com.google.common.collect.Maps;
import com.stevekung.fishofthieves.FOTItems;
import com.stevekung.fishofthieves.FOTSoundEvents;
import com.stevekung.fishofthieves.FishOfThieves;
import com.stevekung.fishofthieves.entity.AbstractSchoolingThievesFish;
import com.stevekung.fishofthieves.entity.ThievesFish;
import com.stevekung.fishofthieves.utils.TerrainUtils;

import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.StructureFeature;

public class Ancientscale extends AbstractSchoolingThievesFish
{
    private static final Map<FishVariant, ResourceLocation> GLOW_BY_TYPE = Util.make(Maps.newHashMap(), map -> map.put(Variant.STARSHINE, new ResourceLocation(FishOfThieves.MOD_ID, "textures/entity/ancientscale/starshine_glow.png")));

    public Ancientscale(EntityType<? extends Ancientscale> entityType, Level level)
    {
        super(entityType, level);
    }

    @Override
    public ItemStack getBucketItemStack()
    {
        return new ItemStack(FOTItems.ANCIENTSCALE_BUCKET);
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return FOTSoundEvents.ANCIENTSCALE_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource)
    {
        return FOTSoundEvents.ANCIENTSCALE_HURT;
    }

    @Override
    protected SoundEvent getFlopSound()
    {
        return FOTSoundEvents.ANCIENTSCALE_FLOP;
    }

    @Override
    public EntityDimensions getDimensions(Pose pose)
    {
        return this.isTrophy() ? super.getDimensions(pose) : EntityDimensions.fixed(0.3F, 0.25F);
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions size)
    {
        return this.isTrophy() ? 0.3575F : 0.18F;
    }

    @Override
    public boolean canGlow()
    {
        return this.getVariant() == Variant.STARSHINE;
    }

    @Override
    public Variant getVariant()
    {
        return Variant.BY_ID[this.entityData.get(TYPE)];
    }

    @Override
    public FishVariant getVariant(CompoundTag compound)
    {
        return Variant.BY_ID[compound.getInt(VARIANT_TAG)];
    }

    @Override
    public Variant getSpawnVariant()
    {
        return Variant.getSpawnVariant(this);
    }

    @Override
    public Map<FishVariant, ResourceLocation> getGlowTextureByType()
    {
        return GLOW_BY_TYPE;
    }

    public enum Variant implements ThievesFish.FishVariant
    {
        ALMOND,
        SAPPHIRE,
        SMOKE,
        BONE(context ->
        {
            var level = context.level();
            var blockPos = context.blockPos();
            return level.random.nextInt(100) == 0 || level.random.nextInt(10) == 0 && (TerrainUtils.isInFeature(level, blockPos, StructureFeature.MINESHAFT) || TerrainUtils.isInFeature(level, blockPos, StructureFeature.STRONGHOLD));
        }),
        STARSHINE(context -> context.level().getMoonBrightness() <= 0.25F && context.level().isNight() && context.level().canSeeSkyFromBelowWater(context.blockPos()));

        public static final Variant[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(Variant::getId)).toArray(Variant[]::new);
        private final ThievesFish.Condition condition;

        Variant(ThievesFish.Condition condition)
        {
            this.condition = condition;
        }

        Variant()
        {
            this(context -> true);
        }

        @Override
        public String getName()
        {
            return this.name().toLowerCase(Locale.ROOT);
        }

        @Override
        public int getId()
        {
            return this.ordinal();
        }

        public static Variant getSpawnVariant(LivingEntity livingEntity)
        {
            var variants = Arrays.stream(BY_ID).filter(variant -> variant.condition.spawn(new ThievesFish.SpawnConditionContext((ServerLevel) livingEntity.level, livingEntity.blockPosition()))).toArray(Variant[]::new);
            return Util.getRandom(variants, livingEntity.getRandom());
        }
    }
}
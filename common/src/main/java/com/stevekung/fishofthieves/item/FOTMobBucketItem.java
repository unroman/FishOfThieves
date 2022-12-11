package com.stevekung.fishofthieves.item;

import java.util.List;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;
import com.stevekung.fishofthieves.entity.ThievesFish;
import com.stevekung.fishofthieves.registry.FOTTags;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

public class FOTMobBucketItem extends MobBucketItem
{
    private final EntityType<?> entityType;
    private final Consumer<Int2ObjectOpenHashMap<String>> dataFixMap;

    public FOTMobBucketItem(EntityType<?> entityType, Fluid fluid, SoundEvent soundEvent, Consumer<Int2ObjectOpenHashMap<String>> dataFixMap, Item.Properties properties)
    {
        super(entityType, fluid, soundEvent, properties);
        this.entityType = entityType;
        this.dataFixMap = dataFixMap;
    }

    @Override
    public void verifyTagAfterLoad(CompoundTag compoundTag)
    {
        super.verifyTagAfterLoad(compoundTag);
        ThievesFish.fixData(compoundTag, this.dataFixMap);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
    {
        var compoundTag = itemStack.getTag();

        if (this.entityType.is(FOTTags.THIEVES_FISH_ENTITY_TYPE) && itemStack.hasTag() && compoundTag.contains(ThievesFish.VARIANT_TAG, Tag.TAG_STRING))
        {
            var type = Component.translatable("entity.fishofthieves.%s.%s".formatted(BuiltInRegistries.ENTITY_TYPE.getKey(this.entityType).getPath(), ResourceLocation.tryParse(compoundTag.getString(ThievesFish.VARIANT_TAG)).getPath())).withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY);

            if (compoundTag.getBoolean(ThievesFish.TROPHY_TAG))
            {
                type.append(", ").append(Component.translatable("entity.fishofthieves.trophy"));
            }
            tooltipComponents.add(type);
        }
    }
}
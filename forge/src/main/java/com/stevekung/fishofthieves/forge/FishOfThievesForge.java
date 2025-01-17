package com.stevekung.fishofthieves.forge;

import com.mojang.serialization.Codec;
import com.stevekung.fishofthieves.FishOfThieves;
import com.stevekung.fishofthieves.forge.compatibility.Aquaculture2;
import com.stevekung.fishofthieves.forge.datagen.FOTBiomeModifier;
import com.stevekung.fishofthieves.forge.datagen.FOTStructureModifiers;
import com.stevekung.fishofthieves.forge.proxy.ClientProxyForge;
import com.stevekung.fishofthieves.forge.proxy.CommonProxyForge;
import com.stevekung.fishofthieves.registry.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.common.world.StructureModifier;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

@Mod(FishOfThieves.MOD_ID)
public class FishOfThievesForge
{
    public static final DeferredRegister<Block> BLOCK = DeferredRegister.create(ForgeRegistries.BLOCKS, FishOfThieves.MOD_ID);
    public static final DeferredRegister<Item> ITEM = DeferredRegister.create(ForgeRegistries.ITEMS, FishOfThieves.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPE = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, FishOfThieves.MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, FishOfThieves.MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, FishOfThieves.MOD_ID);
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, FishOfThieves.MOD_ID);
    public static final DeferredRegister<Codec<? extends StructureModifier>> STRUCTURE_MODIFIERS = DeferredRegister.create(ForgeRegistries.Keys.STRUCTURE_MODIFIER_SERIALIZERS, FishOfThieves.MOD_ID);

    public static CommonProxyForge PROXY;

    private static final String THIEVES_FISH_SPAWNS_IN_STRUCTURE = "thieves_fish_spawns_in_structure";
    public static final ResourceLocation ADD_THIEVES_FISH_SPAWNS_IN_STRUCTURE_RL = FishOfThieves.res(THIEVES_FISH_SPAWNS_IN_STRUCTURE);
    private static final String ADD_FISH_BONE = "add_fish_bone";
    public static final ResourceLocation ADD_FISH_BONE_RL = FishOfThieves.res(ADD_FISH_BONE);

    public FishOfThievesForge()
    {
        var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.register(this);
        modEventBus.addListener(this::commonSetup);
        BLOCK.register(modEventBus);
        ITEM.register(modEventBus);
        BLOCK_ENTITY_TYPE.register(modEventBus);
        ENTITY.register(modEventBus);
        SOUND_EVENTS.register(modEventBus);
        FEATURES.register(modEventBus);
        STRUCTURE_MODIFIERS.register(modEventBus);

        FishOfThieves.init();

        modEventBus.addListener(FOTBiomeModifier::generateBiomeModifiers);
        STRUCTURE_MODIFIERS.register(THIEVES_FISH_SPAWNS_IN_STRUCTURE, FOTStructureModifiers.Modifier::makeCodec);
        modEventBus.addListener(FOTStructureModifiers::generateStructureModifiers);

        PROXY = DistExecutor.safeRunForDist(() -> ClientProxyForge::new, () -> CommonProxyForge::new);
        PROXY.init();
    }

    private void commonSetup(FMLCommonSetupEvent event)
    {
        FishOfThieves.initCommon();

        if (ModList.get().isLoaded("aquaculture"))
        {
            Aquaculture2.init();
        }
    }

    @SubscribeEvent
    public void onRegister(RegisterEvent event)
    {
        event.register(ForgeRegistries.Keys.BLOCKS, helper -> FOTBlocks.init());
        event.register(ForgeRegistries.Keys.ITEMS, helper -> FOTItems.init());
        event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, helper -> FOTBlockEntityTypes.init());
        event.register(ForgeRegistries.Keys.ENTITY_TYPES, helper -> FOTEntities.init());
        event.register(ForgeRegistries.Keys.BIOMES, helper -> FOTLootItemConditions.init());
        event.register(ForgeRegistries.Keys.FEATURES, helper -> FOTFeatures.init());
    }

    @SubscribeEvent
    public void onCreativeModeTabRegister(CreativeModeTabEvent.Register event)
    {
        event.registerCreativeModeTab(FishOfThieves.res("main"), FishOfThieves::getCreativeTabBuilder);
    }
}
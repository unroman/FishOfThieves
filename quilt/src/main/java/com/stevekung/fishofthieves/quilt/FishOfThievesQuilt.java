package com.stevekung.fishofthieves.quilt;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.item.content.registry.api.ItemContentRegistries;
import org.quiltmc.qsl.villager.api.TradeOfferHelper;
import org.quiltmc.qsl.worldgen.biome.api.BiomeModifications;
import org.quiltmc.qsl.worldgen.biome.api.BiomeSelectors;
import com.stevekung.fishofthieves.FishOfThieves;
import com.stevekung.fishofthieves.entity.animal.*;
import com.stevekung.fishofthieves.loot.FOTLootManager;
import com.stevekung.fishofthieves.registry.*;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;

public class FishOfThievesQuilt implements ModInitializer
{
    @Override
    public void onInitialize(ModContainer mod)
    {
        FishOfThieves.init();
        FOTBlocks.init();
        FOTItems.init();
        FOTBlockEntityTypes.init();
        FOTEntities.init();
        FOTFeatures.init();
        FishOfThieves.initCommon();
        FOTLootItemConditions.init();

        ItemContentRegistries.COMPOST_CHANCE.put(FOTItems.EARTHWORMS, 0.4F);
        ItemContentRegistries.COMPOST_CHANCE.put(FOTItems.GRUBS, 0.4F);
        ItemContentRegistries.COMPOST_CHANCE.put(FOTItems.LEECHES, 0.4F);

        ItemContentRegistries.FUEL_TIME.put(FOTTags.Items.WOODEN_FISH_PLAQUE, 300);

        TradeOfferHelper.registerVillagerOffers(VillagerProfession.FISHERMAN, 1, list -> FishOfThieves.getFishermanTradesByLevel(1, list));
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.FISHERMAN, 2, list -> FishOfThieves.getFishermanTradesByLevel(2, list));
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.FISHERMAN, 3, list -> FishOfThieves.getFishermanTradesByLevel(3, list));
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.FISHERMAN, 4, list -> FishOfThieves.getFishermanTradesByLevel(4, list));
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.FISHERMAN, 5, list -> FishOfThieves.getFishermanTradesByLevel(5, list));

        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) ->
        {
            // Gameplay
            if (id.equals(BuiltInLootTables.FISHERMAN_GIFT))
            {
                tableBuilder.modifyPools(FOTLootManager::getFishermanGiftLoot);
            }
            else if (id.equals(BuiltInLootTables.FISHING_FISH))
            {
                tableBuilder.modifyPools(FOTLootManager::getFishingLoot);
            }
            // Entity Loot
            else if (id.equals(EntityType.POLAR_BEAR.getDefaultLootTable()))
            {
                tableBuilder.modifyPools(FOTLootManager::getPolarBearLoot);
            }
            // Chests
            else if (id.equals(BuiltInLootTables.VILLAGE_FISHER))
            {
                tableBuilder.withPool(FOTLootManager.getVillageFisherLoot(LootPool.lootPool()));
            }
            else if (id.equals(BuiltInLootTables.BURIED_TREASURE))
            {
                tableBuilder.withPool(FOTLootManager.getBuriedTreasureLoot(LootPool.lootPool()));
            }
        });

        DefaultAttributes.SUPPLIERS.put(FOTEntities.SPLASHTAIL, AbstractFish.createAttributes().build());
        DefaultAttributes.SUPPLIERS.put(FOTEntities.PONDIE, AbstractFish.createAttributes().build());
        DefaultAttributes.SUPPLIERS.put(FOTEntities.ISLEHOPPER, AbstractFish.createAttributes().build());
        DefaultAttributes.SUPPLIERS.put(FOTEntities.ANCIENTSCALE, AbstractFish.createAttributes().build());
        DefaultAttributes.SUPPLIERS.put(FOTEntities.PLENTIFIN, AbstractFish.createAttributes().build());
        DefaultAttributes.SUPPLIERS.put(FOTEntities.WILDSPLASH, AbstractFish.createAttributes().build());
        DefaultAttributes.SUPPLIERS.put(FOTEntities.DEVILFISH, Devilfish.createAttributes().build());
        DefaultAttributes.SUPPLIERS.put(FOTEntities.BATTLEGILL, Battlegill.createAttributes().build());
        DefaultAttributes.SUPPLIERS.put(FOTEntities.WRECKER, Wrecker.createAttributes().build());
        DefaultAttributes.SUPPLIERS.put(FOTEntities.STORMFISH, AbstractFish.createAttributes().build());

        SpawnPlacements.register(FOTEntities.SPLASHTAIL, SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules);
        SpawnPlacements.register(FOTEntities.PONDIE, SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules);
        SpawnPlacements.register(FOTEntities.ISLEHOPPER, SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Islehopper::checkSpawnRules);
        SpawnPlacements.register(FOTEntities.ANCIENTSCALE, SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Ancientscale::checkSpawnRules);
        SpawnPlacements.register(FOTEntities.PLENTIFIN, SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Plentifin::checkSpawnRules);
        SpawnPlacements.register(FOTEntities.WILDSPLASH, SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Wildsplash::checkSpawnRules);
        SpawnPlacements.register(FOTEntities.DEVILFISH, SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Devilfish::checkSpawnRules);
        SpawnPlacements.register(FOTEntities.BATTLEGILL, SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Battlegill::checkSpawnRules);
        SpawnPlacements.register(FOTEntities.WRECKER, SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Wrecker::checkSpawnRules);
        SpawnPlacements.register(FOTEntities.STORMFISH, SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Stormfish::checkSpawnRules);

        BiomeModifications.addFeature(BiomeSelectors.isIn(FOTTags.Biomes.HAS_FISH_BONE), GenerationStep.Decoration.VEGETAL_DECORATION, FOTPlacements.FISH_BONE_KEY);

        BiomeModifications.addSpawn(BiomeSelectors.isIn(FOTTags.Biomes.SPAWNS_SPLASHTAILS), FOTEntities.SPLASHTAIL.getCategory(), FOTEntities.SPLASHTAIL, FishOfThieves.CONFIG.spawnRate.fishWeight.splashtail, 4, 8);
        BiomeModifications.addSpawn(BiomeSelectors.isIn(FOTTags.Biomes.SPAWNS_PONDIES), FOTEntities.PONDIE.getCategory(), FOTEntities.PONDIE, FishOfThieves.CONFIG.spawnRate.fishWeight.pondie, 2, 4);
        BiomeModifications.addSpawn(BiomeSelectors.isIn(FOTTags.Biomes.SPAWNS_ISLEHOPPERS), FOTEntities.ISLEHOPPER.getCategory(), FOTEntities.ISLEHOPPER, FishOfThieves.CONFIG.spawnRate.fishWeight.islehopper, 2, 4);
        BiomeModifications.addSpawn(BiomeSelectors.isIn(FOTTags.Biomes.SPAWNS_ANCIENTSCALES), FOTEntities.ANCIENTSCALE.getCategory(), FOTEntities.ANCIENTSCALE, FishOfThieves.CONFIG.spawnRate.fishWeight.ancientscale, 4, 8);
        BiomeModifications.addSpawn(BiomeSelectors.isIn(FOTTags.Biomes.SPAWNS_PLENTIFINS), FOTEntities.PLENTIFIN.getCategory(), FOTEntities.PLENTIFIN, FishOfThieves.CONFIG.spawnRate.fishWeight.plentifin, 4, 8);
        BiomeModifications.addSpawn(BiomeSelectors.isIn(FOTTags.Biomes.SPAWNS_WILDSPLASH), FOTEntities.WILDSPLASH.getCategory(), FOTEntities.WILDSPLASH, FishOfThieves.CONFIG.spawnRate.fishWeight.wildsplash, 2, 4);
        BiomeModifications.addSpawn(BiomeSelectors.isIn(FOTTags.Biomes.SPAWNS_DEVILFISH), FOTEntities.DEVILFISH.getCategory(), FOTEntities.DEVILFISH, FishOfThieves.CONFIG.spawnRate.fishWeight.devilfish, 1, 2);
        BiomeModifications.addSpawn(BiomeSelectors.isIn(FOTTags.Biomes.SPAWNS_BATTLEGILLS), FOTEntities.BATTLEGILL.getCategory(), FOTEntities.BATTLEGILL, FishOfThieves.CONFIG.spawnRate.fishWeight.battlegill, 2, 4);
        BiomeModifications.addSpawn(BiomeSelectors.isIn(FOTTags.Biomes.SPAWNS_WRECKERS), FOTEntities.WRECKER.getCategory(), FOTEntities.WRECKER, FishOfThieves.CONFIG.spawnRate.fishWeight.wrecker, 4, 8);
        BiomeModifications.addSpawn(BiomeSelectors.isIn(FOTTags.Biomes.SPAWNS_STORMFISH), FOTEntities.STORMFISH.getCategory(), FOTEntities.STORMFISH, FishOfThieves.CONFIG.spawnRate.fishWeight.stormfish, 4, 8);
    }
}
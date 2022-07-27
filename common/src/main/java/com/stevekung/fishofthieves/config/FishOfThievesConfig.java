package com.stevekung.fishofthieves.config;

import com.stevekung.fishofthieves.core.FishOfThieves;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = FishOfThieves.MOD_ID)
@Config.Gui.Background("minecraft:textures/block/prismarine_bricks.png")
public final class FishOfThievesConfig implements ConfigData
{
    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.TransitiveObject
    public General general;

    @ConfigEntry.Category("drop_rate")
    @ConfigEntry.Gui.TransitiveObject
    public DropRate dropRate;

    @ConfigEntry.Category("spawn_rate")
    @ConfigEntry.Gui.TransitiveObject
    public SpawnRate spawnRate;

    public FishOfThievesConfig()
    {
        this.general = new General();
        this.dropRate = new DropRate();
        this.spawnRate = new SpawnRate();
    }

    public static class General
    {
        public float trophyMaxHealth = 5.0F;

        @ConfigEntry.Gui.RequiresRestart
        @Comment("Each fish and each variant has special spawning conditions requirement.\nIf enabled, special conditions won't be applied and set to always spawn.\nIncluding rare variants and night variants as well.\nNote: Night Time or Probability still work")
        public boolean simpleSpawningCondition;
    }

    public static class DropRate
    {
        public float earthwormsDropRate = 0.05F;
        public float grubsDropRate = 0.05F;
        public float leechesDropRate = 0.05F;
    }

    public static class SpawnRate
    {
        @Comment("Chance to spawn Trophy fish size.")
        public float trophyProbability = 0.15F;
        @Comment("Chance to spawn Umber variant of Splashtails.")
        public float umberSplashtailProbability = 0.1F;
        @Comment("Chance to spawn Bright variant of Pondies.")
        public float brightPondieProbability = 0.1F;
        @Comment("Chance to spawn Raven variant of Islehoppers.")
        public float ravenIslehopperProbability = 0.1F;
        @Comment("Chance to spawn Bone variant of Ancientscales.")
        public float boneAncientscaleProbability = 0.1F;
        @Comment("Chance to spawn Bonedust variant of Plentifins.")
        public float bonedustPlentifinProbability = 0.1F;
        @Comment("Chance to spawn Muddy variant of Wildsplashes.")
        public float muddyWildsplashProbability = 0.1F;
        @Comment("Chance to spawn Forsaken variant of Devilfish.")
        public float forsakenDevilfishProbability = 0.1F;
        @Comment("Chance to spawn Sand variant of Battlegills.")
        public float sandBattlegillProbability = 0.1F;
        @Comment("Chance to spawn Snow variant of Wreckers.")
        public float snowWreckerProbability = 0.1F;
        @Comment("Chance to spawn Blackcloud variant of Wreckers.")
        public float blackcloudWreckerProbability = 0.1F;
        @Comment("Chance to spawn Shadow variant of Stormfish.")
        public float shadowStormfishProbability = 0.1F;
    }
}
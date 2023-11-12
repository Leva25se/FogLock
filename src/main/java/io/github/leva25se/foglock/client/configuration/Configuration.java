package io.github.leva25se.foglock.client.configuration;

import io.github.leva25se.foglock.client.EffectApply;
import io.github.leva25se.foglock.client.FloatType;
import io.github.leva25se.foglock.client.FogType;
import io.github.leva25se.foglock.client.setting.FogSetting;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

import java.util.HashMap;

public class Configuration {
    private boolean biomeTags;
    private boolean worldAndBiome;
    private HashMap <Identifier, FogConfiguration> configuration1;
    private HashMap <FogType, HashMap <FloatType, FogSetting>> default1;
    private final EffectApply effectApply = new EffectApply();
    public HashMap <Identifier, FogConfiguration> getConfiguration1() {
        return configuration1;
    }

    public EffectApply getEffectApply() {
        return effectApply;
    }

    public HashMap <FogType, HashMap <FloatType, FogSetting>> getDefault1() {
        return default1;
    }

    public void set(HashMap<Identifier, FogConfiguration> configuration1, HashMap <FogType, HashMap <FloatType, FogSetting>> default1, boolean worldAndBiome, boolean biomeTags) {
        this.configuration1 = configuration1;
        this.default1 = default1;
        this.worldAndBiome = worldAndBiome;
        this.biomeTags = biomeTags;
    }

    private RegistryEntry<Biome> lastBiome;

    private HashMap <FogType, HashMap <FloatType, FogSetting>> fogSettingHashMap;

    public RegistryEntry<Biome> getLastBiome() {
        return lastBiome;
    }

    public void setLastBiome(RegistryEntry<Biome> lastBiome) {
        this.lastBiome = lastBiome;
    }

    public void setFogSettingHashMap(HashMap <FogType, HashMap <FloatType, FogSetting>> fogSettingHashMap) {
        this.fogSettingHashMap = fogSettingHashMap;
    }

    public HashMap <FogType, HashMap <FloatType, FogSetting>> getFogSettingHashMap() {
        return fogSettingHashMap;
    }

    public boolean isBiomeTags() {
        return biomeTags;
    }

    public boolean isWorldAndBiome() {
        return worldAndBiome;
    }
}
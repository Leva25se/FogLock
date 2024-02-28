package io.github.leva25se.foglock.client.configuration;

import io.github.leva25se.foglock.client.EffectApply;
import io.github.leva25se.foglock.client.FloatType;
import io.github.leva25se.foglock.client.FogType;
import io.github.leva25se.foglock.client.setting.FogSetting;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

import java.util.HashMap;

public class Configuration {
    private final HashMap<FloatType, Float> requireValue = new HashMap<>();
    private final HashMap<FloatType, Float> nowValue = new HashMap<>();
    private final HashMap<FloatType, Float> deltaValue = new HashMap<>();
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

    public float getValue(FloatType floatType, HashMap<FloatType, FogSetting> map, Camera camera, float vieDistance, boolean thickFog){
        FogSetting setting = map.get(floatType);
        float requireNow = setting.get(camera, vieDistance, thickFog);
        if (requireValue.containsKey(floatType)) {
                float now = nowValue.get(floatType);
                if (requireValue.get(floatType) == requireNow) {
                    if (now == requireNow){
                        return requireNow;
                    } else {
                        if (Math.abs(now - requireNow) <= 0.005f){
                            nowValue.put(floatType, requireNow);
                        } else {
                            now += deltaValue.get(floatType);
                            nowValue.put(floatType, now);
                        }
                        return now;
                    }
                } else {
                    requireValue.put(floatType, requireNow);
                    float delta = (requireNow -  now) / setting.getTime();
                    deltaValue.put(floatType, delta);
                    return now;
                }
        } else {
            requireValue.put(floatType, requireNow);
            nowValue.put(floatType, requireNow);
            deltaValue.put(floatType, 0.0f);
            return requireNow;
        }
    }

    public FogType getType(Camera ca, CameraSubmersionType ct) {
        switch (ct) {
            case LAVA -> {
                Entity entity = ca.getFocusedEntity();
                if (entity instanceof LivingEntity && ((LivingEntity) entity).hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
                    return FogType.LAVA_FIRE_RESISTANCE;
                } else {
                    return FogType.LAVA;
                }
            }
            case WATER -> {
                return FogType.WATER;
            }
            case POWDER_SNOW -> {
                return FogType.POWDER_SNOW;
            }
            case NONE -> {
                return FogType.NONE;
            }
            default -> {
                return FogType.UNDEFINED;
            }
        }
    }
}
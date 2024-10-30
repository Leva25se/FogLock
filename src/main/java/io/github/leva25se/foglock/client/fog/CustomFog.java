package io.github.leva25se.foglock.client.fog;

import io.github.leva25se.foglock.client.configuration.FogConfiguration;
import io.github.leva25se.foglock.client.setting.FogSetting;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Fog;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

import java.util.HashMap;
import java.util.List;

public class CustomFog {
    private final boolean biomeTags;
    private final boolean worldAndBiome;
    private final HashMap<Identifier, FogConfiguration> configuration;
    private final HashMap<FogType, HashMap<FloatType, FogSetting>> default1;
    private final ValueHelper valueHelper;
    private final List<String> potions;
    private HashMap<FogType, HashMap<FloatType, FogSetting>> fogSettingHashMap = new HashMap<>();
    private RegistryEntry<Biome> lastBiome = null;


    public CustomFog(boolean biomeTags, boolean worldAndBiome, HashMap<Identifier, FogConfiguration> configuration, HashMap<FogType, HashMap<FloatType, FogSetting>> default1, List<String> potions, long potionApplyTime) {
        this.biomeTags = biomeTags;
        this.worldAndBiome = worldAndBiome;
        this.configuration = configuration;
        this.default1 = default1;
        this.potions = potions;
        this.valueHelper = new ValueHelper(potionApplyTime);
    }


    public Fog setFog(Camera camera, Fog fog, float viewDistance, boolean thickenFog) {
        if (camera.getFocusedEntity() instanceof LivingEntity livingEntity) {
            return applyCustom(livingEntity, camera, fog, checkEffect(livingEntity), viewDistance, thickenFog);
        }
        return null;
    }

    private Fog applyCustom(LivingEntity entity, Camera camera, Fog fog, boolean potion,  float viewDistance, boolean thickenFog) {
        CameraSubmersionType cST = camera.getSubmersionType();
        FogType fogType = valueHelper.getType(camera, cST);


        HashMap<FogType, HashMap<FloatType, FogSetting>> identifiersFog = new HashMap<>();

        boolean set = true;
        if (worldAndBiome || biomeTags) {
            RegistryEntry<Biome> biome = entity.getWorld().getBiome(entity.getBlockPos());
            if (lastBiome != null && lastBiome.equals(biome)) {
                identifiersFog = fogSettingHashMap;
                set = false;
            } else {
                HashMap<Identifier, FogConfiguration> identifiersFog1 = configuration;
                int priority = 0;
                if (biomeTags) {
                    List<TagKey<Biome>> tagKeyList = biome.streamTags().toList();
                    for (TagKey<Biome> value : tagKeyList) {
                        Identifier identifier = value.id();
                        if (identifiersFog1.containsKey(identifier)) {
                            int p = identifiersFog1.get(identifier).priority();
                            if (p > priority) {
                                priority = p;
                                identifiersFog = identifiersFog1.get(identifier).fogSetting();
                            }
                        }
                    }
                }
                if (worldAndBiome) {
                    Identifier[] identifiers = new Identifier[2];
                    identifiers[0] = biome.getKey().orElseThrow().getValue();
                    identifiers[1] = entity.getWorld().getRegistryKey().getValue();
                    for (Identifier identifier1 : identifiers) {
                        if (identifiersFog1.containsKey(identifier1)) {
                            int p = identifiersFog1.get(identifier1).priority();
                            if (p > priority) {
                                priority = p;
                                identifiersFog = identifiersFog1.get(identifier1).fogSetting();
                            }
                        }
                    }
                }
            }
            lastBiome = biome;
        } else {
            if (lastBiome == null) {
                set = false;
                identifiersFog = default1;
            }
            lastBiome = null;
        }
        if (set) {
            if (identifiersFog.isEmpty()) {
                identifiersFog = default1;
            }
            fogSettingHashMap = identifiersFog;
        }
        HashMap<FloatType, FogSetting> fogSetting = identifiersFog.get(fogType);
        if (fogSetting == null) {
            return null;
        }

        float start = valueHelper.getValue(FloatType.START, fogSetting, camera, viewDistance, thickenFog, fog.start(), potion);
        float end = valueHelper.getValue(FloatType.END, fogSetting, camera, viewDistance, thickenFog, fog.end(), potion);
        float red = valueHelper.getValue(FloatType.R, fogSetting, camera, viewDistance, thickenFog, fog.red(), potion);
        float green = valueHelper.getValue(FloatType.G, fogSetting, camera, viewDistance, thickenFog, fog.green(), potion);
        float blue = valueHelper.getValue(FloatType.B, fogSetting, camera, viewDistance, thickenFog, fog.blue(), potion);
        float alpha = valueHelper.getValue(FloatType.ALPHA, fogSetting, camera, viewDistance, thickenFog, fog.alpha(), potion);
        return new Fog(start, end, fog.shape(), red, green, blue, alpha);
    }

    private boolean checkEffect(LivingEntity livingEntity) {
        for (StatusEffectInstance effect : livingEntity.getStatusEffects()) {
            if (potions.contains(effect.getEffectType().getIdAsString())) {
                return true;
            }
        }
        return false;
    }
}

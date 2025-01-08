package io.github.leva25se.foglock.client.fog;

import io.github.leva25se.foglock.client.configuration.FogConfiguration;
import io.github.leva25se.foglock.client.setting.FogSetting;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogParameters;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class CustomFog {
    private final boolean biomeTags;
    private final boolean worldAndBiome;
    private final HashMap<ResourceLocation, FogConfiguration> configuration;
    private final HashMap<FogType, HashMap<FloatType, FogSetting>> default1;
    private final ValueHelper valueHelper;
    private final List<String> potions;
    private HashMap<FogType, HashMap<FloatType, FogSetting>> fogSettingHashMap = new HashMap<>();
    private Holder<Biome> lastBiome = null;


    public CustomFog(boolean biomeTags, boolean worldAndBiome, HashMap<ResourceLocation, FogConfiguration> configuration, HashMap<FogType, HashMap<FloatType, FogSetting>> default1, List<String> potions, long potionApplyTime) {
        this.biomeTags = biomeTags;
        this.worldAndBiome = worldAndBiome;
        this.configuration = configuration;
        this.default1 = default1;
        this.potions = potions;
        this.valueHelper = new ValueHelper(potionApplyTime);
    }


    public FogParameters setFog(Camera camera, FogParameters fog, float viewDistance, boolean thickenFog) {
        if (camera.getEntity() instanceof LivingEntity livingEntity) {
            return applyCustom(livingEntity, camera, fog, checkEffect(livingEntity), viewDistance, thickenFog);
        }
        return null;
    }

    private FogParameters applyCustom(LivingEntity entity, Camera camera, FogParameters fog, boolean potion,  float viewDistance, boolean thickenFog) {
        net.minecraft.world.level.material.FogType cST = camera.getFluidInCamera();
        FogType fogType = valueHelper.getType(camera, cST);


        HashMap<FogType, HashMap<FloatType, FogSetting>> identifiersFog = new HashMap<>();

        boolean set = true;
        if (worldAndBiome || biomeTags) {
            Holder<Biome> biome;
            try (Level level = entity.level()) {
                biome = level.getBiome(entity.blockPosition());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (lastBiome != null && lastBiome.equals(biome)) {
                identifiersFog = fogSettingHashMap;
                set = false;
            } else {
                HashMap<ResourceLocation, FogConfiguration> identifiersFog1 = configuration;
                int priority = 0;
                if (biomeTags) {
                    List<TagKey<Biome>> tagKeyList = biome.tags().toList();
                    for (TagKey<Biome> value : tagKeyList) {
                        ResourceLocation identifier = value.location();
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
                    ResourceLocation[] identifiers = new ResourceLocation[2];
                    identifiers[0] = biome.unwrapKey().orElseThrow().location();
                    try (Level level = entity.level()) {
                        identifiers[1] = level.dimension().location();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    for (ResourceLocation identifier1 : identifiers) {
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
        return new FogParameters(start, end, fog.shape(), red, green, blue, alpha);
    }

    private boolean checkEffect(LivingEntity livingEntity) {
        for (MobEffectInstance effect : livingEntity.getActiveEffects()) {
            if (potions.contains(effect.getEffect().getRegisteredName())) {
                return true;
            }
        }
        return false;
    }
}

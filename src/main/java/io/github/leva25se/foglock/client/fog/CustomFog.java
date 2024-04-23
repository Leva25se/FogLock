package io.github.leva25se.foglock.client.fog;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.leva25se.foglock.client.EffectApply;
import io.github.leva25se.foglock.client.configuration.FogConfiguration;
import io.github.leva25se.foglock.client.setting.FogSetting;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

import java.util.HashMap;
import java.util.List;

public class CustomFog {
    private final boolean biomeTags;
    private final boolean worldAndBiome;
    private final HashMap <Identifier, FogConfiguration> configuration;
    private final HashMap <FogType, HashMap <FloatType, FogSetting>> default1;
    private final EffectApply effectApply = new EffectApply();
    private final boolean potionPriority;
    private final ValueHelper valueHelper = new ValueHelper();
    private HashMap <FogType, HashMap <FloatType, FogSetting>> fogSettingHashMap = new HashMap<>();
    private RegistryEntry<Biome> lastBiome = null;


    public CustomFog(boolean biomeTags, boolean worldAndBiome, HashMap<Identifier, FogConfiguration> configuration, HashMap<FogType, HashMap<FloatType, FogSetting>> default1, boolean potionPriority) {
        this.biomeTags = biomeTags;
        this.worldAndBiome = worldAndBiome;
        this.configuration = configuration;
        this.default1 = default1;
        this.potionPriority = potionPriority;
    }


    public void setFog(Camera camera, BackgroundRenderer.FogType backgroundFogType, float viewDistance, boolean thickFog, float tickDelta) {
        if ( camera.getFocusedEntity() instanceof LivingEntity livingEntity) {
            if (potionPriority){
                if (!checkEffect(false, livingEntity, backgroundFogType, viewDistance, tickDelta)){
                    applyCustom(livingEntity, camera, viewDistance, thickFog);
                }
            } else {
                applyCustom(livingEntity, camera, viewDistance, thickFog);
                checkEffect(true, livingEntity, backgroundFogType, viewDistance, tickDelta);
            }
        }
    }

    private void applyCustom(LivingEntity entity, Camera camera, float viewDistance, boolean thickFog) {
        CameraSubmersionType cST = camera.getSubmersionType();
        FogType fogType = valueHelper.getType(camera, cST);


        HashMap <FogType, HashMap <FloatType, FogSetting>> identifiersFog = new HashMap <>();

        boolean set = true;
        if (worldAndBiome || biomeTags) {
            RegistryEntry<Biome> biome = entity.getWorld().getBiome(entity.getBlockPos());
            if (lastBiome != null && lastBiome.equals(biome)){
                identifiersFog = fogSettingHashMap;
                set = false;
            } else {
                HashMap <Identifier, FogConfiguration> identifiersFog1 = configuration;
                int priority = 0;
                if (biomeTags) {
                    List<TagKey<Biome>> tagKeyList = biome.streamTags().toList();
                    for (TagKey <Biome> value: tagKeyList) {
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
                if (worldAndBiome){
                    Identifier[] identifiers = new Identifier[2];
                    identifiers[0] = biome.getKey().orElseThrow().getValue();
                    identifiers[1] = entity.getWorld().getRegistryKey().getValue();
                    for (Identifier identifier1: identifiers) {
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
            if (lastBiome == null){
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
        HashMap <FloatType, FogSetting> fogSetting = identifiersFog.get(fogType);
        if (fogSetting == null) {
            return;
        }


        float[] color = RenderSystem.getShaderFogColor();
        if (fogSetting.containsKey(FloatType.START)) {
            RenderSystem.setShaderFogStart(valueHelper.getValue(FloatType.START, fogSetting, camera, viewDistance, thickFog));
        }
        if (fogSetting.containsKey(FloatType.END)) {
            RenderSystem.setShaderFogEnd(valueHelper.getValue(FloatType.END, fogSetting, camera, viewDistance, thickFog));
        }
        if (fogSetting.containsKey(FloatType.R)) {
            color[0] = valueHelper.getValue(FloatType.R, fogSetting, camera, viewDistance, thickFog);
        }
        if (fogSetting.containsKey(FloatType.G)) {
            color[1] = valueHelper.getValue(FloatType.G, fogSetting, camera, viewDistance, thickFog);
        }
        if (fogSetting.containsKey(FloatType.B)) {
            color[2] = valueHelper.getValue(FloatType.B, fogSetting, camera, viewDistance, thickFog);
        }
        if (fogSetting.containsKey(FloatType.ALPHA)) {
            color[3] = valueHelper.getValue(FloatType.ALPHA, fogSetting, camera, viewDistance, thickFog);
        }
    }

    private boolean checkEffect(boolean apply, LivingEntity livingEntity, BackgroundRenderer.FogType fogType, float viewDistance, float tickDelta){
        StatusEffectInstance darknessStatusEffect = livingEntity.getStatusEffect(StatusEffects.DARKNESS);
        if (darknessStatusEffect != null){
            if (apply){
                effectApply.applyDark(fogType, livingEntity, darknessStatusEffect, viewDistance, tickDelta);
            } else {
                return true;
            }
        }
        StatusEffectInstance blindnessStatusEffect = livingEntity.getStatusEffect(StatusEffects.BLINDNESS);
        if (blindnessStatusEffect != null) {
            if (apply){
                effectApply.applyBlindness(fogType, blindnessStatusEffect, viewDistance);
            } else {
                return true;
            }
        }
        return false;
    }
}

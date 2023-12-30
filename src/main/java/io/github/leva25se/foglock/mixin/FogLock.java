package io.github.leva25se.foglock.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.leva25se.foglock.client.FloatType;
import io.github.leva25se.foglock.client.FogLockClient;
import io.github.leva25se.foglock.client.FogType;
import io.github.leva25se.foglock.client.configuration.Configuration;
import io.github.leva25se.foglock.client.configuration.FogConfiguration;
import io.github.leva25se.foglock.client.setting.FogSetting;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.List;

@Mixin(value = BackgroundRenderer.class, priority = 1002)
public class FogLock {
    @Inject(at = @At("TAIL"), method = "applyFog")
    private static void setFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci) {

        Configuration c = FogLockClient.getConfiguration();
        CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
        FogType fogType1 = toType(camera, cameraSubmersionType);
        HashMap <FogType, HashMap <FloatType, FogSetting>> identifiersFog = new HashMap <> ();
        boolean set = true;
        if (c.isWorldAndBiome() || c.isBiomeTags()) {
            Entity entity = camera.getFocusedEntity();
            RegistryEntry<Biome> biome = entity.getWorld().getBiome(entity.getBlockPos());
            if (c.getLastBiome() != null && c.getLastBiome().equals(biome)){
                identifiersFog = c.getFogSettingHashMap();
                set = false;
            } else {
                HashMap <Identifier, FogConfiguration> identifiersFog1 = c.getConfiguration1();
                int priority = 0;
                if (c.isBiomeTags()) {
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
                if (c.isWorldAndBiome()){
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
            c.setLastBiome(biome);
        } else {
            if (c.getLastBiome() == null){
                set = false;
                identifiersFog = c.getDefault1();
            }
            c.setLastBiome(null);
        }

        if (set && identifiersFog.isEmpty()) {
            identifiersFog = c.getDefault1();
            c.setFogSettingHashMap(identifiersFog);
        } else if (set){
            c.setFogSettingHashMap(identifiersFog);
        }


        HashMap <FloatType, FogSetting> fogSetting = identifiersFog.get(fogType1);
        if (fogSetting == null) {
            return;
        }

        float[] color = RenderSystem.getShaderFogColor();
        if (fogSetting.containsKey(FloatType.START)) {
            RenderSystem.setShaderFogStart(c.getValue(FloatType.START, fogSetting, camera, viewDistance, thickFog));
        }
        if (fogSetting.containsKey(FloatType.END)) {
            RenderSystem.setShaderFogEnd(c.getValue(FloatType.END, fogSetting, camera, viewDistance, thickFog));
        }
        if (fogSetting.containsKey(FloatType.R)) {
            color[0] = c.getValue(FloatType.R, fogSetting, camera, viewDistance, thickFog);
        }
        if (fogSetting.containsKey(FloatType.G)) {
            color[1] = c.getValue(FloatType.G, fogSetting, camera, viewDistance, thickFog);
        }
        if (fogSetting.containsKey(FloatType.B)) {
            color[2] = c.getValue(FloatType.B, fogSetting, camera, viewDistance, thickFog);
        }
        if (fogSetting.containsKey(FloatType.ALPHA)) {
            color[3] = c.getValue(FloatType.ALPHA, fogSetting, camera, viewDistance, thickFog);
        }
        Entity entity = camera.getFocusedEntity();
        if (entity instanceof LivingEntity livingEntity) {
            if (livingEntity.hasStatusEffect(StatusEffects.DARKNESS)) {
                StatusEffectInstance statusEffectInstance = livingEntity.getStatusEffect(StatusEffects.DARKNESS);
                c.getEffectApply().applyDark(fogType, livingEntity, statusEffectInstance, viewDistance, tickDelta);

            }
            if (livingEntity.hasStatusEffect(StatusEffects.BLINDNESS)) {
                StatusEffectInstance statusEffectInstance = livingEntity.getStatusEffect(StatusEffects.BLINDNESS);
                c.getEffectApply().applyBlindness(fogType, statusEffectInstance, viewDistance);
            }
        }
    }

    @Unique
    private static FogType toType(Camera ca, CameraSubmersionType ct) {
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
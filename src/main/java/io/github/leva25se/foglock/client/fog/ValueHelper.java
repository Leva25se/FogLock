package io.github.leva25se.foglock.client.fog;

import io.github.leva25se.foglock.client.setting.FogSetting;
import net.minecraft.client.render.Camera;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;

import java.util.HashMap;

public class ValueHelper {

    private final HashMap<FloatType, Float> requireValue = new HashMap<>();
    private final HashMap<FloatType, Float> nowValue = new HashMap<>();
    private final HashMap<FloatType, Float> deltaValue = new HashMap<>();

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

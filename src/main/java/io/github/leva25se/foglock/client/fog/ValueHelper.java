package io.github.leva25se.foglock.client.fog;

import io.github.leva25se.foglock.client.setting.FogSetting;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;

public class ValueHelper {
    private final static boolean POTION_PRIORITY = true;
    private final long potionApplyTime;
    private final HashMap<FloatType, Float> requireValue = new HashMap<>();
    private final HashMap<FloatType, Float> currentValues = new HashMap<>();
    private final HashMap<FloatType, Float> startValues = new HashMap<>();
    private final HashMap<FloatType, Float> deltaValues = new HashMap<>();
    private final HashMap<FloatType, Long> startTime = new HashMap<>();
    private final HashMap<FloatType, Long> endTime = new HashMap<>();

    public ValueHelper(long potionApplyTime) {
        this.potionApplyTime = potionApplyTime;
    }

    public float getValue(FloatType floatType, HashMap<FloatType, FogSetting> map, Camera camera, float vieDistance, boolean thickFog, float requireNow, boolean potion, ClientLevel clientLevel) {
        FogSetting setting = null;
        if (!potion) {
            if (map.containsKey(floatType)) {
                setting = map.get(floatType);
                float result = setting.get(camera, vieDistance, thickFog, requireNow, clientLevel);
                if (result != -1) {
                    requireNow = result;
                }
            } else {
                return requireNow;
            }
        }
        if (requireValue.containsKey(floatType)) {
            float currentValue = currentValues.get(floatType);
            if (currentValue != requireNow) {
                if (requireValue.get(floatType) != requireNow) {

                    long time;
                    if (POTION_PRIORITY && potion) {
                        switch (floatType) {
                            case START, END -> time = -1;
                            default -> time = potionApplyTime;
                        }
                    } else {
                        if (setting == null) {
                            setting = map.get(floatType);
                        }
                        time = setting.getTime();
                    }
                    if (time == -1) {
                        requireValue.put(floatType, requireNow);
                        currentValues.put(floatType, requireNow);
                        return requireNow;
                    }

                    long currentTime = System.currentTimeMillis();
                    deltaValues.put(floatType, (requireNow - currentValue) / time);
                    startTime.put(floatType, currentTime);
                    startValues.put(floatType, currentValue);
                    requireValue.put(floatType, requireNow);
                    endTime.put(floatType, System.currentTimeMillis() + time);
                    return currentValue;
                } else {
                    long time = System.currentTimeMillis();
                    long timeEnd = endTime.get(floatType);
                    if (time > timeEnd) {
                        startValues.remove(floatType);
                        deltaValues.remove(floatType);
                        startTime.remove(floatType);
                        currentValues.put(floatType, requireNow);
                        return requireNow;
                    } else {
                        float result = startValues.get(floatType) + (deltaValues.get(floatType) * (time - startTime.get(floatType)));
                        if (result == requireNow) {
                            startValues.remove(floatType);
                            deltaValues.remove(floatType);
                            startTime.remove(floatType);
                        }
                        currentValues.put(floatType, result);
                        return result;
                    }
                }
            } else {
                return currentValue;
            }
        }

        currentValues.put(floatType, requireNow);
        requireValue.put(floatType, requireNow);
        return requireNow;
    }

    public FogType getType(Camera ca, net.minecraft.world.level.material.FogType ct) {
        switch (ct) {
            case LAVA -> {
                Entity entity = ca.getEntity();
                if (entity instanceof LivingEntity && ((LivingEntity) entity).hasEffect(MobEffects.FIRE_RESISTANCE)) {
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

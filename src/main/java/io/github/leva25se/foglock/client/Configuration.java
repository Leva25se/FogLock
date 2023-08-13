package io.github.leva25se.foglock.client;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;

import java.util.ArrayList;
import java.util.HashMap;

public class Configuration {
    static ArrayList<FogType> fogEnable = new ArrayList<>();
    static HashMap<FogType, Float> fogStart = new HashMap<>();
    static HashMap<FogType, Float> fogEnd = new HashMap<>();

    public static FogType toType(Camera ca, CameraSubmersionType ct){
        switch (ct){
            case LAVA -> {
                Entity entity = ca.getFocusedEntity();
                if (entity instanceof LivingEntity && ((LivingEntity) entity).hasStatusEffect(StatusEffects.FIRE_RESISTANCE)){
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
        }
        return null;
    }

    public static FogType isEnable(Camera ca, CameraSubmersionType ct){
        FogType fogType = toType(ca, ct);
        if (fogType != null){
            if (fogEnable.contains(fogType)){
                return fogType;
            }
        }
        return null;
    }
    public static float getStart(FogType fogType) {
        return fogStart.get(fogType);
    }
    public static float getEnd(FogType fogType) {
        return fogEnd.get(fogType);
    }
    public static void add(FogType fogType, Float start, Float end){
        fogEnable.add(fogType);
        fogStart.put(fogType, start);
        fogEnd.put(fogType, end);

        System.out.println(fogEnable);
        System.out.println(fogStart);
        System.out.println(fogEnd);
    }
}

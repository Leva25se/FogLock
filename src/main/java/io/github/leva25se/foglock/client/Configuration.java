package io.github.leva25se.foglock.client;

import com.google.gson.JsonObject;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;

import java.util.HashMap;

public class Configuration {
    static HashMap<FogType, Float[]> fog = new HashMap<>();

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

    public static Float[] get(Camera ca, CameraSubmersionType ct){
        FogType fogType = toType(ca, ct);
        return fog.get(fogType);
    }

    private static float get(JsonObject jsonObject, String str){
        return jsonObject.get(str).getAsFloat();
    }
    public static void add(FogType fogType, JsonObject jo){
        Float[] floats = new Float[]{get(jo, "start"),get(jo, "end")};
        fog.put(fogType, floats);
    }


}

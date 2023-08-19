package io.github.leva25se.foglock.client;

import com.google.gson.JsonObject;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;

import java.util.HashMap;

public class Configuration {
    static HashMap<FogType, HashMap<FloatType, Float> > fog = new HashMap<>();

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

    public static HashMap<FloatType, Float> get(Camera ca, CameraSubmersionType ct){
        FogType fogType = toType(ca, ct);
        return fog.getOrDefault(fogType, null);
    }

    private static float get(JsonObject jsonObject, String str){
        return jsonObject.get(str).getAsFloat();
    }
    public static void add(FogType fogType, JsonObject jo){
        HashMap<FloatType, Float> floatHashMap = new HashMap<>();
        if (jo.has("start")) {
            floatHashMap.put(FloatType.START, get(jo, "start"));
        }
        if (jo.has("end")) {
            floatHashMap.put(FloatType.END, get(jo, "end"));
        }
        if (jo.has("alpha")){
            floatHashMap.put(FloatType.ALPHA, get(jo, "alpha"));
        }
        if (jo.has("r") && jo.has("g") && jo.has("b")){
            floatHashMap.put(FloatType.R, get(jo, "r"));
            floatHashMap.put(FloatType.G, get(jo, "g"));
            floatHashMap.put(FloatType.B, get(jo, "b"));
        }
        fog.put(fogType, floatHashMap);
    }


}

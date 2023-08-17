package io.github.leva25se.foglock.client;

import com.google.gson.JsonObject;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;

import java.util.ArrayList;
import java.util.HashMap;

public class Configuration {
    static HashMap<FogType, ArrayList<Float>> fog = new HashMap<>();

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

    public static ArrayList<Float> get(Camera ca, CameraSubmersionType ct){
        FogType fogType = toType(ca, ct);
        return fog.get(fogType);
    }

    private static float get(JsonObject jsonObject, String str){
        return jsonObject.get(str).getAsFloat();
    }
    public static void add(FogType fogType, JsonObject jo){
        ArrayList<Float> floats = new ArrayList<>();
        floats.add(get(jo, "start"));
        floats.add(get(jo, "end"));
        if (jo.has("alpha")){
            floats.add(get(jo, "alpha"));
        }
        if (jo.has("r") && jo.has("g") && jo.has("b")){
            floats.add(get(jo, "r"));
            floats.add(get(jo, "g"));
            floats.add(get(jo, "b"));
        }
        fog.put(fogType, floats);
    }


}

package io.github.leva25se.foglock.client;

import com.google.gson.JsonObject;
import io.github.leva25se.foglock.client.setting.FloatFog;
import io.github.leva25se.foglock.client.setting.FogSetting;
import io.github.leva25se.foglock.client.setting.StringFog;
import io.github.leva25se.foglock.client.value.StringValue;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;

import java.util.HashMap;

public class Configuration {

    private final HashMap<FogType, HashMap<FloatType, FogSetting>> fog = new HashMap<>();

    public FogType toType(Camera ca, CameraSubmersionType ct){
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
            default -> {
                return FogType.UNDEFINED;
            }
        }
    }

    public HashMap<FloatType, FogSetting> get(FogType type) {
        if (type != null){
            if (fog.containsKey(type)) {
                return fog.get(type);
            }
        }
        return null;
    }

    private String getS(JsonObject jsonObject, String str) {
        return jsonObject.get(str).getAsString();
    }

    private float getF(JsonObject jsonObject, String str) {
        return jsonObject.get(str).getAsFloat();
    }

    public void add(FogType fogType, JsonObject jo, StringValue stringValue) {
        HashMap < FloatType, FogSetting > floatHashMap = new HashMap <> ();
        for (FloatType floatType: FloatType.values()) {
            String str = floatType.name().toLowerCase();
            if (jo.has(str)) {
                if (jo.getAsJsonPrimitive(str).isNumber()) {
                    floatHashMap.put(floatType, new FloatFog(getF(jo, str)));
                } else {
                    floatHashMap.put(floatType, new StringFog(getS(jo, str), stringValue));
                }
            }
        }
        fog.put(fogType, floatHashMap);
    }
}

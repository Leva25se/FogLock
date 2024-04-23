package io.github.leva25se.foglock.client.configuration;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.leva25se.foglock.client.fog.FloatType;
import io.github.leva25se.foglock.client.fog.FogType;
import io.github.leva25se.foglock.client.setting.FloatFog;
import io.github.leva25se.foglock.client.setting.FogSetting;
import io.github.leva25se.foglock.client.setting.StringFog;
import io.github.leva25se.foglock.client.value.StringValue;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class FogConfigurationLoader {
    public FogConfigurationLoader(JsonObject jsonObject, StringValue stringValue, HashMap<Identifier, FogConfiguration> configuration, HashMap <FogType, HashMap <FloatType, FogSetting>> default1) {
        if (jsonObject.has("identifiers")) {
            JsonArray array = jsonObject.get("identifiers").getAsJsonArray();
            for (int i = 0; i <array.size(); i++) {
                JsonObject jsonObject1 = array.get(i).getAsJsonObject();
                Identifier key = new Identifier(jsonObject1.get("namespace").getAsString(), jsonObject1.get("path").getAsString());
                HashMap <FogType, HashMap <FloatType, FogSetting>> fogSettingCreator = new HashMap <>();
                for (FogType fogType: FogType.values()) {
                    if (jsonObject1.has(fogType.name())) {
                        JsonObject jo = jsonObject1.get(fogType.name()).getAsJsonObject();
                        HashMap <FloatType, FogSetting> floatHashMap = new HashMap <>();
                        for (FloatType floatType: FloatType.values()) {
                            String str = floatType.name().toLowerCase();
                            if (jo.has(str)) {
                                long time = 1;
                                String str1 = str + "Time";
                                if (jo.has(str1)) {
                                    time = jo.get(str1).getAsLong();
                                }
                                if (jo.getAsJsonPrimitive(str).isNumber()) {
                                    floatHashMap.put(floatType, new FloatFog(jo.get(str).getAsFloat(), time));
                                } else {
                                    floatHashMap.put(floatType, new StringFog(jo.get(str).getAsString(), stringValue, time));
                                }
                            }
                        }
                        fogSettingCreator.put(fogType, floatHashMap);
                    }
                }
                if (!fogSettingCreator.keySet().isEmpty()) {
                    configuration.put(key, new FogConfiguration(fogSettingCreator, jsonObject1.get("priority").getAsInt()));
                }
            }
        }
        for (FogType fogType: FogType.values()) {
            if (jsonObject.has(fogType.name())) {
                JsonObject jo = jsonObject.get(fogType.name()).getAsJsonObject();
                HashMap <FloatType, FogSetting> floatHashMap = new HashMap <>();
                for (FloatType floatType: FloatType.values()) {
                    String str = floatType.name().toLowerCase();
                    if (jo.has(str)) {
                        long time = 1;
                        String str1 = str + "Time";
                        if (jo.has(str1)) {
                            time = jo.get(str1).getAsLong();
                        }
                        if (jo.getAsJsonPrimitive(str).isNumber()) {
                            floatHashMap.put(floatType, new FloatFog(jo.get(str).getAsFloat(), time));
                        } else {
                            floatHashMap.put(floatType, new StringFog(jo.get(str).getAsString(), stringValue, time));
                        }
                    }
                }
                default1.put(fogType, floatHashMap);
            }
        }
    }
}
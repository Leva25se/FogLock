package io.github.leva25se.foglock.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;

import java.io.*;
import java.nio.file.Path;

public class FogLockClient implements ClientModInitializer {

    private void setDefaultValue(){

    }
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitializeClient() {

        Path path = FabricLoader.getInstance().getConfigDir();
        File file = new File(path + "\\FogLock.json");
        if (!file.exists()) {
            new ConfigLoader().load(file);
        }
        try {
            Gson gson = new Gson();
            var json = gson.fromJson(new FileReader(file), JsonObject.class);
            {
                if (json.has("LAVA")){
                    JsonObject jsonObject = json.getAsJsonObject("LAVA");
                    Configuration.add(FogType.LAVA, jsonObject.get("start").getAsFloat(), jsonObject.get("end").getAsFloat());
                }
            }
            {
                if (json.has("LAVA_FIRE_RESISTANCE")){
                    JsonObject jsonObject = json.getAsJsonObject("LAVA_FIRE_RESISTANCE");
                    Configuration.add(FogType.LAVA_FIRE_RESISTANCE, jsonObject.get("start").getAsFloat(), jsonObject.get("end").getAsFloat());
                }
            }
            {
                if (json.has("NONE")){
                    JsonObject jsonObject = json.getAsJsonObject("NONE");
                    Configuration.add(FogType.NONE, jsonObject.get("start").getAsFloat(), jsonObject.get("end").getAsFloat());
                }
            }
            {
                if (json.has("POWDER_SNOW")){
                    JsonObject jsonObject = json.getAsJsonObject("POWDER_SNOW");
                    Configuration.add(FogType.POWDER_SNOW, jsonObject.get("start").getAsFloat(), jsonObject.get("end").getAsFloat());
                }
            }
            {
                if (json.has("WATER")){
                    JsonObject jsonObject = json.getAsJsonObject("WATER");
                    Configuration.add(FogType.WATER, jsonObject.get("start").getAsFloat(), jsonObject.get("end").getAsFloat());
                }
            }

        } catch (NumberFormatException e) {
            LOGGER.error("Config file invalid: " + e.getMessage());
            setDefaultValue();
        } catch (FileNotFoundException e){
            throw new RuntimeException(e);
        }

    }
}

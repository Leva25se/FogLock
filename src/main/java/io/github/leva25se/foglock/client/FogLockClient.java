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
            var json =  new Gson().fromJson(new FileReader(file), JsonObject.class);

            for (FogType fogType : FogType.values()) {
                if (json.has(fogType.name())) {
                    JsonObject jsonObject = json.getAsJsonObject(fogType.name());
                    Configuration.add(fogType, jsonObject);
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

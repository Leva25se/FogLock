package io.github.leva25se.foglock.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;

public class FogLockClient implements ClientModInitializer {

    private static final Configuration configuration = new Configuration();

    public static Configuration getConfiguration(){
        return FogLockClient.configuration;
    }


    @Override
    public void onInitializeClient() {
        Path path = FabricLoader.getInstance().getConfigDir();
        File file = new File(path + "\\FogLock.json");
        if (!file.exists()) {
            new ConfigCreator().load(file);
        }
        try {
            var json =  new Gson().fromJson(new FileReader(file), JsonObject.class);
            for (FogType fogType : FogType.values()) {
                if (json.has(fogType.name())) {
                    JsonObject jsonObject = json.getAsJsonObject(fogType.name());
                    configuration.add(fogType, jsonObject);
                }
            }
        } catch (NumberFormatException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

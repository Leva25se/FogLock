package io.github.leva25se.foglock.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.leva25se.foglock.client.configuration.ConfigCreator;
import io.github.leva25se.foglock.client.configuration.ConfigUpdater;
import io.github.leva25se.foglock.client.value.AdvancedCalculation;
import io.github.leva25se.foglock.client.value.SimpleCalculation;
import io.github.leva25se.foglock.client.value.StringValue;
import io.github.leva25se.foglock.client.value.Value;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
        {
            ConfigCreator configCreator = new ConfigCreator(file);
            try {
                if (!file.exists()) {
                    configCreator.load();
                }
                new ConfigUpdater(configCreator, file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        try {
            var json =  new Gson().fromJson(new FileReader(file), JsonObject.class);
            StringValue stringValue;
            switch (json.get("calculation").getAsString()){
                case "advanced" -> stringValue = new AdvancedCalculation();
                case "simple" -> stringValue = new SimpleCalculation();
                default -> stringValue = new Value();
            }
            for (FogType fogType : FogType.values()) {
                if (json.has(fogType.name())) {
                    JsonObject jsonObject = json.getAsJsonObject(fogType.name());
                    configuration.add(fogType, jsonObject, stringValue);
                }
            }
        } catch (NumberFormatException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

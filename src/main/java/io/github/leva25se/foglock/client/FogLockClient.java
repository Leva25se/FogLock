package io.github.leva25se.foglock.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.leva25se.foglock.client.configuration.Configuration;
import io.github.leva25se.foglock.client.configuration.ConfigurationManager;
import io.github.leva25se.foglock.client.configuration.FogConfiguration;
import io.github.leva25se.foglock.client.configuration.FogConfigurationLoader;
import io.github.leva25se.foglock.client.setting.FogSetting;
import io.github.leva25se.foglock.client.value.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;

public class FogLockClient implements ClientModInitializer {

    private static final Configuration configuration = new Configuration();

    public static Configuration getConfiguration() {
        return FogLockClient.configuration;
    }

    @Override
    public void onInitializeClient() {
        Path path = FabricLoader.getInstance().getConfigDir();
        File file = new File(path + "\\FogLock.json");
        try {
            ConfigurationManager fileManager = new ConfigurationManager(file);
            fileManager.update();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            JsonObject json = new Gson().fromJson(new FileReader(file), JsonObject.class);
            HashMap <Identifier, FogConfiguration> configuration1 = new HashMap <>();
            HashMap <FogType, HashMap <FloatType, FogSetting>> default1 = new HashMap <>();
            StringValue stringValue;
            ApplyPlaceholders applyPlaceholders = new ApplyPlaceholders();
            switch (json.get("mathModule").getAsString().toLowerCase()) {
                case "simple" -> stringValue = new SimpleMathModule(applyPlaceholders);
                case "advanced" -> stringValue = new AdvancedMathModule(applyPlaceholders);
                default -> stringValue = new Value();
            }
            new FogConfigurationLoader(json, stringValue, configuration1, default1);
            configuration.set(configuration1, default1, json.get("worldAndBiomeEnable").getAsBoolean(), json.get("biomeTagsEnable").getAsBoolean());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}
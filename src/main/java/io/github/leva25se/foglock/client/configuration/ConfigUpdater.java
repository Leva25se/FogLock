package io.github.leva25se.foglock.client.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ConfigUpdater {
    public ConfigUpdater(ConfigCreator configCreator, File file) throws IOException {
        com.google.gson.Gson gson = new GsonBuilder().create();
        JsonObject json =  new Gson().fromJson(new FileReader(file), JsonObject.class);
        if (!json.has("AutoAddNewFeaturesToConfiguration")){
            json.addProperty("AutoAddNewFeaturesToConfiguration", true);
        }
        if (!json.has("version")){
            json.addProperty("version", gson.toJson(1.41));
            json.addProperty("calculation", "none");
        }
        configCreator.write(json);
    }
}

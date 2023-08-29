package io.github.leva25se.foglock.client.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class ConfigCreator {
    private final  Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File file;
    public ConfigCreator(File file){
        this.file = file;
    }
    public void load() throws IOException {
        HashMap<String, Object> json = new HashMap<>();
        {
            HashMap<String, Object> map = new HashMap<>();
            map.put("end", 128);
            map.put("start", "32");
            map.put("b", 1.5f);
            json.put("NONE", map);
        }
        gson.toJson(json, new FileWriter(file));
    }
    public void write(JsonObject json) throws IOException {
            gson.toJson(json, new FileWriter(file));
    }
}

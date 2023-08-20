package io.github.leva25se.foglock.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;

public class ConfigCreator {
    public void load(File file) {
        HashMap<String, HashMap<String, Integer>> json = new HashMap<>();
        {
            HashMap<String, Integer> map = new HashMap<>();
            map.put("end", 128);
            map.put("start", 32);
            json.put("NONE", map);
        }
        try (Writer writer = new FileWriter(file)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(json, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

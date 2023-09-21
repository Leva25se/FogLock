package io.github.leva25se.foglock.client.configuration;

import com.google.gson.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ConfigurationManager {
    private final File file;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public ConfigurationManager(File file) throws IOException {
        this.file = file;
        if (!file.exists()) {
            HashMap <String, Object> json = new HashMap <> ();
            json.put("version", "1.5");
            json.put("AutoAddNewFeaturesToConfiguration", true);
            json.put("calculation", "none");
            json.put("worldAndBiomeEnable", true);
            json.put("biomeTagsEnable", true);
            HashMap <String, Object> none = new HashMap <> ();
            none.put("start", 32);
            none.put("end", 128);
            none.put("b", 2);
            none.put("alpha", 0.25);
            json.put("identifiers", generateTagList());
            json.put("NONE", none);
            write(json);
        }
    }
    private ArrayList <Object> generateTagList() {
        ArrayList <Object> end = new ArrayList <> ();
        HashMap <String, Object> end1 = new HashMap <> ();
        end1.put("namespace", "minecraft");
        end1.put("path", "is_end");
        end1.put("priority", 1);
        HashMap <String, Object> noneEnd = new HashMap <> ();
        end1.put("NONE", noneEnd);
        noneEnd.put("r", 0.875f);
        noneEnd.put("g", 0.054f);
        noneEnd.put("b", 0.917f);
        noneEnd.put("start", 16);
        noneEnd.put("end", 128);
        end.add(end1);
        return end;
    }

    public void write(Object json) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(json, writer);
        }
    }

    public void update() throws IOException {
        JsonObject json = new Gson().fromJson(new FileReader(file), JsonObject.class);
        if (json.has("version")) {
            switch (json.get("version").getAsString()) {
                case "1.4", "1.41" -> {
                    if (json.get("AutoAddNewFeaturesToConfiguration").getAsBoolean()) {
                        json.add("identifiers", JsonParser.parseString(gson.toJson(generateTagList())));
                    }
                    add(json);
                }
            }
        } else {
            json.addProperty("AutoAddNewFeaturesToConfiguration", true);
            json.addProperty("calculation", "none");
            add(json);
        }
        json.addProperty("version", "1.5");
        write(json);
    }
    private void add(JsonObject json){
        json.addProperty("worldAndBiomeEnable", true);
        json.addProperty("biomeTagsEnable", true);
    }
}
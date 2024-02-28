package io.github.leva25se.foglock.client.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ConfigurationManager {
    private final File file;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public ConfigurationManager(File file) throws IOException {
        this.file = file;
        if (!file.exists()) {
            HashMap <String, Object> json = new HashMap <>();
            json.put("version", "1.6");
            json.put("autoAddNewFeaturesToConfiguration", true);
            json.put("mathModule", "advanced");
            json.put("worldAndBiomeEnable", true);
            json.put("biomeTagsEnable", true);
            HashMap <String, Object> none = new HashMap <>();
            none.put("start", "{vieDistance}*0.3");
            none.put("end", "{vieDistance}*0.8");
            none.put("startTime", 250);
            none.put("endTime", 250);
            none.put("b", 2);
            none.put("alpha", 1);
            json.put("identifiers", generateTagList());
            json.put("NONE", none);
            write(json);
        }
    }
    private ArrayList <Object> generateTagList() {
        ArrayList <Object> end = new ArrayList<>();
        HashMap <String, Object> end1 = new HashMap <>();
        end1.put("namespace", "minecraft");
        end1.put("path", "is_end");
        end1.put("priority", 1);
        HashMap <String, Object> noneEnd = new HashMap <>();
        end1.put("NONE", noneEnd);
        noneEnd.put("r", 0.875f);
        noneEnd.put("g", 0.054f);
        noneEnd.put("b", 0.917f);
        noneEnd.put("start", 16);
        noneEnd.put("end", 64);
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
                    if (json.get("autoAddNewFeaturesToConfiguration").getAsBoolean()) {
                        json.add("identifiers", JsonParser.parseString(gson.toJson(generateTagList())));
                    }
                    add(json);
                }
                case "1.5" -> {
                    boolean features = json.get("AutoAddNewFeaturesToConfiguration").getAsBoolean();
                    String str = json.get("calculation").getAsString();
                    if (str.equalsIgnoreCase("none") && features) {
                        json.addProperty("mathModule", "advanced");
                        JsonObject jsonObject = json.getAsJsonObject("NONE");
                        jsonObject.addProperty("start", "{vieDistance}*0.3");
                        jsonObject.addProperty("end", "{vieDistance}*0.8");
                        jsonObject.addProperty("startTime", 250);
                        jsonObject.addProperty("endTime", 250);
                    } else {
                        json.addProperty("mathModule", str);
                    }
                    json.remove("calculation");
                    json.addProperty("autoAddNewFeaturesToConfiguration", features);
                    json.remove("AutoAddNewFeaturesToConfiguration");
                }
            }
        } else {
            json.addProperty("autoAddNewFeaturesToConfiguration", true);
            json.addProperty("mathModule", "advanced");
            add(json);
        }
        json.addProperty("version", "1.6");
        write(json);
    }
    private void add(JsonObject json){
        json.addProperty("worldAndBiomeEnable", true);
        json.addProperty("biomeTagsEnable", true);
    }
}
package io.github.leva25se.foglock.client.value;

import io.github.leva25se.foglock.client.fog.FloatType;
import net.minecraft.client.render.Camera;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class AdvancedMathModule implements StringValue  {

    private final ApplyPlaceholders applyPlaceholders;
    private final int cacheSize = FloatType.values().length * 5;
    private final HashMap<String, Float> cache = new HashMap<>();
    public AdvancedMathModule(ApplyPlaceholders applyPlaceholders){
        this.applyPlaceholders = applyPlaceholders;
    }
    @Override
    public float getValue(String str, Camera camera, float vieDistance, boolean thickFog) {
        str = applyPlaceholders.applyPlaceholders(str, camera, vieDistance, thickFog);
        if (cache.containsKey(str)){
            return cache.get(str);
        }
        if (cache.size() > cacheSize) {
            cache.clear();
        }
        ArrayList<Float> elements = new ArrayList<>();
        ArrayList<Character> actions = new ArrayList<>();
        ArrayList<Integer> priority = new ArrayList<>();
        str = str.replace(" ", "");
        int deep = 0;
        char[] chars = str.toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : chars){
            if (Character.isDigit(c) || c == '.'){
                stringBuilder.append(c);
            } else {
                if (!stringBuilder.isEmpty()) {
                    elements.add(Float.valueOf(stringBuilder.toString()));
                    stringBuilder = new StringBuilder();
                }
                switch (c) {
                    case '(' -> deep += 10;
                    case ')' -> deep -= 10;
                    case '+', '-' -> {priority.add(deep + 1); actions.add(c);}
                    case '*', '/', '^' -> {priority.add(deep + 2); actions.add(c);}
                    default -> {
                        priority.add(deep);
                        actions.add(c);
                    }
                }
            }
        }
        if (!stringBuilder.isEmpty()) {
            elements.add(Float.valueOf(stringBuilder.toString()));
        }
        while (!priority.isEmpty()){
            int index = priority.indexOf(Collections.max(priority));
            priority.remove(index);
            float result = getResult(elements, index, actions);
            actions.remove(index);
            elements.set(index, result);
            elements.remove(index + 1);
        }
        float result = elements.getFirst();
        cache.put(str, result);
        return result;
    }

    private static float getResult(ArrayList<Float> elements, int index, ArrayList<Character> actions) {
        float f = elements.get(index);
        float f1 = elements.get(index + 1);
        float result = 0;
        switch (actions.get(index)){
            case '+' -> result = f + f1;
            case '-' -> result = f - f1;
            case '*' -> result = f * f1;
            case '/' -> result = f / f1;
            case '^' -> result = (float) Math.pow(f, f1);
            case 'n' -> result = Math.min(f, f1);
            case 'x' -> result = Math.max(f, f1);
            case '|' -> result = f != 0 ? f : f1;
        }
        return result;
    }
}

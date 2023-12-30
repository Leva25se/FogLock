package io.github.leva25se.foglock.client.value;

import io.github.leva25se.foglock.client.FloatType;
import net.minecraft.client.render.Camera;

import java.util.HashMap;

public class SimpleCalculation implements StringValue {

    private final ApplyPlaceholders applyPlaceholders;
    private final int cacheSize = FloatType.values().length * 5;

    public SimpleCalculation(ApplyPlaceholders applyPlaceholders){
        this.applyPlaceholders = applyPlaceholders;
    }
    private final HashMap<String, Float> cache = new HashMap<>();

    @Override
    public float getValue(String str, Camera camera, float vieDistance, boolean thickFog) {
        str = applyPlaceholders.applyPlaceholders(str, camera, vieDistance, thickFog);
        if (cache.containsKey(str)){
            return cache.get(str);
        }
        if (cache.size() > cacheSize) {
            cache.clear();
        }
        char[] chars = str.toCharArray();
        float result = 0f;
        boolean first = true;
        StringBuilder stringBuilder1 = new StringBuilder();
        for (int i  = 0; i <chars.length; i++) {
            char c1 = chars[i];
            if (Character.isDigit(c1) || c1 == '.') {
                if (first){
                    stringBuilder1.append(c1);
                }
            } else {
                if (first){
                    first = false;
                    result = Float.parseFloat(stringBuilder1.toString());
                }
                i++;
                float f = getNext(i, chars);
                switch (c1){
                    case '+' -> result += f;
                    case '-' -> result -= f;
                    case '*' -> result *= f;
                    case '/' -> result /= f;
                    case 'x' -> result = Math.max(result, f);
                    case 'n' -> result = Math.min(result, f);
                }
            }
        }
        if (first){
            result = Float.parseFloat(stringBuilder1.toString());
        }
        cache.put(str, result);
        return result;
    }

    public  float getNext(int i, char[] chars) {
        StringBuilder stringBuilder1 = new StringBuilder();
        for (; i <chars.length; i += 1) {
            char c1 = chars[i];
            if (Character.isDigit(c1) || c1 == '.') {
                stringBuilder1.append(c1);
            } else {
                return Float.parseFloat(stringBuilder1.toString());
            }
        }
        return Float.parseFloat(stringBuilder1.toString());
    }
}

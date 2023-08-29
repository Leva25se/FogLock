package io.github.leva25se.foglock.client.value;

import net.minecraft.client.render.Camera;

public class Value implements StringValue{
    @Override
    public float getValue(String str, Camera camera, float vieDistance, boolean thickFog) {
        return Float.parseFloat(str);
    }
}

package io.github.leva25se.foglock.client.value;

import net.minecraft.client.render.Camera;

public interface StringValue {
    float getValue(String str, Camera camera, float vieDistance, boolean thickFog);
}

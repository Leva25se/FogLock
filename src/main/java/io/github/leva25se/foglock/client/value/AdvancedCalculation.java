package io.github.leva25se.foglock.client.value;

import net.minecraft.client.render.Camera;

public class AdvancedCalculation implements StringValue  {
    private final SimpleCalculation simpleValue = new SimpleCalculation();
    @Override
    public float getValue(String str, Camera camera, float vieDistance, boolean thickFog) {
        return simpleValue.getValue(str, camera, vieDistance, thickFog);
    }
}

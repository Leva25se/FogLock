package io.github.leva25se.foglock.client.value;

import net.minecraft.client.render.Camera;

public class AdvancedCalculation implements StringValue  {
    private final SimpleCalculation simpleCalculation;
    public AdvancedCalculation(ApplyPlaceholders applyPlaceholders){
        this.simpleCalculation = new SimpleCalculation(applyPlaceholders);
    }
    @Override
    public float getValue(String str, Camera camera, float vieDistance, boolean thickFog) {
        return simpleCalculation.getValue(str, camera, vieDistance, thickFog);
    }
}

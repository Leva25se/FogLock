package io.github.leva25se.foglock.client.value;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;

public class ApplyPlaceholders {

    public String applyPlaceholders(String str, Camera camera, float vieDistance, boolean thickFog){
        Entity entity = camera.getFocusedEntity();
        if (entity instanceof ClientPlayerEntity) {
            str = str.replace("{underwaterVisibility}", String.valueOf(((ClientPlayerEntity)entity).getUnderwaterVisibility()));
        }
        str = str.replace("{vieDistance}", String.valueOf(vieDistance))
                .replace("{thickFog}", String.valueOf((thickFog) ? 1 : 0));
        return str;
    }
}

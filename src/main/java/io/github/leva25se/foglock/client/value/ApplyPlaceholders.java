package io.github.leva25se.foglock.client.value;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApplyPlaceholders {
    private final Pattern pattern = Pattern.compile("\\{(.*?)}");

    public String applyPlaceholders(String str, Camera camera, float vieDistance, boolean thickFog, float current) {
        Matcher matcher = pattern.matcher(str);
        StringBuilder stringBuilder = new StringBuilder(str);
        Entity entity = camera.getFocusedEntity();
        World world = MinecraftClient.getInstance().world;
        if (world == null) {
            return str;
        }
        while (matcher.find()) {
            String string = str.substring(matcher.start(), matcher.end());
            switch (string) {
                case "{vieDistance}" ->
                        stringBuilder.replace(matcher.start(), matcher.end(), String.valueOf(vieDistance));
                case "{thickFog}" ->
                        stringBuilder.replace(matcher.start(), matcher.end(), String.valueOf(thickFog ? 1 : 0));
                case "{underwaterVisibility}" ->
                        stringBuilder.replace(matcher.start(), matcher.end(), (entity instanceof ClientPlayerEntity) ? String.valueOf(((ClientPlayerEntity) entity).getUnderwaterVisibility()) : "0");
                case "{value}" -> stringBuilder.replace(matcher.start(), matcher.end(), String.valueOf(current));
            }
        }
        return stringBuilder.toString();
    }
}

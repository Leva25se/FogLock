package io.github.leva25se.foglock.client.value;


import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.ARGB;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApplyPlaceholders {
    private final Pattern pattern = Pattern.compile("\\{(.*?)}");
    private final Map<String, Float> floatMap = new HashMap<>(16);

    public String applyPlaceholders(String str) {
        Matcher matcher = pattern.matcher(str);
        StringBuilder stringBuilder = new StringBuilder(str);
        while (matcher.find()) {
            String string = str.substring(matcher.start() + 1, matcher.end() - 1);
            stringBuilder.replace(matcher.start(), matcher.end(), String.valueOf(getPlaceholderValue(string)));
        }
        return stringBuilder.toString();
    }

    public void create(Camera camera, float vieDistance, boolean thickFog, float current, ClientLevel clientLevel) {
        floatMap.put("value", current);

        float ac = Mth.clamp(Mth.cos(clientLevel.getDayTime() * 6.2831855F) * 2.0F + 0.5F, 0.0F, 1.0F);
        Vec3 vec3 = camera.getPosition().subtract(2.0, 2.0, 2.0).scale(0.25);
        Vec3 vec32 = CubicSampler.gaussianSampleVec3(vec3, (ix, jx, kx) -> clientLevel.effects().getBrightnessDependentFogColor(new Vec3(0.9, 0.9, 0.9), ac));

        floatMap.put("timeX", (float) vec32.x);
        floatMap.put("timeY", (float) vec32.y);
        floatMap.put("timeZ", (float) vec32.z);

        float f = clientLevel.getDayTime();
        float u = 1;
        float v = 1;
        float w = 1;
        float h = Mth.sin(clientLevel.getSunAngle(f)) > 0.0F ? -1.0F : 1.0F;
        Vector3f vector3f = new Vector3f(h, 0.0F, 0.0F);
        float d = camera.getLookVector().dot(vector3f);
        if (d < 0.0F) {
            d = 0.0F;
        }

        if (d > 0.0F && clientLevel.effects().isSunriseOrSunset(clientLevel.getTimeOfDay(f))) {
            int ad = clientLevel.effects().getSunriseOrSunsetColor(clientLevel.getTimeOfDay(f));
            d *= ARGB.alphaFloat(ad);
            u = u * (1.0F - d) + ARGB.redFloat(ad) * d;
            v = v * (1.0F - d) + ARGB.greenFloat(ad) * d;
            w = w * (1.0F - d) + ARGB.blueFloat(ad) * d;
        }

        floatMap.put("sunR", u);
        floatMap.put("sunG", v);
        floatMap.put("sunB", w);

        float dayTime = ((int) clientLevel.getDayTime()) % 24000;
        floatMap.put("time", Mth.cos(clientLevel.getTimeOfDay(clientLevel.getTimeOfDay(clientLevel.getDayTime())) * 6.2831855F));

        boolean day = dayTime > 23460 || dayTime < 12542;
        floatMap.put("dayTime", dayTime);
        floatMap.put("day", day ? 1f : 0f);
        floatMap.put("night", day ? 0f : 1f);

        Entity entity = camera.getEntity();
        floatMap.put("x", (float) entity.getX());
        floatMap.put("y", (float) entity.getY());
        floatMap.put("z", (float) entity.getZ());

        floatMap.put("vieDistance", vieDistance);
        floatMap.put("thickFog", thickFog ? 1f : 0f);
        floatMap.put("underwaterVisibility", ((LocalPlayer) entity).getWaterVision());
    }

    public void clear() {
        floatMap.clear();
    }

    public float getPlaceholderValue(String s) {
        return floatMap.getOrDefault(s, 0f);
    }

}

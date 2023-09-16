package io.github.leva25se.foglock.client.configuration;

import io.github.leva25se.foglock.client.FloatType;
import io.github.leva25se.foglock.client.FogType;
import io.github.leva25se.foglock.client.setting.FogSetting;

import java.util.HashMap;

public record FogConfiguration(HashMap <FogType, HashMap <FloatType, FogSetting>> fogSetting, int priority) {}
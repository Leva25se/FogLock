package io.github.leva25se.foglock.menu;

import io.github.leva25se.foglock.client.FogLockClient;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import net.minecraft.client.gui.components.Button;

public class ConfigScreen extends Screen {
    private final Screen parent;
    protected ConfigScreen(Screen parent) {
        super(Component.literal("foglock.menu"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        Button button1 = Button.builder(Component.literal("Reload"), (a) -> FogLockClient.getFogLockClient().load()).build();
        addRenderableWidget(button1);
    }

    @Override
    public void onClose() {
        if (minecraft != null) {
            minecraft.setScreen(this.parent);
        }
    }
}

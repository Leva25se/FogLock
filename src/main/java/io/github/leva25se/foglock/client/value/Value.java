package io.github.leva25se.foglock.client.value;

public class Value implements StringValue {
    @Override
    public float getValue(String str) {
        return Float.parseFloat(str);
    }

    @Override
    public boolean placeholders() {
        return false;
    }
}

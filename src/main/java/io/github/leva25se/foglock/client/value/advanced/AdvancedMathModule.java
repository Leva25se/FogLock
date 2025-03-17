package io.github.leva25se.foglock.client.value.advanced;

import io.github.leva25se.foglock.client.value.ApplyPlaceholders;
import io.github.leva25se.foglock.client.value.StringValue;

import java.util.*;

public class AdvancedMathModule implements StringValue {

    private final ApplyPlaceholders applyPlaceholders;
    private final Map<String, FloatGetter> floatGetterHashMap;
    private final Map<String, AdvancedMathModuleData> map;


    public AdvancedMathModule(ApplyPlaceholders applyPlaceholders) {
        this.applyPlaceholders = applyPlaceholders;
        floatGetterHashMap = new LinkedHashMap<>();
        map = new HashMap<>();
        map.put("+", new AdvancedMathModuleData(false, (f) -> () -> f[0].f() + f[1].f(), 1));
        map.put("-", new AdvancedMathModuleData(false, (f) -> () -> f[0].f() - f[1].f(), 1));
        map.put("*", new AdvancedMathModuleData(false, (f) -> () -> f[0].f() * f[1].f(), 2));
        map.put("/", new AdvancedMathModuleData(false, (f) -> () -> f[0].f() / f[1].f(), 2));
        map.put("^", new AdvancedMathModuleData(false, (f) -> () -> (float) Math.pow(f[0].f(), f[1].f()), 3));
        map.put("n", new AdvancedMathModuleData(false, (f) -> () -> Math.min(f[0].f(), f[1].f()), 0));
        map.put("x", new AdvancedMathModuleData(false, (f) -> () -> Math.max(f[0].f(), f[1].f()), 0));
        AdvancedMathModuleData or = new AdvancedMathModuleData(false, (f) -> () -> (f[0].f() == 1) || (f[1].f() == 1) ? 1 : 0, 0);
        map.put("|", or);
        map.put("||", or);
        AdvancedMathModuleData and = new AdvancedMathModuleData(false, (f) -> () -> (f[0].f() == 1) && (f[1].f() == 1) ? 1 : 0, 0);
        map.put("&", and);
        map.put("&&", and);
        map.put("<", new AdvancedMathModuleData(false, (f) -> () -> f[0].f() < f[1].f() ? 1 : 0, 0));
        map.put(">", new AdvancedMathModuleData(false, (f) -> () -> f[0].f() > f[1].f() ? 1 : 0, 0));
        map.put("<=", new AdvancedMathModuleData(false, (f) -> () -> f[0].f() <= f[1].f() ? 1 : 0, 0));
        map.put(">=", new AdvancedMathModuleData(false, (f) -> () -> f[0].f() >= f[1].f() ? 1 : 0, 0));
        map.put("==", new AdvancedMathModuleData(false, (f) -> () -> f[0].f() == f[1].f() ? 1 : 0, 0));
        map.put("!=", new AdvancedMathModuleData(false, (f) -> () -> f[0].f() != f[1].f() ? 1 : 0, 0));
        map.put("min", new AdvancedMathModuleData(true, (f) -> () -> Math.min(f[0].f(), f[1].f()), 0));
        map.put("max", new AdvancedMathModuleData(true, (f) -> () -> Math.max(f[0].f(), f[1].f()), 0));
        map.put("!", new AdvancedMathModuleData(true, (f) -> () -> f[0].f() == 1f ? 0 : 1, 1));
    }

    public FloatGetter preLoad(String string) {
        List<FloatGetter> floats = new ArrayList<>();
        List<String> actions = new ArrayList<>();
        List<Integer> priority = new ArrayList<>();
        char[] chars = string.toCharArray();

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            switch (c) {
                case ' ' -> {
                }
                case '(' -> {
                    i++;
                    int end = findClose(chars, i);
                    String string1 = string.substring(i, end);
                    if (stringBuilder.isEmpty()) {
                        floats.add(preLoad(string1));
                    } else {
                        String string2 = stringBuilder.toString();
                        AdvancedMathModuleData advancedData = map.get(string2);
                        if (advancedData == null) {
                            throw new RuntimeException("Undefined math action " + string2);
                        }

                        if (advancedData.b()) {
                            FloatGetter[] floatGetters = preLoadArray(string1);
                            floats.add(advancedData.creator().f(floatGetters));
                        } else {
                            actions.add(string2);
                            priority.add(advancedData.priority());
                            floats.add(preLoad(string1));
                        }
                        stringBuilder = new StringBuilder();
                    }
                    i = end;
                }
                case ',' -> {
                    actions.add(",");
                    stringBuilder = new StringBuilder();
                }
                case '{' -> {
                    StringBuilder stringBuilder1 = new StringBuilder();
                    while (true) {
                        i++;
                        if (chars[i] == '}') {
                            String string1 = stringBuilder1.toString();
                            floats.add(() -> applyPlaceholders.getPlaceholderValue(string1));
                            break;
                        }
                        stringBuilder1.append(chars[i]);
                    }
                }
                default -> {
                    if (Character.isDigit(c) || c == '.') {
                        if (!stringBuilder.isEmpty()) {
                            String string1 = stringBuilder.toString();
                            if (map.containsKey(string1)) {
                                actions.add(string1);
                                priority.add(map.get(string1).priority());
                                stringBuilder = new StringBuilder();
                            } else {
                                throw new RuntimeException("Undefined math action " + string1);
                            }
                        }
                        StringBuilder numberBuilder = new StringBuilder();
                        boolean dot = true;
                        while (i < chars.length) {
                            char c1 = chars[i];
                            if (Character.isDigit(c1)) {
                                numberBuilder.append(c1);
                            } else if ((dot && c1 == '.')) {
                                numberBuilder.append(".");
                                dot = false;
                            } else {
                                i--;
                                break;
                            }
                            i++;
                        }
                        if (numberBuilder.isEmpty()) {
                            floats.add(new DefaultFloatGetter(0f));
                        } else {
                            floats.add(new DefaultFloatGetter(Float.parseFloat(numberBuilder.toString())));
                        }
                    } else {
                        stringBuilder.append(c);
                    }
                }
            }
        }

        while (!priority.isEmpty()) {
            int max = Collections.max(priority);
            int pos = priority.indexOf(max);
            String string1 = actions.get(pos);
            FloatGetter floatGetter = floats.get(pos);
            FloatGetter floatGetter1 = floats.get(pos + 1);
            floats.remove(pos + 1);
            FloatGetter floatGetter2 = map.get(string1).creator().f(floatGetter, floatGetter1);
            floats.set(pos, floatGetter2);
            priority.remove(pos);
            actions.remove(pos);
        }

        return floats.getFirst();
    }

    public FloatGetter[] preLoadArray(String string) {
        List<FloatGetter> list = new LinkedList<>();
        char[] chars = string.toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        int deep = 0;
        for (char c : chars) {
            boolean append = true;
            switch (c) {
                case '(' -> deep++;
                case ')' -> deep--;
                case ',' -> {
                    if (deep == 0) {
                        list.add(preLoad(stringBuilder.toString()));
                        stringBuilder = new StringBuilder();
                        append = false;
                    }
                }
            }
            if (append) {
                stringBuilder.append(c);
            }
        }
        list.add(preLoad(stringBuilder.toString()));
        return list.toArray(FloatGetter[]::new);
    }



    private int findClose(char[] chars, int start) {
        int deep = 1;
        for (int i = start; i < chars.length; i++) {
            switch (chars[i]) {
                case '(' -> deep += 1;
                case ')' -> deep -= 1;
            }
            if (deep == 0) {
                return i;
            }
        }
        return chars.length;
    }

    @Override
    public float getValue(String str) {
        FloatGetter floatGetter = floatGetterHashMap.get(str);
        if (floatGetter == null) {
            floatGetter = preLoad(str);
            floatGetterHashMap.put(str, floatGetter);
        }
        return floatGetter.f();
    }

    @Override
    public boolean placeholders() {
        return true;
    }
}

package com.cabin.demo.locator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceLocator {
    private static final Map<Class<?>, Object> registry = new ConcurrentHashMap<>();
    public static <T> void register(Class<T> key, T instance) {
        registry.put(key, instance);
    }
    public static <T> T get(Class<T> key) {
        return key.cast(registry.get(key));
    }
}

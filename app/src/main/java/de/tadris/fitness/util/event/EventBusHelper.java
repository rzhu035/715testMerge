package de.tadris.fitness.util.event;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;

public class EventBusHelper {

    /**
     * Register an object to a certain {@link EventBus} instance.
     *
     * @param eventBus the {@link EventBus} instance the object should register to
     * @param object the object that should be registered
     *
     * @return whether registering was successful
     */
    public static boolean saveRegisterTo(@Nullable EventBus eventBus, @NonNull Object object) {
        if (eventBus == null) {
            return false;
        }
        if (!eventBus.isRegistered(object)) {
            eventBus.register(object);
        }
        return true;
    }


    /**
     * Unregister an object from the {@link EventBus} instance it is currently registered to.
     */
    public static void saveUnregisterFrom(@Nullable EventBus eventBus, @NonNull Object object) {
        if (eventBus == null) {
            return;
        }
        if (eventBus.isRegistered(object)) {
            eventBus.unregister(object);
        }
    }
}

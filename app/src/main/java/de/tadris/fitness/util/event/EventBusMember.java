package de.tadris.fitness.util.event;

import androidx.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;

public interface EventBusMember {
    /**
     * Tell the object which {@link EventBus} instance it should register to.
     *
     * @apiNote An object may only register to one {@link EventBus} instance at a time. Calling this
     * function will make the object unregister from the bus its currently registered to first.
     *
     * @implNote Make sure the object unregisters first, if it is currently registered to any
     * {@link EventBus}.
     *
     * @param eventBus the {@link EventBus} instance the object should register to
     * @see #unregisterFromBus() 
     */
    default boolean registerTo(@NonNull EventBus eventBus) {
        unregisterFromBus();
        if(!EventBusHelper.saveRegisterTo(eventBus, this)) {
            return false;
        }
        setEventBus(eventBus);
        return true;
    }

    /**
     * Make the object unregister from the {@link EventBus} its currently registered to.
     * @see #registerTo(EventBus)  
     */
    default void unregisterFromBus() {
        EventBusHelper.saveUnregisterFrom(getEventBus(), this);
    }

    /**
     * {@link EventBus} setter
     * @param eventBus the {@link EventBus} this instance is registered to
     * @see #registerTo(EventBus)
     * @apiNote This function will usually be called when {@link #registerTo(EventBus) registering}
     * to an {@link EventBus}. Use only if you know what you're doing.
     */
    void setEventBus(EventBus eventBus);

    /**
     * {@link EventBus} getter
     * @return the {@link EventBus} this instance is registered to
     * @see #registerTo(EventBus)
     */
    EventBus getEventBus();
}

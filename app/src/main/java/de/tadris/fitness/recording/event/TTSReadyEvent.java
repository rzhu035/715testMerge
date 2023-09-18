package de.tadris.fitness.recording.event;

public class TTSReadyEvent {

    public final String id;
    public final boolean ttsAvailable;

    public TTSReadyEvent(boolean ttsAvailable, String id) {
        this.id = id;
        this.ttsAvailable = ttsAvailable;
    }
}

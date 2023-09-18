package de.tadris.fitness.recording.autostart;

import de.tadris.fitness.Instance;

/**
 * Base class for all voice announcements related to the remaining auto start countdown time.
 */
public abstract class CountdownTimeAnnouncement extends CountdownAnnouncement {
    private final int countdownS;

    public CountdownTimeAnnouncement(Instance instance, int countdownS) {
        super(instance);
        this.countdownS = countdownS;
    }

    public int getCountdownS() {
        return countdownS;
    }
}

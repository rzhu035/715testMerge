package de.tadris.fitness.recording.autostart;

import de.tadris.fitness.Instance;
import de.tadris.fitness.recording.announcement.Announcement;

/**
 * Base class for all auto start countdown related voice announcements.
 */
public abstract class CountdownAnnouncement implements Announcement {
    protected final Instance instance;

    public CountdownAnnouncement(Instance instance) {
        this.instance = instance;
    }

    @Override
    public boolean isAnnouncementEnabled() {
        return instance.userPreferences.isAutoStartCountdownAnnouncementsEnabled();
    }

}

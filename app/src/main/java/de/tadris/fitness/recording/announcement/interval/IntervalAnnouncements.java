/*
 * Copyright (c) 2021 Jannis Scheibe <jannis@tadris.de>
 *
 * This file is part of FitoTrack
 *
 * FitoTrack is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     FitoTrack is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.tadris.fitness.recording.announcement.interval;

import android.content.Context;

import java.util.List;

import de.tadris.fitness.Instance;
import de.tadris.fitness.data.Interval;
import de.tadris.fitness.data.preferences.UserPreferences;
import de.tadris.fitness.recording.BaseWorkoutRecorder;
import de.tadris.fitness.recording.announcement.TTSController;

public class IntervalAnnouncements {

    private final BaseWorkoutRecorder recorder;
    private final TTSController ttsController;
    private List<Interval> intervals;
    private final UserPreferences preferences;

    private int index = -1; // Last spoken interval
    private long speakNextAt = 0;

    public IntervalAnnouncements(Context context, BaseWorkoutRecorder recorder, TTSController ttsController, List<Interval> intervals) {
        this.preferences = Instance.getInstance(context).userPreferences;
        this.recorder = recorder;
        this.ttsController = ttsController;
        this.intervals = intervals;
    }

    public void check(){
        if (getWorkoutDuration() > speakNextAt) {
            speakNextInterval();
        }
    }

    private long getWorkoutDuration() {
        if (preferences.intervalsIncludePauses()) {
            return recorder.getTimeSinceStart();
        } else {
            return recorder.getDuration();
        }
    }

    private void speakNextInterval(){
        if (intervals.size() == 0) {
            return;
        }
        index++;
        if(index >= intervals.size()){
            index= 0;
        }
        Interval interval= intervals.get(index);
        speak(interval);
        recorder.onIntervalWasTriggered(interval);
        speakNextAt += interval.delayMillis;
    }

    private void speak(Interval interval){
        IntervalAnnouncement announcement= new IntervalAnnouncement(interval);
        ttsController.speak(recorder, announcement);
    }

    public void setIntervals(List<Interval> intervals) {
        this.intervals = intervals;
    }
}

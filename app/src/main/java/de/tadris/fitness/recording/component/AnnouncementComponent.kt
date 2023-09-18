/*
 * Copyright (c) 2022 Jannis Scheibe <jannis@tadris.de>
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

package de.tadris.fitness.recording.component

import de.tadris.fitness.data.Interval
import de.tadris.fitness.recording.RecorderService
import de.tadris.fitness.recording.announcement.TTSController
import de.tadris.fitness.recording.announcement.VoiceAnnouncements
import de.tadris.fitness.recording.event.WorkoutGPSStateChanged
import de.tadris.fitness.recording.gps.GpsWorkoutRecorder
import de.tadris.fitness.recording.information.GPSStatus
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * Voice announcements and interval sets
 */
class AnnouncementComponent : RecorderServiceComponent {

    companion object {
        const val TTS_CONTROLLER_ID = "RecorderService"
    }

    private lateinit var service: RecorderService
    private lateinit var mTTSController: TTSController
    private lateinit var announcements: VoiceAnnouncements

    private var lastList: List<Interval>? = null

    override fun register(service: RecorderService) {
        this.service = service
        mTTSController = TTSController(service, TTS_CONTROLLER_ID)
        announcements = VoiceAnnouncements(service, service.instance.recorder, mTTSController, ArrayList())
        EventBus.getDefault().register(this)
    }

    override fun unregister() {
        EventBus.getDefault().unregister(this)
        // Shutdown TTS
        mTTSController.destroy()
    }

    override fun check() {
        super.check()
        // UPDATE INTERVAL LIST IF NEEDED
        val intervalList = service.instance.recorder.intervalList
        if (lastList != intervalList) {
            announcements.applyIntervals(intervalList)
            lastList = intervalList
        }

        // CHECK FOR ANNOUNCEMENTS
        announcements.check()
    }

    @Subscribe
    fun onGPSStateChange(event: WorkoutGPSStateChanged) {
        val announcement = GPSStatus(service)
        if (service.instance.recorder.isResumed && announcement.isAnnouncementEnabled) {
            if (event.oldState == GpsWorkoutRecorder.GpsState.SIGNAL_LOST) { // GPS Signal found
                mTTSController.speak(announcement.spokenGPSFound)
            } else if (event.newState == GpsWorkoutRecorder.GpsState.SIGNAL_LOST) {
                mTTSController.speak(announcement.spokenGPSLost)
            }
        }
    }

}
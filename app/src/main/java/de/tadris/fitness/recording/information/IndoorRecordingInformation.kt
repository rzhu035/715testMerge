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
package de.tadris.fitness.recording.information

import android.content.Context
import de.tadris.fitness.recording.BaseWorkoutRecorder
import de.tadris.fitness.recording.indoor.IndoorWorkoutRecorder

abstract class IndoorRecordingInformation(context: Context) : RecordingInformation(context) {

    override fun getDisplayedText(recorder: BaseWorkoutRecorder): String {
        return if (recorder is IndoorWorkoutRecorder) {
            getDisplayedText(recorder)
        } else {
            "-"
        }
    }

    override fun getSpokenText(recorder: BaseWorkoutRecorder): String {
        return if (recorder is IndoorWorkoutRecorder) {
            getSpokenText(recorder)
        } else {
            ""
        }
    }

    abstract fun getDisplayedText(recorder: IndoorWorkoutRecorder): String
    abstract fun getSpokenText(recorder: IndoorWorkoutRecorder): String

}
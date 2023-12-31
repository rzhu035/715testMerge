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

package de.tadris.fitness.data;

import java.util.ArrayList;
import java.util.List;

public class BaseWorkoutData {

    protected BaseWorkout workout;
    protected List<BaseSample> samples;

    public BaseWorkoutData(BaseWorkout workout, List<BaseSample> samples) {
        this.workout = workout;
        this.samples = samples;
    }

    public BaseWorkout getWorkout() {
        return workout;
    }

    public void setWorkout(BaseWorkout workout) {
        this.workout = workout;
    }

    public List<BaseSample> getSamples() {
        return samples;
    }

    public void setSamples(List<BaseSample> samples) {
        this.samples = samples;
    }

    public GpsWorkoutData castToGpsData() {
        List<GpsSample> samples = new ArrayList<>();
        for (BaseSample sample : this.samples) {
            samples.add((GpsSample) sample);
        }
        return new GpsWorkoutData((GpsWorkout) workout, samples);
    }

    public IndoorWorkoutData castToIndoorData() {
        List<IndoorSample> samples = new ArrayList<>();
        for (BaseSample sample : this.samples) {
            samples.add((IndoorSample) sample);
        }
        return new IndoorWorkoutData((IndoorWorkout) workout, samples);
    }

}

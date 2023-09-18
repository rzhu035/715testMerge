/*
 * Copyright (c) 2020 Jannis Scheibe <jannis@tadris.de>
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

import java.util.List;

public class WorkoutManager {

    public static void calculateInclination(List<GpsSample> samples) {
        samples.get(0).tmpInclination = 0;

        // Calculate inclination
        for (int i = 1; i < samples.size(); i++) {
            GpsSample sample = samples.get(i);
            GpsSample lastSample = samples.get(i - 1);
            double elevationDifference = sample.elevation - lastSample.elevation;
            double distance = sample.toLatLong().sphericalDistance(lastSample.toLatLong());
            sample.tmpInclination = (float) (elevationDifference * 100 / distance);
        }
    }

}

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

package de.tadris.fitness.aggregation;

import androidx.annotation.StringRes;

import de.tadris.fitness.data.BaseWorkout;
import de.tadris.fitness.util.unit.UnitUtils;

/**
 * general note: this could also be used to display the data in the ShowWorkoutActivity.
 */
public interface WorkoutInformation {

    @StringRes
    int getTitleRes();

    String getUnit();

    String getId();

    boolean isInformationAvailableFor(BaseWorkout workout);

    double getValueFromWorkout(BaseWorkout workout);

    AggregationType getAggregationType();

    default String getFormattedValueFromWorkout(BaseWorkout workout) {
        return UnitUtils.autoRound(getValueFromWorkout(workout)) + " " + getUnit();
    }

}

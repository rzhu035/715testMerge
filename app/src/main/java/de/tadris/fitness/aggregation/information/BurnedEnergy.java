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

package de.tadris.fitness.aggregation.information;

import android.content.Context;

import androidx.annotation.StringRes;

import de.tadris.fitness.R;
import de.tadris.fitness.aggregation.AggregationType;
import de.tadris.fitness.data.BaseWorkout;

public class BurnedEnergy extends AbstractWorkoutInformation {
    public BurnedEnergy(Context context) {
        super(context);
    }

    @Override
    @StringRes
    public int getTitleRes() {
        return R.string.workoutBurnedEnergy;
    }

    @Override
    public String getUnit() {
        return energyUnitUtils.getEnergyUnit().getInternationalShortName();
    }

    @Override
    public String getId() {
        return "burned-energy";
    }

    @Override
    public double getValueFromWorkout(BaseWorkout workout) {
        return workout.calorie;
    }

    @Override
    public AggregationType getAggregationType() {
        return AggregationType.SUM;
    }
}

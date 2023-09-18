package de.tadris.fitness.util.unit;

import de.tadris.fitness.R;

public class Kcal implements EnergyUnit {

    @Override
    public double getEnergy(double energyInKcal) {
        return energyInKcal;
    }

    @Override
    public String getInternationalShortName() {
        return "kcal";
    }

    @Override
    public int getLongNameTitle() {
        return R.string.unitKcalLong;
    }
}

package de.tadris.fitness.util.unit;

import de.tadris.fitness.R;

public class KJoule implements EnergyUnit {

    @Override
    public double getEnergy(double energyInKcal) {
        return energyInKcal * 4.1868;
    }

    @Override
    public String getInternationalShortName() {
        return "kJ";
    }

    @Override
    public int getLongNameTitle() {
        return R.string.unitKJoule;
    }
}

package de.tadris.fitness.model;
import com.google.gson.annotations.SerializedName;

public class BirdData {
    @SerializedName("scientificName")
    public String scientificName;
    @SerializedName("probability")
    public double probability;

    public String getScientificName() {
        return scientificName;
    }

    public double getProbability() {
        return probability;
    }

}

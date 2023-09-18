package de.tadris.fitness.util.io.general;

import de.tadris.fitness.util.io.GpxExporter;
import de.tadris.fitness.util.io.GpxImporter;

public final class IOHelper {
    public static final IWorkoutExporter GpxExporter = new GpxExporter();
    public static final IWorkoutImporter GpxImporter = new GpxImporter();
}

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

package de.tadris.fitness.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Database(version = 16, entities = {
        GpsWorkout.class,
        GpsSample.class,
        IndoorWorkout.class,
        IndoorSample.class,
        Interval.class,
        IntervalSet.class,
        WorkoutType.class,
        ExportTargetConfiguration.class,
}, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "fito-track";

    public abstract GpsWorkoutDao gpsWorkoutDao();

    public abstract IndoorWorkoutDao indoorWorkoutDao();

    public abstract WorkoutTypeDao workoutTypeDao();

    public abstract IntervalDao intervalDao();

    public abstract ExportTargetDao exportTargetDao();

    @Nullable
    public BaseWorkout getWorkoutByStart(long start) {
        BaseWorkout workout = gpsWorkoutDao().getWorkoutByStart(start);
        if (workout == null) {
            workout = indoorWorkoutDao().getWorkoutByStart(start);
        }
        return workout;
    }

    public List<BaseWorkout> getAllWorkouts() {
        List<BaseWorkout> allWorkouts = new ArrayList<>();

        List<GpsWorkout> gpsWorkouts = new ArrayList<>(Arrays.asList(gpsWorkoutDao().getWorkouts()));
        List<IndoorWorkout> indoorWorkouts = new ArrayList<>(Arrays.asList(indoorWorkoutDao().getWorkouts()));


        // Merging gps workouts indoor workouts
        while (gpsWorkouts.size() > 0 && indoorWorkouts.size() > 0) {
            GpsWorkout firstGpsWorkout = gpsWorkouts.get(0);
            IndoorWorkout firstIndoorWorkout = indoorWorkouts.get(0);
            if (firstGpsWorkout.start > firstIndoorWorkout.start) {
                allWorkouts.add(gpsWorkouts.remove(0));
            } else {
                allWorkouts.add(indoorWorkouts.remove(0));
            }
        }
        if (gpsWorkouts.size() > 0) {
            allWorkouts.addAll(gpsWorkouts);
        }
        if (indoorWorkouts.size() > 0) {
            allWorkouts.addAll(indoorWorkouts);
        }

        return allWorkouts;
    }

    public long getLastWorkoutTimeByType(String type) {
        BaseWorkout workout = getLastWorkoutByType(type);
        if (workout != null) {
            return workout.start;
        } else {
            return 0;
        }
    }

    @Nullable
    public BaseWorkout getLastWorkoutByType(String type) {
        GpsWorkout gpsWorkout = gpsWorkoutDao().getLastWorkoutByType(type);
        IndoorWorkout indoorWorkout = indoorWorkoutDao().getLastWorkoutByType(type);
        if (gpsWorkout != null && indoorWorkout != null) {
            return gpsWorkout.start > indoorWorkout.start ? gpsWorkout : indoorWorkout;
        } else if (gpsWorkout != null) {
            return gpsWorkout;
        } else return indoorWorkout;
    }

    public static AppDatabase provideDatabase(Context context) {
        return Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME)
                .addMigrations(new Migration(1, 2) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        try {
                            database.beginTransaction();

                            database.execSQL("ALTER table workout add descent REAL NOT NULL DEFAULT 0;");
                            database.execSQL("ALTER table workout add ascent REAL NOT NULL DEFAULT 0");

                            database.execSQL("ALTER TABLE workout_sample RENAME TO workout_sample2;");

                            database.execSQL("CREATE TABLE workout_sample (" +
                                    "id INTEGER NOT NULL DEFAULT NULL PRIMARY KEY," +
                                    "relativeTime INTEGER NOT NULL DEFAULT NULL," +
                                    "elevation REAL NOT NULL DEFAULT NULL," +
                                    "absoluteTime INTEGER NOT NULL DEFAULT NULL," +
                                    "lat REAL NOT NULL DEFAULT NULL," +
                                    "lon REAL NOT NULL DEFAULT NULL," +
                                    "speed REAL NOT NULL DEFAULT NULL," +
                                    "workout_id INTEGER NOT NULL DEFAULT NULL," +
                                    "FOREIGN KEY (workout_id) REFERENCES workout(id) ON DELETE CASCADE);");

                            database.execSQL("INSERT INTO workout_sample (id, relativeTime, elevation, absoluteTime, lat, lon, speed, workout_id) " +
                                    "SELECT id, relativeTime, elevation, absoluteTime, lat, lon, speed, workout_id FROM workout_sample2");

                            database.execSQL("DROP TABLE workout_sample2");

                            database.setTransactionSuccessful();
                        } finally {
                            database.endTransaction();
                        }
                    }
                }, new Migration(2, 3) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        try {
                            database.beginTransaction();

                            database.execSQL("ALTER table workout add COLUMN edited INTEGER not null default 0");

                            database.setTransactionSuccessful();
                        } finally {
                            database.endTransaction();
                        }
                    }
                }, new Migration(3, 4) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        try {
                            database.beginTransaction();

                            database.execSQL("CREATE TABLE interval (\n" +
                                    "    id integer NOT NULL primary key,\n" +
                                    "    name text,\n" +
                                    "    delay_millis integer NOT NULL,\n" +
                                    "    queue_id integer NOT NULL,\n" +
                                    "   FOREIGN KEY (queue_id) \n" +
                                    "      REFERENCES interval_queue (id) \n" +
                                    "         ON DELETE CASCADE \n" +
                                    "         ON UPDATE NO ACTION" +
                                    ");");

                            database.execSQL("CREATE TABLE interval_queue (\n" +
                                    "    id integer NOT NULL primary key,\n" +
                                    "    name text,\n" +
                                    "    state integer not null\n" +
                                    ");");

                            database.setTransactionSuccessful();
                        } finally {
                            database.endTransaction();
                        }
                    }
                }, new Migration(3, 5) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        try {
                            database.beginTransaction();

                            database.execSQL("CREATE TABLE interval (\n" +
                                    "    id integer NOT NULL primary key,\n" +
                                    "    name text,\n" +
                                    "    delay_millis integer NOT NULL,\n" +
                                    "    set_id integer NOT NULL,\n" +
                                    "   FOREIGN KEY (set_id) \n" +
                                    "      REFERENCES interval_set (id) \n" +
                                    "         ON DELETE CASCADE \n" +
                                    "         ON UPDATE NO ACTION" +
                                    ");");

                            database.execSQL("CREATE TABLE interval_set (\n" +
                                    "    id integer NOT NULL primary key,\n" +
                                    "    name text,\n" +
                                    "    state integer not null\n" +
                                    ");");

                            database.setTransactionSuccessful();
                        } finally {
                            database.endTransaction();
                        }
                    }
                }, new Migration(4, 5) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        try {
                            database.beginTransaction();

                            database.execSQL("drop table interval");

                            database.execSQL("drop table interval_queue");

                            database.execSQL("CREATE TABLE interval (\n" +
                                    "    id integer NOT NULL primary key,\n" +
                                    "    name text,\n" +
                                    "    delay_millis integer NOT NULL,\n" +
                                    "    set_id integer NOT NULL,\n" +
                                    "   FOREIGN KEY (set_id) \n" +
                                    "      REFERENCES interval_set (id) \n" +
                                    "         ON DELETE CASCADE \n" +
                                    "         ON UPDATE NO ACTION" +
                                    ");");

                            database.execSQL("CREATE TABLE interval_set (\n" +
                                    "    id integer NOT NULL primary key,\n" +
                                    "    name text,\n" +
                                    "    state integer not null\n" +
                                    ");");

                            database.setTransactionSuccessful();
                        } finally {
                            database.endTransaction();
                        }
                    }
                }, new Migration(5, 6) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        try {
                            database.beginTransaction();

                            database.execSQL("ALTER table workout add COLUMN interval_set_used_id INTEGER not null default 0");

                            database.setTransactionSuccessful();
                        } finally {
                            database.endTransaction();
                        }
                    }
                }, new Migration(6, 7) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        try {
                            database.beginTransaction();

                            database.execSQL("ALTER table workout add COLUMN interval_set_include_pauses INTEGER not null default 0");

                            database.setTransactionSuccessful();
                        } finally {
                            database.endTransaction();
                        }
                    }
                }, new Migration(7, 8) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        try {
                            database.beginTransaction();

                            database.execSQL("ALTER table workout_sample add COLUMN pressure REAL not null default 0");

                            database.setTransactionSuccessful();
                        } finally {
                            database.endTransaction();
                        }
                    }
                }, new Migration(8, 9) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        try {
                            database.beginTransaction();

                            database.execSQL("ALTER table workout_sample add COLUMN elevation_msl REAL not null default 0;");

                            database.execSQL("UPDATE workout_sample set elevation_msl = elevation;");

                            database.setTransactionSuccessful();
                        } finally {
                            database.endTransaction();
                        }
                    }
                }, new Migration(9, 10) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        try {
                            database.beginTransaction();

                            database.execSQL("ALTER table workout_sample add COLUMN heart_rate INTEGER not null default 0;");
                            database.execSQL("ALTER table workout add COLUMN avg_heart_rate INTEGER not null default 0;");
                            database.execSQL("ALTER table workout add COLUMN max_heart_rate INTEGER not null default 0;");

                            database.setTransactionSuccessful();
                        } finally {
                            database.endTransaction();
                        }
                    }
                }, new Migration(10, 11) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        try {
                            database.beginTransaction();

                            database.execSQL("CREATE TABLE workout_type (\n" +
                                    "    id text NOT NULL primary key,\n" +
                                    "    icon text,\n" +
                                    "    title text,\n" +
                                    "    min_distance integer NOT NULL,\n" +
                                    "    color integer NOT NULL,\n" +
                                    "    met integer NOT NULL\n" +
                                    ");");

                            database.setTransactionSuccessful();
                        } finally {
                            database.endTransaction();
                        }
                    }
                }, new Migration(11, 12) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        try {
                            database.beginTransaction();

                            database.execSQL("create index index_workout_sample_workout_id on workout_sample (workout_id)");
                            database.execSQL("create index index_interval_set_id on interval (set_id)");

                            database.setTransactionSuccessful();
                        } finally {
                            database.endTransaction();
                        }
                    }
                }, new Migration(12, 13) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        try {
                            database.beginTransaction();

                            // Add new columns
                            database.execSQL("ALTER table workout add COLUMN min_elevation_msl REAL not null default 0;");
                            database.execSQL("ALTER table workout add COLUMN max_elevation_msl REAL not null default 0;");

                            // Calculate min and max elevation for the new columns
                            // What it does: take the min/max value from samples. If no samples exist for the workout, then it's 0
                            database.execSQL("update workout set min_elevation_msl = " +
                                    "(select min(elevation_msl) from workout_sample where workout_id = workout.id) " +
                                    "where min_elevation_msl=0 and (select count(id) from workout_sample where workout_id = workout.id) > 0");
                            database.execSQL("update workout set max_elevation_msl = " +
                                    "(select max(elevation_msl) from workout_sample where workout_id = workout.id) " +
                                    "where max_elevation_msl=0 and (select count(id) from workout_sample where workout_id = workout.id) > 0");

                            database.setTransactionSuccessful();
                        } finally {
                            database.endTransaction();
                        }
                    }
                }, new Migration(13, 14) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        try {
                            database.beginTransaction();

                            database.execSQL("ALTER TABLE workout RENAME TO workout2;");

                            database.execSQL("CREATE TABLE workout (" +
                                    "id INTEGER NOT NULL DEFAULT NULL PRIMARY KEY," +
                                    "start INTEGER NOT NULL DEFAULT 0," +
                                    "`end` INTEGER NOT NULL DEFAULT 0," +
                                    "duration INTEGER NOT NULL DEFAULT 0," +
                                    "pauseDuration INTEGER NOT NULL DEFAULT 0," +
                                    "comment TEXT DEFAULT NULL," +
                                    "length INTEGER NOT NULL DEFAULT 0," +
                                    "avgSpeed REAL NOT NULL DEFAULT 0," +
                                    "topSpeed REAL NOT NULL DEFAULT 0," +
                                    "avgPace REAL NOT NULL DEFAULT 0," +
                                    "workoutType TEXT DEFAULT NULL," +
                                    "min_elevation_msl REAL NOT NULL DEFAULT 0," +
                                    "max_elevation_msl REAL NOT NULL DEFAULT 0," +
                                    "ascent REAL NOT NULL DEFAULT 0," +
                                    "descent REAL NOT NULL DEFAULT 0," +
                                    "calorie INTEGER NOT NULL DEFAULT 0," +
                                    "edited INTEGER NOT NULL DEFAULT 0," +
                                    "interval_set_used_id INTEGER NOT NULL DEFAULT 0," +
                                    "avg_heart_rate INTEGER NOT NULL DEFAULT 0," +
                                    "max_heart_rate INTEGER NOT NULL DEFAULT 0);");

                            database.execSQL("INSERT INTO workout (id,start,`end`,duration,pauseDuration,comment,length,avgSpeed,topSpeed,avgPace,avgPace,workoutType,min_elevation_msl,max_elevation_msl,ascent,descent,calorie,edited,interval_set_used_id,avg_heart_rate,max_heart_rate) " +
                                    "SELECT id,start,`end`,duration,pauseDuration,comment,length,avgSpeed,topSpeed,avgPace,avgPace,workoutType,min_elevation_msl,max_elevation_msl,ascent,descent,calorie,edited,interval_set_used_id,avg_heart_rate,max_heart_rate FROM workout2");

                            database.execSQL("DROP TABLE workout2");

                            database.execSQL("ALTER table workout_sample add COLUMN interval_triggered INTEGER not null default -1;");

                            database.setTransactionSuccessful();
                        } finally {
                            database.endTransaction();
                        }
                    }
                }, new Migration(14, 15) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        try {
                            database.beginTransaction();

                            database.execSQL("CREATE TABLE indoor_workout (" +
                                    "id INTEGER NOT NULL DEFAULT NULL PRIMARY KEY," +
                                    "start INTEGER NOT NULL DEFAULT 0," +
                                    "`end` INTEGER NOT NULL DEFAULT 0," +
                                    "duration INTEGER NOT NULL DEFAULT 0," +
                                    "pauseDuration INTEGER NOT NULL DEFAULT 0," +
                                    "comment TEXT DEFAULT NULL," +
                                    "workoutType TEXT DEFAULT NULL," +
                                    "calorie INTEGER NOT NULL DEFAULT 0," +
                                    "edited INTEGER NOT NULL DEFAULT 0," +
                                    "repetitions INTEGER NOT NULL DEFAULT 0," +
                                    "avgFrequency REAL NOT NULL DEFAULT 0," +
                                    "maxFrequency REAL NOT NULL DEFAULT 0," +
                                    "avgIntensity REAL NOT NULL DEFAULT 0," +
                                    "maxIntensity REAL NOT NULL DEFAULT 0," +
                                    "interval_set_used_id INTEGER NOT NULL DEFAULT 0," +
                                    "avg_heart_rate INTEGER NOT NULL DEFAULT 0," +
                                    "max_heart_rate INTEGER NOT NULL DEFAULT 0);");

                            database.execSQL("CREATE TABLE indoor_sample (" +
                                    "id INTEGER NOT NULL DEFAULT NULL PRIMARY KEY," +
                                    "absoluteTime INTEGER NOT NULL DEFAULT 0," +
                                    "relativeTime INTEGER NOT NULL DEFAULT 0," +
                                    "heart_rate INTEGER NOT NULL DEFAULT 0," +
                                    "interval_triggered INTEGER NOT NULL DEFAULT 0," +
                                    "workout_id INTEGER NOT NULL DEFAULT 0," +
                                    "intensity REAL NOT NULL DEFAULT 0," +
                                    "frequency REAL NOT NULL DEFAULT 0," +
                                    "repetitions INTEGER NOT NULL DEFAULT 0," +
                                    "absoluteEndTime INTEGER NOT NULL DEFAULT 0," +
                                    "   FOREIGN KEY (workout_id) \n" +
                                    "      REFERENCES indoor_workout (id) \n" +
                                    "         ON DELETE CASCADE \n" +
                                    "         ON UPDATE NO ACTION" +
                                    ");");

                            database.execSQL("ALTER table workout_type add COLUMN type TEXT default 'gps';");

                            database.execSQL("create index index_indoor_sample_workout_id on indoor_sample (workout_id)");

                            database.setTransactionSuccessful();
                        } finally {
                            database.endTransaction();
                        }
                    }
                }, new Migration(15, 16) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        try {
                            database.beginTransaction();

                            database.execSQL("CREATE TABLE export_target_config (\n" +
                                    "    id integer NOT NULL primary key,\n" +
                                    "    source text,\n" +
                                    "    type text,\n" +
                                    "    data text\n" +
                                    ");");

                            database.setTransactionSuccessful();
                        } finally {
                            database.endTransaction();
                        }
                    }
                })
                .allowMainThreadQueries()
                .build();
    }
}
package de.tadris.fitness.model;

public class WorkBean {
    public int workTypeIcon;
    public String time;
    public String workType;

    public String workDistance;

    public String workDistanceTime;

    public WorkBean(int person, String time, String running, String workDistance, String workDistanceTime) {
        workTypeIcon = person;
        this.time  = time;
        workType = running;
        this.workDistance = workDistance;
        this.workDistanceTime = workDistanceTime;
    }
}

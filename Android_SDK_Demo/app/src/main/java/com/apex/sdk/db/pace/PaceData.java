package com.apex.sdk.db.pace;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class PaceData implements Comparable<PaceData> {
    @Id
    private long currentTime;
    private int stepPace;

    @Generated(hash = 758756701)
    public PaceData(long currentTime, int stepPace) {
        this.currentTime = currentTime;
        this.stepPace = stepPace;
    }

    @Generated(hash = 2026698477)
    public PaceData() {
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public int getStepPace() {
        return stepPace;
    }

    public void setStepPace(int stepPace) {
        this.stepPace = stepPace;
    }

    @Override
    public String toString() {
        return "PaceData{" +
                "currentTime=" + currentTime +
                ", stepPace=" + stepPace +
                '}';
    }

    @Override
    public int compareTo(PaceData o) {
        if (o != null) {
            if (this.currentTime > o.currentTime) {
                return 1;
            } else if (this.currentTime == o.currentTime) {
                return 0;
            } else {
                return -1;
            }
        }
        return 0;
    }
}

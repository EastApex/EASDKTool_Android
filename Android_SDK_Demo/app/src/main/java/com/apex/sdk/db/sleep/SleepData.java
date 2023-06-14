package com.apex.sdk.db.sleep;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class SleepData implements Comparable<SleepData> {
    @Id
    private long currentTime;
    private int sleepType;

    @Generated(hash = 1474891069)
    public SleepData(long currentTime, int sleepType) {
        this.currentTime = currentTime;
        this.sleepType = sleepType;
    }

    @Generated(hash = 1639116881)
    public SleepData() {
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public int getSleepType() {
        return sleepType;
    }

    public void setSleepType(int sleepType) {
        this.sleepType = sleepType;
    }

    @Override
    public String toString() {
        return "SleepData{" +
                "currentTime=" + currentTime +
                ", sleepType=" + sleepType +
                '}';
    }

    @Override
    public int compareTo(SleepData o) {
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

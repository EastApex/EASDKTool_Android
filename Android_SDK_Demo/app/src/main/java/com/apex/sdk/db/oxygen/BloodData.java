package com.apex.sdk.db.oxygen;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class BloodData implements Comparable<BloodData> {
    private int blood_oxygen_value;        //血氧值
    @Id
    private long currentTime;

    @Generated(hash = 1186315660)
    public BloodData(int blood_oxygen_value, long currentTime) {
        this.blood_oxygen_value = blood_oxygen_value;
        this.currentTime = currentTime;
    }

    @Generated(hash = 1705986732)
    public BloodData() {
    }

    public int getBlood_oxygen_value() {
        return blood_oxygen_value;
    }

    public void setBlood_oxygen_value(int blood_oxygen_value) {
        this.blood_oxygen_value = blood_oxygen_value;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    @Override
    public String toString() {
        return "BloodData{" +
                "blood_oxygen_value=" + blood_oxygen_value +
                ", currentTime=" + currentTime +
                '}';
    }

    @Override
    public int compareTo(BloodData o) {
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

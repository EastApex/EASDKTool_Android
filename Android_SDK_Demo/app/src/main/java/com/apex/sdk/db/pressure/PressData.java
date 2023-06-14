package com.apex.sdk.db.pressure;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class PressData implements Comparable<PressData> {
    private int press_value;
    @Id
    private long currentTime;
    private int level;

    @Generated(hash = 129819017)
    public PressData(int press_value, long currentTime, int level) {
        this.press_value = press_value;
        this.currentTime = currentTime;
        this.level = level;
    }

    @Generated(hash = 1876746012)
    public PressData() {
    }

    public int getPress_value() {
        return press_value;
    }

    public void setPress_value(int press_value) {
        this.press_value = press_value;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "PressData{" +
                "press_value=" + press_value +
                ", currentTime=" + currentTime +
                ", level=" + level +
                '}';
    }

    @Override
    public int compareTo(PressData o) {
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

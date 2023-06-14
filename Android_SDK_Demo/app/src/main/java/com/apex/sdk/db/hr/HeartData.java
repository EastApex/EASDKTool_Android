package com.apex.sdk.db.hr;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class HeartData implements Comparable<HeartData> {
    @Id
    public long currentTime;//心率时间戳
    public int hr_value;//心率值

    @Generated(hash = 1192789162)
    public HeartData(long currentTime, int hr_value) {
        this.currentTime = currentTime;
        this.hr_value = hr_value;
    }

    @Generated(hash = 241019369)
    public HeartData() {
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public int getHr_value() {
        return hr_value;
    }

    public void setHr_value(int hr_value) {
        this.hr_value = hr_value;
    }

    @Override
    public String toString() {
        return "HeartData{" +
                "currentTime=" + currentTime +
                ", hr_value=" + hr_value +
                '}';
    }

    @Override
    public int compareTo(HeartData o) {
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

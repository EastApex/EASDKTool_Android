package com.apex.sdk.db.daily;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class DailyData implements Comparable<DailyData> {
    @Id
    private long currentTime;       //当前时间
    private int steps;          //运动数据：步数
    private int calorie;         //运动数据：卡路里（单位:小卡)
    private int distance;        //运动数据：距离 （单位:厘米）
    private int duration;            //运动数据：运动时长(单位:秒)
    private int average_heart_rate;    //运动数据：平均心率

    @Generated(hash = 868083451)
    public DailyData(long currentTime, int steps, int calorie, int distance,
            int duration, int average_heart_rate) {
        this.currentTime = currentTime;
        this.steps = steps;
        this.calorie = calorie;
        this.distance = distance;
        this.duration = duration;
        this.average_heart_rate = average_heart_rate;
    }

    @Generated(hash = 556979270)
    public DailyData() {
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public int getCalorie() {
        return calorie;
    }

    public void setCalorie(int calorie) {
        this.calorie = calorie;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getAverage_heart_rate() {
        return average_heart_rate;
    }

    public void setAverage_heart_rate(int average_heart_rate) {
        this.average_heart_rate = average_heart_rate;
    }

    @Override
    public String toString() {
        return "SportData{" +
                "currentTime=" + currentTime +
                ", steps=" + steps +
                ", calorie=" + calorie +
                ", distance=" + distance +
                ", duration=" + duration +
                ", average_heart_rate=" + average_heart_rate +
                '}';
    }

    @Override
    public int compareTo(DailyData o) {
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

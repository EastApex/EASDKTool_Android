package com.apex.sdk.db.freq;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;


@Entity
public class StepFreqData implements Comparable<StepFreqData> {
    @Id
    private long currentTime;
    private int stepFreq;

    @Generated(hash = 703165642)
    public StepFreqData(long currentTime, int stepFreq) {
        this.currentTime = currentTime;
        this.stepFreq = stepFreq;
    }

    @Generated(hash = 814939287)
    public StepFreqData() {
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public int getStepFreq() {
        return stepFreq;
    }

    public void setStepFreq(int stepFreq) {
        this.stepFreq = stepFreq;
    }

    @Override
    public String toString() {
        return "StepFreqData{" +
                "currentTime=" + currentTime +
                ", stepFreq=" + stepFreq +
                '}';
    }

    @Override
    public int compareTo(StepFreqData o) {
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

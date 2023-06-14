package com.apex.sdk.db.resting;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

@Entity
public class RestingRateData {
    @Id
    private long time_stamp;
    @Property
    private int heartRate;
    @Property
    private long currentTime;


    @Generated(hash = 1768302328)
    public RestingRateData(long time_stamp, int heartRate, long currentTime) {
        this.time_stamp = time_stamp;
        this.heartRate = heartRate;
        this.currentTime = currentTime;
    }

    @Generated(hash = 756566857)
    public RestingRateData() {
    }


    public long getTime_stamp() {
        return this.time_stamp;
    }

    public void setTime_stamp(long time_stamp) {
        this.time_stamp = time_stamp;
    }

    public int getHeartRate() {
        return this.heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public long getCurrentTime() {
        return this.currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }
    

   
}

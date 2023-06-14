package com.apex.sdk.db.gps;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class GpsData implements Comparable<GpsData> {
    private double latitude;
    private double longitude;
    @Id
    private long currentTime;

    @Generated(hash = 1635470309)
    public GpsData(double latitude, double longitude, long currentTime) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.currentTime = currentTime;
    }

    @Generated(hash = 1232323744)
    public GpsData() {
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    @Override
    public String toString() {
        return "GpsData{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", currentTime=" + currentTime +
                '}';
    }

    @Override
    public int compareTo(GpsData o) {
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

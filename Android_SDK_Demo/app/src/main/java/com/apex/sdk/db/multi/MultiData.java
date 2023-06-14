package com.apex.sdk.db.multi;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

@Entity
public class MultiData implements Comparable<MultiData> {
    @Id
    private long begin_time_stamp;                                          //起始时间戳
    @Property
    private long end_time_stamp;                                            //停止时间戳
    @Property
    private int steps;                                                      //步数
    @Property
    private int calorie;                                                    //卡路里（单位:小卡)
    @Property
    private int distance;                                                   //距离 （单位:厘米）
    @Property
    private int duration;                                                   //运动时长(单位:秒)
    @Property
    private int training_effect_normal;                                     //训练效果 正常心率 时长(单位:秒)
    @Property
    private int training_effect_warmUp;                                     //训练效果 热身心率 时长(单位:秒)
    @Property
    private int training_effect_fatconsumption;                             //训练效果 消耗脂肪 时长(单位:秒)
    @Property
    private int training_effect_aerobic;                                    //训练效果 有氧心率 时长(单位:秒)
    @Property
    private int training_effect_anaerobic;                                  //训练效果 无氧心率 时长(单位:秒)
    @Property
    private int training_effect_limit;                                      //训练效果 极限心率 时长(单位:秒)
    @Property
    private int average_heart_rate;                                         //平均心率
    @Property
    private float average_temperature;                                      //平均体温（单位：摄氏度）
    @Property
    private int average_altitude;                                           //平均海拔
    @Property
    private int avg_velocity;                                               //平均速度
    @Property
    private int avg_pace;                                                   //平均配速
    @Property
    private int avg_pace_frequency;                                         //平均步频
    @Property
    private int e_type;                                                     //运动类型
    @Property
    private int average_stride;                                             //平均步距(单位厘米)
    @Property
    private int average_heart_rate_max;                                     //最大心率
    @Property
    private int average_heart_rate_min;                                     //最小心率


    @Generated(hash = 553626012)
    public MultiData(long begin_time_stamp, long end_time_stamp, int steps, int calorie, int distance,
            int duration, int training_effect_normal, int training_effect_warmUp,
            int training_effect_fatconsumption, int training_effect_aerobic,
            int training_effect_anaerobic, int training_effect_limit, int average_heart_rate,
            float average_temperature, int average_altitude, int avg_velocity, int avg_pace,
            int avg_pace_frequency, int e_type, int average_stride, int average_heart_rate_max,
            int average_heart_rate_min) {
        this.begin_time_stamp = begin_time_stamp;
        this.end_time_stamp = end_time_stamp;
        this.steps = steps;
        this.calorie = calorie;
        this.distance = distance;
        this.duration = duration;
        this.training_effect_normal = training_effect_normal;
        this.training_effect_warmUp = training_effect_warmUp;
        this.training_effect_fatconsumption = training_effect_fatconsumption;
        this.training_effect_aerobic = training_effect_aerobic;
        this.training_effect_anaerobic = training_effect_anaerobic;
        this.training_effect_limit = training_effect_limit;
        this.average_heart_rate = average_heart_rate;
        this.average_temperature = average_temperature;
        this.average_altitude = average_altitude;
        this.avg_velocity = avg_velocity;
        this.avg_pace = avg_pace;
        this.avg_pace_frequency = avg_pace_frequency;
        this.e_type = e_type;
        this.average_stride = average_stride;
        this.average_heart_rate_max = average_heart_rate_max;
        this.average_heart_rate_min = average_heart_rate_min;
    }

    @Generated(hash = 305623652)
    public MultiData() {
    }


    @Override
    public String toString() {
        return "MultiDataCache{" +
                "begin_time_stamp=" + begin_time_stamp +
                ", end_time_stamp=" + end_time_stamp +
                ", steps=" + steps +
                ", calorie=" + calorie +
                ", distance=" + distance +
                ", duration=" + duration +
                ", training_effect_normal=" + training_effect_normal +
                ", training_effect_warmUp=" + training_effect_warmUp +
                ", training_effect_fatconsumption=" + training_effect_fatconsumption +
                ", training_effect_aerobic=" + training_effect_aerobic +
                ", training_effect_anaerobic=" + training_effect_anaerobic +
                ", training_effect_limit=" + training_effect_limit +
                ", average_heart_rate=" + average_heart_rate +
                ", average_temperature=" + average_temperature +
                ", average_altitude=" + average_altitude +
                ", avg_velocity=" + avg_velocity +
                ", avg_pace=" + avg_pace +
                ", avg_pace_frequency=" + avg_pace_frequency +
                ", e_type=" + e_type +
                ", average_stride=" + average_stride +
                ", average_heart_rate_max=" + average_heart_rate_max +
                ", average_heart_rate_min=" + average_heart_rate_min +
                '}';
    }

    @Override
    public int compareTo(MultiData o) {
        if (o != null) {
            if (this.begin_time_stamp > o.begin_time_stamp) {
                return 1;
            } else if (this.begin_time_stamp == o.begin_time_stamp) {
                return 0;
            } else {
                return -1;
            }
        }
        return 0;
    }

    public long getBegin_time_stamp() {
        return this.begin_time_stamp;
    }

    public void setBegin_time_stamp(long begin_time_stamp) {
        this.begin_time_stamp = begin_time_stamp;
    }

    public long getEnd_time_stamp() {
        return this.end_time_stamp;
    }

    public void setEnd_time_stamp(long end_time_stamp) {
        this.end_time_stamp = end_time_stamp;
    }

    public int getSteps() {
        return this.steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public int getCalorie() {
        return this.calorie;
    }

    public void setCalorie(int calorie) {
        this.calorie = calorie;
    }

    public int getDistance() {
        return this.distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getTraining_effect_normal() {
        return this.training_effect_normal;
    }

    public void setTraining_effect_normal(int training_effect_normal) {
        this.training_effect_normal = training_effect_normal;
    }

    public int getTraining_effect_warmUp() {
        return this.training_effect_warmUp;
    }

    public void setTraining_effect_warmUp(int training_effect_warmUp) {
        this.training_effect_warmUp = training_effect_warmUp;
    }

    public int getTraining_effect_fatconsumption() {
        return this.training_effect_fatconsumption;
    }

    public void setTraining_effect_fatconsumption(int training_effect_fatconsumption) {
        this.training_effect_fatconsumption = training_effect_fatconsumption;
    }

    public int getTraining_effect_aerobic() {
        return this.training_effect_aerobic;
    }

    public void setTraining_effect_aerobic(int training_effect_aerobic) {
        this.training_effect_aerobic = training_effect_aerobic;
    }

    public int getTraining_effect_anaerobic() {
        return this.training_effect_anaerobic;
    }

    public void setTraining_effect_anaerobic(int training_effect_anaerobic) {
        this.training_effect_anaerobic = training_effect_anaerobic;
    }

    public int getTraining_effect_limit() {
        return this.training_effect_limit;
    }

    public void setTraining_effect_limit(int training_effect_limit) {
        this.training_effect_limit = training_effect_limit;
    }

    public int getAverage_heart_rate() {
        return this.average_heart_rate;
    }

    public void setAverage_heart_rate(int average_heart_rate) {
        this.average_heart_rate = average_heart_rate;
    }

    public float getAverage_temperature() {
        return this.average_temperature;
    }

    public void setAverage_temperature(float average_temperature) {
        this.average_temperature = average_temperature;
    }

    public int getAverage_altitude() {
        return this.average_altitude;
    }

    public void setAverage_altitude(int average_altitude) {
        this.average_altitude = average_altitude;
    }


    public int getAvg_velocity() {
        return this.avg_velocity;
    }

    public void setAvg_velocity(int avg_velocity) {
        this.avg_velocity = avg_velocity;
    }

    public int getAvg_pace() {
        return this.avg_pace;
    }

    public void setAvg_pace(int avg_pace) {
        this.avg_pace = avg_pace;
    }

    public int getAvg_pace_frequency() {
        return this.avg_pace_frequency;
    }

    public void setAvg_pace_frequency(int avg_pace_frequency) {
        this.avg_pace_frequency = avg_pace_frequency;
    }

    public int getE_type() {
        return this.e_type;
    }

    public void setE_type(int e_type) {
        this.e_type = e_type;
    }

    public int getAverage_stride() {
        return this.average_stride;
    }

    public void setAverage_stride(int average_stride) {
        this.average_stride = average_stride;
    }

    public int getAverage_heart_rate_max() {
        return this.average_heart_rate_max;
    }

    public void setAverage_heart_rate_max(int average_heart_rate_max) {
        this.average_heart_rate_max = average_heart_rate_max;
    }

    public int getAverage_heart_rate_min() {
        return this.average_heart_rate_min;
    }

    public void setAverage_heart_rate_min(int average_heart_rate_min) {
        this.average_heart_rate_min = average_heart_rate_min;
    }
}

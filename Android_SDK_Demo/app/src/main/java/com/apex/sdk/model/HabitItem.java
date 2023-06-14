package com.apex.sdk.model;

import java.io.Serializable;

public class HabitItem implements Serializable {
    private int e_icon_id;
    private int id;
    private int begin_hour;
    private int begin_minute;
    private int end_hour;
    private int end_minute;
    private int redColor;
    private int greenColor;
    private int blueColor;
    private int duration;
    private int e_action;
    private String content;
    private int habitState;

    public int getE_icon_id() {
        return e_icon_id;
    }

    public void setE_icon_id(int e_icon_id) {
        this.e_icon_id = e_icon_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBegin_hour() {
        return begin_hour;
    }

    public void setBegin_hour(int begin_hour) {
        this.begin_hour = begin_hour;
    }

    public int getBegin_minute() {
        return begin_minute;
    }

    public void setBegin_minute(int begin_minute) {
        this.begin_minute = begin_minute;
    }

    public int getEnd_hour() {
        return end_hour;
    }

    public void setEnd_hour(int end_hour) {
        this.end_hour = end_hour;
    }

    public int getEnd_minute() {
        return end_minute;
    }

    public void setEnd_minute(int end_minute) {
        this.end_minute = end_minute;
    }

    public int getRedColor() {
        return redColor;
    }

    public void setRedColor(int redColor) {
        this.redColor = redColor;
    }

    public int getGreenColor() {
        return greenColor;
    }

    public void setGreenColor(int greenColor) {
        this.greenColor = greenColor;
    }

    public int getBlueColor() {
        return blueColor;
    }

    public void setBlueColor(int blueColor) {
        this.blueColor = blueColor;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getHabitState() {
        return habitState;
    }

    public void setHabitState(int habitState) {
        this.habitState = habitState;
    }

    public int getE_action() {
        return e_action;
    }

    public void setE_action(int e_action) {
        this.e_action = e_action;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "HabitItem{" +
                "e_icon_id=" + e_icon_id +
                ", id=" + id +
                ", begin_hour=" + begin_hour +
                ", begin_minute=" + begin_minute +
                ", end_hour=" + end_hour +
                ", end_minute=" + end_minute +
                ", redColor=" + redColor +
                ", greenColor=" + greenColor +
                ", blueColor=" + blueColor +
                ", duration=" + duration +
                ", e_action=" + e_action +
                ", content='" + content + '\'' +
                '}';
    }
}

package com.squareup.timessquare;

import java.util.Date;

/**
 * Created by Bernat on 6/11/13.
 */
public class Event {

    private Date date;
    private int color;
    private String text;

    public Event(Date date, int color, String text) {
        this.date = date;
        this.color = color;
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}


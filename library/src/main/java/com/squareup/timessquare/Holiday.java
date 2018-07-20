package com.squareup.timessquare;

import java.util.Calendar;

public class Holiday {

    private Calendar date;
    private String name;

    public Holiday(Calendar date, String name) {
        this.date = date;
        this.name = name;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}


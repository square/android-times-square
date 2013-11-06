package com.squareup.timessquare;

import java.util.Calendar;
import java.util.Date;

/** Event class that allows calendar to highlight specific date. */
public class Event {

  private Date date;
  private int color;
  private int textColor;

  public Event(int backgroundColor, int textColor) {
    this.color = backgroundColor;
    this.textColor = textColor;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(int day, int month, int year) {

    Calendar eventCalendar = Calendar.getInstance();
    eventCalendar.set(Calendar.DAY_OF_MONTH, day);
    eventCalendar.set(Calendar.MONTH, month);
    eventCalendar.set(Calendar.YEAR, year);
    eventCalendar.set(Calendar.AM_PM, Calendar.AM);
    CalendarPickerView.setMidnight(eventCalendar);

    this.date = eventCalendar.getTime();
  }

  public int getColor() {
    return color;
  }

  public void setColor(int color) {
    this.color = color;
  }

  public int getTextColor() {
    return textColor;
  }

  public void setTextColor(int textColor) {
    this.textColor = textColor;
  }

  @Override public String toString() {
    return "Event{"
        + "date="
        + date
        + ", color="
        + color
        + ", textColor="
        + textColor +
        '}';
  }
}


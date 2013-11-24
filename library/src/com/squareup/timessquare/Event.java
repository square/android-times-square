package com.squareup.timessquare;

import java.util.Calendar;
import java.util.Date;

/** Event class that allows calendar to highlight specific date. */
public class Event {

  private Date date;
  private int color;
  private int textColor;

  public Event(Date date, int backgroundColor, int textColor) {
    setDate(date);
    this.color = backgroundColor;
    this.textColor = textColor;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {

    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.set(Calendar.AM_PM, Calendar.AM);
    CalendarPickerView.setMidnight(cal);

    this.date = cal.getTime();
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


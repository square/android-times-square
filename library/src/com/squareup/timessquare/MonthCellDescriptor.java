// Copyright 2012 Square, Inc.
package com.squareup.timessquare;

import java.util.Date;

/** Describes the state of a particular date cell in a {@link MonthView}. */
class MonthCellDescriptor {
  private final Date date;
  private final int value;
  private final boolean isCurrentMonth;
  private boolean isSelected;
  private final boolean isToday;
  private final boolean isSelectable;
  private boolean isPeriodFirst;
  private boolean isPeriodMiddle;
  private boolean isPeriodLast;

  MonthCellDescriptor(Date date, boolean currentMonth, boolean selectable, boolean selected,
      boolean today, int value) {
    this.date = date;
    isCurrentMonth = currentMonth;
    isSelectable = selectable;
    isSelected = selected;
    isToday = today;
    this.value = value;
  }

  public Date getDate() {
    return date;
  }

  public boolean isCurrentMonth() {
    return isCurrentMonth;
  }

  public boolean isSelectable() {
    return isSelectable;
  }

  public boolean isSelected() {
    return isSelected;
  }

  public void setSelected(boolean selected) {
    isSelected = selected;
  }

  public boolean isToday() {
    return isToday;
  }

  public boolean isPeriodFirst() {
    return isPeriodFirst;
  }

  public void setPeriodFirst(boolean periodFirst) {
    isPeriodFirst = periodFirst;
  }

  public boolean isPeriodMiddle() {
    return isPeriodMiddle;
  }

  public void setPeriodMiddle(boolean periodMiddle) {
    isPeriodMiddle = periodMiddle;
  }

  public boolean isPeriodLast() {
    return isPeriodLast;
  }

  public void setPeriodLast(boolean periodLast) {
    isPeriodLast = periodLast;
  }

  public void clearPeriodState() {
    isPeriodFirst = false;
    isPeriodMiddle = false;
    isPeriodLast = false;
  }

  public int getValue() {
    return value;
  }

  @Override public String toString() {
    return "MonthCellDescriptor{"
        + "date="
        + date
        + ", value="
        + value
        + ", isCurrentMonth="
        + isCurrentMonth
        + ", isSelected="
        + isSelected
        + ", isToday="
        + isToday
        + ", isSelectable="
        + isSelectable
        + ", isPeriodFirst="
        + isPeriodFirst
        + ", isPeriodMiddle="
        + isPeriodMiddle
        + ", isPeriodLast="
        + isPeriodLast
        + '}';
  }
}

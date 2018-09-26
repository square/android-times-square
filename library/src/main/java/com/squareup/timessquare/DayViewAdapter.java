package com.squareup.timessquare;

/** Adapter used to provide a layout for {@link CalendarCellView}.*/
public interface DayViewAdapter {
  void makeCellView(CalendarCellView parent);
  void updateCellView(CalendarCellView parent);
}

package com.squareup.timessquare;

/** Adapter used to provide a layout for {@link CalendarCellView}.*/
public interface DayViewAdapter {

  /**
   * Make Cell View method is overriden to create a cell view and add it to the parent so it can
   * be displayed at runtime. This view MUST extend from the Android Text View class to be assured
   * of working correctly.
   *
   * @param parent  The {@link CalendarCellView} Calendar Cell View that holds the current view that
   *                is being created, the new view needs to be added to this parent in order to be
   *                displayed correctly.
   */
  void makeCellView(CalendarCellView parent);
}

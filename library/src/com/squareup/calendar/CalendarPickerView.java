// Copyright 2012 Square, Inc.
package com.squareup.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.util.Calendar.DATE;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MILLISECOND;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.SECOND;
import static java.util.Calendar.SUNDAY;
import static java.util.Calendar.YEAR;

public class CalendarPickerView extends ListView {
  private final CalendarPickerView.MonthAdapter adapter;
  private final DateFormat monthNameFormat;
  private final DateFormat weekdayNameFormat;
  private final DateFormat fullDateFormat;
  final List<MonthDescriptor> months = new ArrayList<MonthDescriptor>();
  final List<List<List<MonthCellDescriptor>>> cells =
      new ArrayList<List<List<MonthCellDescriptor>>>();

  private MonthCellDescriptor selectedCell;
  final Calendar today = Calendar.getInstance();
  private final Calendar selectedCal = Calendar.getInstance();
  private final Calendar minCal = Calendar.getInstance();
  private final Calendar maxCal = Calendar.getInstance();
  private final Calendar monthCounter = Calendar.getInstance();

  private final MonthView.Listener listener = new CellClickedListener();

  public CalendarPickerView(Context context, AttributeSet attrs) {
    super(context, attrs);
    adapter = new MonthAdapter();
    setDivider(null);
    setDividerHeight(0);
    setAdapter(adapter);
    final int bg = getResources().getColor(R.color.calendar_bg);
    setBackgroundColor(bg);
    setCacheColorHint(bg);
    monthNameFormat = new SimpleDateFormat(context.getString(R.string.month_name_format));
    weekdayNameFormat = new SimpleDateFormat(context.getString(R.string.day_name_format));
    fullDateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
  }

  public void init(Date startDate, Date minDate, Date maxDate) {
    if (startDate == null || minDate == null || maxDate == null) {
      throw new IllegalArgumentException(
          "All dates must be non-null.  " + dbg(startDate, minDate, maxDate));
    }
    if (startDate.getTime() == 0 || minDate.getTime() == 0 || maxDate.getTime() == 0) {
      throw new IllegalArgumentException(
          "All dates must be non-zero.  " + dbg(startDate, minDate, maxDate));
    }
    if (minDate.after(maxDate)) {
      throw new IllegalArgumentException(
          "Min date must be before max date.  " + dbg(startDate, minDate, maxDate));
    }

    // Clear previous state.
    cells.clear();
    months.clear();

    // Sanitize input: clear out the hours/minutes/seconds/millis.
    selectedCal.setTime(startDate);
    minCal.setTime(minDate);
    maxCal.setTime(maxDate);
    setMidnight(selectedCal);
    setMidnight(minCal);
    setMidnight(maxCal);

    // Now iterate between minCal and maxCal and build up our list of months to show.
    monthCounter.setTime(minCal.getTime());
    final int maxMonth = maxCal.get(MONTH);
    final int maxYear = maxCal.get(YEAR);
    while ((monthCounter.get(MONTH) <= maxMonth // Up to, including the month.
        || monthCounter.get(YEAR) < maxYear) // Up to the year.
        && monthCounter.get(YEAR) < maxYear + 1) { // But not > next yr.
      MonthDescriptor month = new MonthDescriptor(monthCounter.get(MONTH), monthCounter.get(YEAR),
          monthNameFormat.format(monthCounter.getTime()));
      cells.add(getMonthCells(month, monthCounter, selectedCal));
      Logr.d("Adding month " + month);
      months.add(month);
      monthCounter.add(MONTH, 1);
    }
    adapter.notifyDataSetChanged();
  }

  public long getSelectedDateMillis() {
    return getSelectedDate().getTimeInMillis();
  }

  public Calendar getSelectedDate() {
    return selectedCal;
  }

  /** Returns a string summarizing what the client sent us for init() params. */
  private static String dbg(Date startDate, Date minDate, Date maxDate) {
    return "startDate: " + startDate + "\nminDate: " + minDate + "\nmaxDate: " + maxDate;
  }

  /** Clears out the hours/minutes/seconds/millis of a Calendar. */
  private static void setMidnight(Calendar cal) {
    cal.set(HOUR_OF_DAY, 0);
    cal.set(MINUTE, 0);
    cal.set(SECOND, 0);
    cal.set(MILLISECOND, 0);
  }

  private class CellClickedListener implements MonthView.Listener {
    @Override public void handleClick(MonthCellDescriptor cell) {
      if (!betweenDates(cell.getDate(), minCal, maxCal)) {
        String errMessage =
            getResources().getString(R.string.invalid_date, fullDateFormat.format(minCal.getTime()),
                fullDateFormat.format(maxCal.getTime()));
        Toast.makeText(getContext(), errMessage, Toast.LENGTH_SHORT).show();
      } else {
        // De-select the currently-selected cell.
        selectedCell.setSelected(false);
        // Select the new cell.
        selectedCell = cell;
        selectedCell.setSelected(true);
        // Track the currently selected date value.
        selectedCal.setTime(cell.getDate());
        // Update the adapter.
        adapter.notifyDataSetChanged();
      }
    }
  }

  private class MonthAdapter extends BaseAdapter {
    private final LayoutInflater inflater;

    private MonthAdapter() {
      inflater = LayoutInflater.from(getContext());
    }

    @Override public boolean isEnabled(int position) {
      // Disable selectability: each cell will handle that itself.
      return false;
    }

    @Override public int getCount() {
      return months.size();
    }

    @Override public Object getItem(int position) {
      return months.get(position);
    }

    @Override public long getItemId(int position) {
      return position;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
      MonthView monthView = (MonthView) convertView;
      if (monthView == null) {
        monthView = MonthView.create(parent, inflater, weekdayNameFormat, listener, today);
      }
      monthView.init(months.get(position), cells.get(position));
      return monthView;
    }
  }

  List<List<MonthCellDescriptor>> getMonthCells(MonthDescriptor month, Calendar startCal,
      Calendar selectedDate) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(startCal.getTime());
    List<List<MonthCellDescriptor>> cells = new ArrayList<List<MonthCellDescriptor>>();
    cal.set(DAY_OF_MONTH, 1);
    int firstDayOfWeek = cal.get(DAY_OF_WEEK);
    cal.add(DATE, SUNDAY - firstDayOfWeek);
    while ((cal.get(MONTH) < month.getMonth() + 1 || cal.get(YEAR) < month.getYear()) //
        && cal.get(YEAR) <= month.getYear()) {
      Logr.d("Building week row starting at " + cal.getTime());
      List<MonthCellDescriptor> weekCells = new ArrayList<MonthCellDescriptor>();
      cells.add(weekCells);
      for (int c = 0; c < 7; c++) {
        Date date = cal.getTime();
        boolean isCurrentMonth = cal.get(MONTH) == month.getMonth();
        boolean isSelected = isCurrentMonth && sameDate(cal, selectedDate);
        boolean isSelectable = isCurrentMonth && betweenDates(cal, minCal, maxCal);
        boolean isToday = sameDate(cal, today);
        int value = cal.get(DAY_OF_MONTH);
        MonthCellDescriptor cell =
            new MonthCellDescriptor(date, isCurrentMonth, isSelectable, isSelected, isToday, value);
        if (isSelected) {
          selectedCell = cell;
        }
        weekCells.add(cell);
        cal.add(DATE, 1);
      }
    }
    return cells;
  }

  private static boolean sameDate(Calendar cal, Calendar selectedDate) {
    return cal.get(MONTH) == selectedDate.get(MONTH)
        && cal.get(YEAR) == selectedDate.get(YEAR)
        && cal.get(DAY_OF_MONTH) == selectedDate.get(DAY_OF_MONTH);
  }

  private static boolean betweenDates(Calendar cal, Calendar minCal, Calendar maxCal) {
    final Date date = cal.getTime();
    return betweenDates(date, minCal, maxCal);
  }

  static boolean betweenDates(Date date, Calendar minCal, Calendar maxCal) {
    final Date min = minCal.getTime();
    return (date.equals(min) || date.after(min)) // >= minCal
        && date.before(maxCal.getTime()); // && < maxCal
  }
}

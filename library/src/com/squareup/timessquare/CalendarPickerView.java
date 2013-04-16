// Copyright 2012 Square, Inc.
package com.squareup.timessquare;

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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.util.Calendar.DATE;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MILLISECOND;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.SECOND;
import static java.util.Calendar.YEAR;

/**
 * Android component to allow picking a date from a calendar view (a list of months).  Must be
 * initialized after inflation with {@link #init(java.util.Date, java.util.Date, java.util.Date)}.
 * The currently selected date can be retrieved with {@link #getSelectedDate()}.
 */
public class CalendarPickerView extends ListView {
  private final CalendarPickerView.MonthAdapter adapter;
  private final DateFormat monthNameFormat;
  private final DateFormat weekdayNameFormat;
  private final DateFormat fullDateFormat;
  private boolean multiSelect;
  final List<MonthDescriptor> months = new ArrayList<MonthDescriptor>();
  final List<List<List<MonthCellDescriptor>>> cells =
      new ArrayList<List<List<MonthCellDescriptor>>>();

  private final List<MonthCellDescriptor> selectedCells = new ArrayList<MonthCellDescriptor>();
  final Calendar today = Calendar.getInstance();
  private final List<Calendar> selectedCals = new ArrayList<Calendar>();
  private final Calendar minCal = Calendar.getInstance();
  private final Calendar maxCal = Calendar.getInstance();
  private final Calendar monthCounter = Calendar.getInstance();

  private final MonthView.Listener listener = new CellClickedListener();

  private OnDateSelectedListener dateListener;

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

  /**
   * Gets a value indicating whether the user can select several dates or only
   * a single one.
   *
   * @return true to select mutiple dates, false to select only one date
   */
  public boolean getMultiSelect() {
    return multiSelect;
  }

  /**
   * Sets a value indicating whether the user can select several dates or only
   * a single one.
   *
   * @param value true to select mutiple dates, false to select only one date
   */
  public void setMultiSelect(boolean value) {
    multiSelect = value;
  }

  /**
   * All date parameters must be non-null and their {@link java.util.Date#getTime()} must not
   * return 0.  Time of day will be ignored.  For instance, if you pass in {@code minDate} as
   * 11/16/2012 5:15pm and {@code maxDate} as 11/16/2013 4:30am, 11/16/2012 will be the first
   * selectable date and 11/15/2013 will be the last selectable date ({@code maxDate} is
   * exclusive).
   *
   * @param minDate Earliest selectable date, inclusive.  Must be earlier than {@code maxDate}.
   * @param maxDate Latest selectable date, exclusive.  Must be later than {@code minDate}.
   */
  public void init(Date minDate, Date maxDate) {
    initialize(null, minDate, maxDate);
  }

  /**
   * All date parameters must be non-null and their {@link java.util.Date#getTime()} must not
   * return 0.  Time of day will be ignored.  For instance, if you pass in {@code minDate} as
   * 11/16/2012 5:15pm and {@code maxDate} as 11/16/2013 4:30am, 11/16/2012 will be the first
   * selectable date and 11/15/2013 will be the last selectable date ({@code maxDate} is
   * exclusive).
   *
   * @param selectedDate Initially selected date.  Must be between {@code minDate} and {@code
   * maxDate}.
   * @param minDate Earliest selectable date, inclusive.  Must be earlier than {@code maxDate}.
   * @param maxDate Latest selectable date, exclusive.  Must be later than {@code minDate}.
   */
  public void init(Date selectedDate, Date minDate, Date maxDate) {
    setMultiSelect(false);
    initialize(Arrays.asList(selectedDate), minDate, maxDate);
  }

  /**
   * All date parameters must be non-null and their {@link java.util.Date#getTime()} must not
   * return 0.  Time of day will be ignored.  For instance, if you pass in {@code minDate} as
   * 11/16/2012 5:15pm and {@code maxDate} as 11/16/2013 4:30am, 11/16/2012 will be the first
   * selectable date and 11/15/2013 will be the last selectable date ({@code maxDate} is
   * exclusive).
   *
   * @param selectedDates Initially selected dates.  Must be between {@code minDate} and {@code
   * maxDate}.
   * @param minDate Earliest selectable date, inclusive.  Must be earlier than {@code maxDate}.
   * @param maxDate Latest selectable date, exclusive.  Must be later than {@code minDate}.
   */
  public void init(Iterable<Date> selectedDates, Date minDate, Date maxDate) {
    setMultiSelect(true);
    initialize(selectedDates, minDate, maxDate);
  }

  private void initialize(Iterable<Date> selectedDates, Date minDate, Date maxDate) {
    if (minDate == null || maxDate == null) {
      throw new IllegalArgumentException(
          "minDate and maxDate must be non-null.  " + dbg(selectedDates, minDate, maxDate));
    }
    if (minDate.after(maxDate)) {
      throw new IllegalArgumentException(
          "Min date must be before max date.  " + dbg(selectedDates, minDate, maxDate));
    }
    if (minDate.getTime() == 0 || maxDate.getTime() == 0) {
      throw new IllegalArgumentException(
          "minDate and maxDate must be non-zero.  " + dbg(selectedDates, minDate, maxDate));
    }

    selectedCals.clear();
    selectedCells.clear();
    if (selectedDates != null) {
      for (Date selectedDate : selectedDates) {
        if (selectedDate.getTime() == 0) {
          throw new IllegalArgumentException(
              "Selected date must be non-zero.  " + dbg(selectedDates, minDate, maxDate));
        }

        if (selectedDate.before(minDate) || selectedDate.after(maxDate)) {
          throw new IllegalArgumentException(
              "selectedDate must be between minDate and maxDate.  " + dbg(selectedDates, minDate,
                  maxDate));
        }

        Calendar selectedCal = Calendar.getInstance();
        selectedCals.add(selectedCal);
        // Sanitize input: clear out the hours/minutes/seconds/millis.
        selectedCal.setTime(selectedDate);
        setMidnight(selectedCal);
      }
    }

    // Clear previous state.
    cells.clear();
    months.clear();
    minCal.setTime(minDate);
    maxCal.setTime(maxDate);
    setMidnight(minCal);
    setMidnight(maxCal);

    // maxDate is exclusive: bump back to the previous day so if maxDate is the first of a month,
    // we don't accidentally include that month in the view.
    maxCal.add(MINUTE, -1);

    // Now iterate between minCal and maxCal and build up our list of months to show.
    monthCounter.setTime(minCal.getTime());
    final int maxMonth = maxCal.get(MONTH);
    final int maxYear = maxCal.get(YEAR);
    int selectedIndex = 0;
    int todayIndex = 0;
    Calendar today = Calendar.getInstance();
    while ((monthCounter.get(MONTH) <= maxMonth // Up to, including the month.
        || monthCounter.get(YEAR) < maxYear) // Up to the year.
        && monthCounter.get(YEAR) < maxYear + 1) { // But not > next yr.
      MonthDescriptor month = new MonthDescriptor(monthCounter.get(MONTH), monthCounter.get(YEAR),
          monthNameFormat.format(monthCounter.getTime()));
      cells.add(getMonthCells(month, monthCounter));
      Logr.d("Adding month %s", month);
      if (selectedIndex == 0) {
        for (Calendar selectedCal : selectedCals) {
          if (sameMonth(selectedCal, month)) {
            selectedIndex = months.size();
            break;
          }
        }
        if (selectedIndex == 0 && todayIndex == 0 && sameMonth(today, month)) {
          todayIndex = months.size();
        }
      }
      months.add(month);
      monthCounter.add(MONTH, 1);
    }

    adapter.notifyDataSetChanged();
    if (selectedIndex != 0 || todayIndex != 0) {
      scrollToSelectedMonth(selectedIndex != 0 ? selectedIndex : todayIndex);
    }
  }

  private void scrollToSelectedMonth(final int selectedIndex) {
    post(new Runnable() {
      @Override public void run() {
        smoothScrollToPosition(selectedIndex);
      }
    });
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    if (months.isEmpty()) {
      throw new IllegalStateException(
          "Must have at least one month to display.  Did you forget to call init()?");
    }
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  }

  public Date getSelectedDate() {
    return (selectedCals.size() > 0 ? selectedCals.get(0).getTime() : null);
  }

  public Iterable<Date> getSelectedDates() {
    List<Date> selectedDates = new ArrayList<Date>();
    for (Calendar cal : selectedCals) {
      selectedDates.add(cal.getTime());
    }
    return selectedDates;
  }

  /** Returns a string summarizing what the client sent us for init() params. */
  private static String dbg(Iterable<Date> selectedDates, Date minDate, Date maxDate) {
    String dbgString = "minDate: " + minDate + "\nmaxDate: " + maxDate;
    if (selectedDates == null) {
      dbgString += "\nselectedDates: null";
    } else {
      dbgString += "\nselectedDates: ";
      for (Date selectedDate : selectedDates) {
        dbgString += selectedDate + "; ";
      }
    }
    return dbgString;
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
        Date selectedDate = cell.getDate();
        Calendar selectedCal = Calendar.getInstance();
        selectedCal.setTime(selectedDate);

        if (getMultiSelect()) {
          for (MonthCellDescriptor selectedCell : selectedCells) {
            if (selectedCell.getDate().equals(selectedDate)) {
              // De-select the currently-selected cell.
              selectedCell.setSelected(false);
              selectedCells.remove(selectedCell);
              selectedDate = null;
              break;
            }
          }
          for (Calendar cal : selectedCals) {
            if (sameDate(cal, selectedCal)) {
              selectedCals.remove(cal);
              break;
            }
          }
        } else {
          for (MonthCellDescriptor selectedCell : selectedCells) {
            // De-select the currently-selected cell.
            selectedCell.setSelected(false);
          }
          selectedCells.clear();
          selectedCals.clear();
        }

        if (selectedDate != null) {
          // Select a new cell
          selectedCells.add(cell);
          cell.setSelected(true);
          selectedCals.add(selectedCal);
        }

        // Update the adapter.
        adapter.notifyDataSetChanged();

        if (selectedDate != null && dateListener != null) {
          dateListener.onDateSelected(selectedDate);
        }
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

  List<List<MonthCellDescriptor>> getMonthCells(MonthDescriptor month, Calendar startCal) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(startCal.getTime());
    List<List<MonthCellDescriptor>> cells = new ArrayList<List<MonthCellDescriptor>>();
    cal.set(DAY_OF_MONTH, 1);
    int firstDayOfWeek = cal.get(DAY_OF_WEEK);
    cal.add(DATE, cal.getFirstDayOfWeek() - firstDayOfWeek);
    while ((cal.get(MONTH) < month.getMonth() + 1 || cal.get(YEAR) < month.getYear()) //
        && cal.get(YEAR) <= month.getYear()) {
      Logr.d("Building week row starting at %s", cal.getTime());
      List<MonthCellDescriptor> weekCells = new ArrayList<MonthCellDescriptor>();
      cells.add(weekCells);
      for (int c = 0; c < 7; c++) {
        Date date = cal.getTime();
        boolean isCurrentMonth = cal.get(MONTH) == month.getMonth();
        boolean isSelected = isCurrentMonth && containsDate(selectedCals, cal);
        boolean isSelectable = isCurrentMonth && betweenDates(cal, minCal, maxCal);
        boolean isToday = sameDate(cal, today);
        int value = cal.get(DAY_OF_MONTH);
        MonthCellDescriptor cell =
            new MonthCellDescriptor(date, isCurrentMonth, isSelectable, isSelected, isToday, value);
        if (isSelected) {
          selectedCells.add(cell);
        }
        weekCells.add(cell);
        cal.add(DATE, 1);
      }
    }
    return cells;
  }

  private static boolean containsDate(Iterable<Calendar> selectedCals, Calendar cal) {
    for (Calendar selectedCal : selectedCals) {
      if (sameDate(cal, selectedCal)) {
        return true;
      }
    }
    return false;
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

  private static boolean sameMonth(Calendar cal, MonthDescriptor month) {
    return (cal.get(MONTH) == month.getMonth() && cal.get(YEAR) == month.getYear());
  }

  public void setOnDateSelectedListener(OnDateSelectedListener listener) {
    dateListener = listener;
  }

  public interface OnDateSelectedListener {
    void onDateSelected(Date date);
  }
}

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
import java.util.Collections;
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
import static java.util.Calendar.YEAR;

/**
 * Android component to allow picking a date from a calendar view (a list of months).  Must be
 * initialized after inflation with one of the init() methods.  The currently selected date can be
 * retrieved with {@link #getSelectedDate()}.
 */
public class CalendarPickerView extends ListView {
  public enum SelectionMode {
    /** Only one date will be selectable. */
    SINGLE,
    /** Multiple dates will be selectable. */
    MULTIPLE,
    /** Up to (and no more than) two dates will be selectable. */
    PERIOD,
    /** Like {@link #PERIOD} but also selects all dates in between the two selected dates. */
    SELECTED_PERIOD
  }

  private final CalendarPickerView.MonthAdapter adapter;
  private final DateFormat monthNameFormat;
  private final DateFormat weekdayNameFormat;
  private final DateFormat fullDateFormat;
  SelectionMode selectionMode;
  final List<MonthDescriptor> months = new ArrayList<MonthDescriptor>();
  final List<MonthCellDescriptor> selectedCells = new ArrayList<MonthCellDescriptor>();
  final Calendar today = Calendar.getInstance();
  private final List<List<List<MonthCellDescriptor>>> cells =
      new ArrayList<List<List<MonthCellDescriptor>>>();
  private final List<Calendar> selectedCals = new ArrayList<Calendar>();
  private final Calendar minCal = Calendar.getInstance();
  private final Calendar maxCal = Calendar.getInstance();
  private final Calendar monthCounter = Calendar.getInstance();

  private final MonthView.Listener listener = new CellClickedListener();

  private OnDateSelectedListener dateListener;
  private DateSelectableFilter dateConfiguredListener;

  private OnInvalidDateSelectedListener invalidDateListener =
      new DefaultOnInvalidDateSelectedListener();

  public CalendarPickerView(Context context, AttributeSet attrs) {
    super(context, attrs);
    adapter = new MonthAdapter();
    setDivider(null);
    setDividerHeight(0);
    final int bg = getResources().getColor(R.color.calendar_bg);
    setBackgroundColor(bg);
    setCacheColorHint(bg);
    monthNameFormat = new SimpleDateFormat(context.getString(R.string.month_name_format));
    weekdayNameFormat = new SimpleDateFormat(context.getString(R.string.day_name_format));
    fullDateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);

    if (isInEditMode()) {
      Calendar nextYear = Calendar.getInstance();
      nextYear.add(Calendar.YEAR, 1);

      init(new Date(), new Date(), nextYear.getTime());
    }
  }

  /**
   * All date parameters must be non-null and their {@link Date#getTime()} must not return 0.  Time
   * of day will be ignored.  For instance, if you pass in {@code minDate} as 11/16/2012 5:15pm and
   * {@code maxDate} as 11/16/2013 4:30am, 11/16/2012 will be the first selectable date and
   * 11/15/2013 will be the last selectable date ({@code maxDate} is exclusive).
   * <p/>
   * This will implicitly set the {@link SelectionMode} to {@link SelectionMode#SINGLE}.  If you
   * want a different selection mode, use {@link #init(SelectionMode, List, Date, Date)}.
   *
   * @param selectedDate Initially selected date.  Must be between {@code minDate} and {@code
   * maxDate}.
   * @param minDate Earliest selectable date, inclusive.  Must be earlier than {@code maxDate}.
   * @param maxDate Latest selectable date, exclusive.  Must be later than {@code minDate}.
   */
  public void init(Date selectedDate, Date minDate, Date maxDate) {
    selectionMode = SelectionMode.SINGLE;
    initialize(Arrays.asList(selectedDate), minDate, maxDate);
  }

  /**
   * All date parameters must be non-null and their {@link Date#getTime()} must not return 0.  Time
   * of day will be ignored.  For instance, if you pass in {@code minDate} as 11/16/2012 5:15pm and
   * {@code maxDate} as 11/16/2013 4:30am, 11/16/2012 will be the first selectable date and
   * 11/15/2013 will be the last selectable date ({@code maxDate} is exclusive).
   *
   * @param minDate Earliest selectable date, inclusive.  Must be earlier than {@code maxDate}.
   * @param maxDate Latest selectable date, exclusive.  Must be later than {@code minDate}.
   */
  public void init(SelectionMode selectionMode, Date minDate, Date maxDate) {
    this.selectionMode = selectionMode;
    initialize(null, minDate, maxDate);
  }

  /**
   * All date parameters must be non-null and their {@link Date#getTime()} must not return 0.  Time
   * of day will be ignored.  For instance, if you pass in {@code minDate} as 11/16/2012 5:15pm and
   * {@code maxDate} as 11/16/2013 4:30am, 11/16/2012 will be the first selectable date and
   * 11/15/2013 will be the last selectable date ({@code maxDate} is exclusive).
   *
   * @param selectedDates Initially selected dates.  Must be between {@code minDate} and {@code
   * maxDate}.
   * @param minDate Earliest selectable date, inclusive.  Must be earlier than {@code maxDate}.
   * @param maxDate Latest selectable date, exclusive.  Must be later than {@code minDate}.
   */
  public void init(SelectionMode selectionMode, List<Date> selectedDates, Date minDate,
      Date maxDate) {
    this.selectionMode = selectionMode;
    initialize(selectedDates, minDate, maxDate);
  }

  private void initialize(List<Date> selectedDates, Date minDate, Date maxDate) {
    if (getAdapter() == null) {
        setAdapter(adapter);
    }
    if (selectionMode == SelectionMode.SINGLE && selectedDates.size() > 1) {
      throw new IllegalArgumentException("SINGLE mode cannot be used with multiple selectedDates");
    }
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
        if (selectedDate == null) {
          throw new IllegalArgumentException(
              "Selected date must be non-null.  " + dbg(selectedDates, minDate, maxDate));
        }
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
      @Override
      public void run() {
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

  public List<Date> getSelectedDates() {
    List<Date> selectedDates = new ArrayList<Date>();
    for (Calendar cal : selectedCals) {
      selectedDates.add(cal.getTime());
    }
    if (selectionMode == SelectionMode.SELECTED_PERIOD) {
      // Add all days in the period.
      for (int i = 2; i < selectedCells.size(); i++) {
        selectedDates.add(selectedCells.get(i).getDate());
      }
    }
    Collections.sort(selectedDates);
    return selectedDates;
  }

  /** Returns a string summarizing what the client sent us for init() params. */
  private static String dbg(List<Date> selectedDates, Date minDate, Date maxDate) {
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
      Date clickedDate = cell.getDate();

      if (!betweenDates(clickedDate, minCal, maxCal) || !isDateSelectable(clickedDate)) {
        if (invalidDateListener != null) {
          invalidDateListener.onInvalidDateSelected(clickedDate);
        }
      } else {
        boolean wasSelected = doSelectDate(clickedDate, cell);

        if (wasSelected && dateListener != null) {
          dateListener.onDateSelected(clickedDate);
        }
      }
    }
  }

  /**
   * Select a new date.  Respects the {@link SelectionMode} this CalendarPickerView is configured
   * with: if you are in {@link SelectionMode#SINGLE}, the previously selected date will be
   * un-selected.  In {@link SelectionMode#MULTIPLE}, the new date will be added to the list of
   * selected dates.  TODO figure out the behavior in {@link SelectionMode#PERIOD} and
   * TODO {@link SelectionMode#SELECTED_PERIOD} and document here.  Write tests for this.
   * </p>
   * If the selection was made (selectable date, in range), the view will scroll to the newly
   * selected date if it's not already visible.
   *
   * @return - whether we were able to set the date
   */
  public boolean selectDate(Date date) {
    MonthCellWithMonthIndex monthCellWithMonthIndex = getMonthCellWithIndexByDate(date);
    if (monthCellWithMonthIndex == null || !isDateSelectable(date)) {
      return false;
    }
    boolean wasSelected = doSelectDate(date, monthCellWithMonthIndex.cell);
    if (wasSelected) {
      scrollToSelectedMonth(monthCellWithMonthIndex.monthIndex);
    }
    return wasSelected;
  }

  private boolean doSelectDate(Date date, MonthCellDescriptor cell) {
    Calendar selectedCal = Calendar.getInstance();
    selectedCal.setTime(date);

    switch (selectionMode) {
      case SELECTED_PERIOD: {
        // Clear additionally selected cells (Cals were not selected).
        while (selectedCells.size() > 2) {
          selectedCells.get(2).setSelected(false);
          selectedCells.remove(2);
        }
      }
      // NOTE: there is no break here.  This code falls through and runs the normal PERIOD code.
      case PERIOD:
        // Keep cell selected if this was both start and end date.
        if (selectedCals.size() >= 2) {
          if (selectedCals.get(0).compareTo(selectedCals.get(1)) != 0) {
            if (selectedCals.size() != selectedCells.size()) {
              throw new IllegalStateException(
                  String.format("Mismatched number of selected cells (%d)/dates (%d)",
                      selectedCells.size(), selectedCals.size()));
            }
            selectedCells.get(0).setSelected(false);
            selectedCells.remove(0);
          }
          selectedCals.remove(0);
          if (selectedCals.size() != selectedCells.size()) {
            throw new IllegalStateException(
                String.format("Mismatched number of selected cells (%d)/dates (%d) after removal",
                    selectedCells.size(), selectedCals.size()));
          }
        }
        break;

      case MULTIPLE:
        date = applyMultiSelect(date, selectedCal);
        break;

      case SINGLE:
        for (MonthCellDescriptor selectedCell : selectedCells) {
          // De-select the currently-selected cell.
          selectedCell.setSelected(false);
        }
        selectedCells.clear();
        selectedCals.clear();
        break;
      default:
        throw new IllegalStateException("Unknown selectionMode " + selectionMode);
    }

    if (date != null) {
      // Select a new cell.
      if (selectedCells.size() == 0 || !selectedCells.get(0).equals(cell)) {
        selectedCells.add(cell);
        cell.setSelected(true);
      }
      selectedCals.add(selectedCal);

      if (selectionMode == SelectionMode.SELECTED_PERIOD && selectedCells.size() > 1) {
        // Select all days in between start and end.
        Date start;
        Date end;
        if (selectedCells.get(0).getDate().after(selectedCells.get(1).getDate())) {
          start = selectedCells.get(1).getDate();
          end = selectedCells.get(0).getDate();
        } else {
          start = selectedCells.get(0).getDate();
          end = selectedCells.get(1).getDate();
        }

        for (List<List<MonthCellDescriptor>> month : cells) {
          for (List<MonthCellDescriptor> week : month) {
            for (MonthCellDescriptor singleCell : week) {
              if (singleCell.getDate().after(start)
                  && singleCell.getDate().before(end)
                  && singleCell.isSelectable()) {
                singleCell.setSelected(true);
                selectedCells.add(singleCell);
              }
            }
          }
        }
      }
    }

    // Update the adapter.
    adapter.notifyDataSetChanged();
    return date != null;
  }

  private Date applyMultiSelect(Date date, Calendar selectedCal) {
    for (MonthCellDescriptor selectedCell : selectedCells) {
      if (selectedCell.getDate().equals(date)) {
        // De-select the currently-selected cell.
        selectedCell.setSelected(false);
        selectedCells.remove(selectedCell);
        date = null;
        break;
      }
    }
    for (Calendar cal : selectedCals) {
      if (sameDate(cal, selectedCal)) {
        selectedCals.remove(cal);
        break;
      }
    }
    return date;
  }

  /** Hold a cell with a month-index. */
  private static class MonthCellWithMonthIndex {
    public MonthCellDescriptor cell;
    public int monthIndex;

    public MonthCellWithMonthIndex(MonthCellDescriptor cell, int monthIndex) {
      this.cell = cell;
      this.monthIndex = monthIndex;
    }
  }

  /** Return cell and month-index (for scrolling) for a given Date. */
  private MonthCellWithMonthIndex getMonthCellWithIndexByDate(Date date) {
    int index = 0;
    Calendar searchCal = Calendar.getInstance();
    searchCal.setTime(date);
    Calendar actCal = Calendar.getInstance();

    for (List<List<MonthCellDescriptor>> monthCells : cells) {
      for (List<MonthCellDescriptor> weekCells : monthCells) {
        for (MonthCellDescriptor actCell : weekCells) {
          actCal.setTime(actCell.getDate());
          if (sameDate(actCal, searchCal) && actCell.isSelectable()) {
            return new MonthCellWithMonthIndex(actCell, index);
          }
        }
      }
      index++;
    }
    return null;
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
        boolean isSelectable =
            isCurrentMonth && betweenDates(cal, minCal, maxCal) && isDateSelectable(date);
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

  private static boolean containsDate(List<Calendar> selectedCals, Calendar cal) {
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

  private boolean isDateSelectable(Date date) {
    if (dateConfiguredListener == null) {
      return true;
    }
    return dateConfiguredListener.isDateSelectable(date);
  }

  public void setOnDateSelectedListener(OnDateSelectedListener listener) {
    dateListener = listener;
  }

  /**
   * Set a listener to react to user selection of a disabled date.
   *
   * @param listener the listener to set, or null for no reaction
   */
  public void setOnInvalidDateSelectedListener(OnInvalidDateSelectedListener listener) {
    invalidDateListener = listener;
  }

  /**
   * Set a listener used to discriminate between selectable and unselectable dates. Set this to
   * disable arbitrary dates as they are rendered.
   * <p/>
   * Important: set this before you call one of the init() methods.  If called after init(), it
   * will not be consistently applied.
   */
  public void setDateSelectableFilter(DateSelectableFilter listener) {
    dateConfiguredListener = listener;
  }

  /**
   * Interface to be notified when a new date is selected.  This will only be called when the user
   * initiates the date selection.  If you call {@link #selectDate(Date)} this listener will not be
   * notified.
   * <p/>
   * See {@link #setOnDateSelectedListener(OnDateSelectedListener)}.
   */
  public interface OnDateSelectedListener {
    void onDateSelected(Date date);
  }

  /**
   * Interface to be notified when an invalid date is selected by the user. This will only be
   * called when the user initiates the date selection. If you call {@link #selectDate(Date)} this
   * listener will not be notified.
   * <p/>
   * See {@link #setOnInvalidDateSelectedListener(OnInvalidDateSelectedListener)}.
   */
  public interface OnInvalidDateSelectedListener {
    void onInvalidDateSelected(Date date);
  }

  /**
   * Interface used for determining the selectability of a date cell when it is configured for
   * display on the calendar.
   * <p/>
   * See {@link #setDateSelectableFilter(DateSelectableFilter)}.
   */
  public interface DateSelectableFilter {
    boolean isDateSelectable(Date date);
  }

  private class DefaultOnInvalidDateSelectedListener implements OnInvalidDateSelectedListener {
    @Override public void onInvalidDateSelected(Date date) {
      String errMessage =
          getResources().getString(R.string.invalid_date, fullDateFormat.format(minCal.getTime()),
              fullDateFormat.format(maxCal.getTime()));
      Toast.makeText(getContext(), errMessage, Toast.LENGTH_SHORT).show();
    }
  }
}

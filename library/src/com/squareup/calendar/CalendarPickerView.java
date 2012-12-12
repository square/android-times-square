package com.squareup.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
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
import static java.util.Calendar.MONTH;
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

  private Date startDate;
  Date minDate;
  Date maxDate;
  private Calendar selectedCal;
  private MonthCellDescriptor selectedCell;
  Calendar today;
  private MonthView.Listener listener = new CellClickedListener();

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
    selectedCal = Calendar.getInstance();
    today = Calendar.getInstance();
  }

  public void init(Date startDate, Date minDate, Date maxDate) {
    this.startDate = startDate;
    selectedCal.setTime(startDate);
    setDates(startDate, minDate, maxDate);
  }

  void setDates(Date startDate, Date minDate, Date maxDate) {
    if (startDate == null || minDate == null || maxDate == null) {
      throw new IllegalArgumentException("All dates must be non-null.  " + toString());
    }
    if (startDate.getTime() == 0 || minDate.getTime() == 0 || maxDate.getTime() == 0) {
      throw new IllegalArgumentException("All dates must be non-zero.  " + toString());
    }
    if (minDate.after(maxDate)) {
      throw new IllegalArgumentException("Min date must be before max date.  " + toString());
    }
    this.minDate = minDate;
    this.maxDate = maxDate;
    Calendar curMonth = Calendar.getInstance();
    curMonth.setTime(minDate);
    Calendar maxCal = Calendar.getInstance();
    maxCal.setTime(maxDate);
    final int maxMonth = maxCal.get(MONTH);
    final int maxYear = maxCal.get(YEAR);
    while ((curMonth.get(MONTH) <= maxMonth // Up to, including the month.
        || curMonth.get(YEAR) < maxYear) // Up to the year.
        && curMonth.get(YEAR) < maxYear + 1) { // But not > next yr.
      MonthDescriptor month = new MonthDescriptor(curMonth.get(MONTH), curMonth.get(YEAR),
          monthNameFormat.format(curMonth.getTime()));
      cells.add(getMonthCells(month, curMonth, selectedCal));
      Log.d("CalendarPicker", "Adding month " + month);
      months.add(month);
      curMonth.add(MONTH, 1);
    }
    adapter.notifyDataSetChanged();
  }

  public long getSelectedDateMillis() {
    return getSelectedDate().getTimeInMillis();
  }

  public Calendar getSelectedDate() {
    return selectedCal;
  }

  private class CellClickedListener implements MonthView.Listener {
    @Override public void handleClick(MonthCellDescriptor cell) {
      if (!betweenDates(cell.getDate(), minDate, maxDate)) {
        String errMessage =
            getResources().getString(R.string.invalid_date, fullDateFormat.format(minDate),
                fullDateFormat.format(maxDate));
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

  @Override public String toString() {
    return "CalendarPickerView{"
        + "startDate="
        + startDate
        + ", minDate="
        + minDate
        + ", maxDate="
        + maxDate
        + '}';
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
      if (convertView == null) {
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
      Log.d("CalendarPicker", "Building week row starting at " + cal.getTime());
      List<MonthCellDescriptor> weekCells = new ArrayList<MonthCellDescriptor>();
      cells.add(weekCells);
      for (int c = 0; c < 7; c++) {
        Date date = cal.getTime();
        boolean isCurrentMonth = cal.get(MONTH) == month.getMonth();
        boolean isSelected = isCurrentMonth && sameDate(cal, selectedDate);
        boolean isSelectable = isCurrentMonth && betweenDates(cal, minDate, maxDate);
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

  private static boolean betweenDates(Calendar cal, Date minDate, Date maxDate) {
    final Date date = cal.getTime();
    return betweenDates(date, minDate, maxDate);
  }

  private static boolean sameDate(Calendar cal, Calendar selectedDate) {
    return cal.get(MONTH) == selectedDate.get(MONTH)
        && cal.get(YEAR) == selectedDate.get(YEAR)
        && cal.get(DAY_OF_MONTH) == selectedDate.get(DAY_OF_MONTH);
  }

  static boolean betweenDates(Date date, Date minDate, Date maxDate) {
    return (date.equals(minDate) || date.after(minDate)) // >= minDate
        && date.before(maxDate); // && < maxDate
  }
}

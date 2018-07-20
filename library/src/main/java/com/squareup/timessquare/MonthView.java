// Copyright 2012 Square, Inc.
package com.squareup.timessquare;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MonthView extends LinearLayout {
  TextView title;
  CalendarGridView grid;
  View dayNamesHeaderRowView;
  private Listener listener;
  private List<CalendarCellDecorator> decorators;
  private boolean isRtl;
  private Locale locale;
  private boolean alwaysDigitNumbers;

  public static MonthView create(ViewGroup parent, LayoutInflater inflater,
      DateFormat weekdayNameFormat, Listener listener, Calendar today, int dividerColor,
      int dayBackgroundResId, int dayTextColorResId, int titleTextStyle, boolean displayHeader,
      int headerTextColor, boolean showDayNamesHeaderRowView, Locale locale,
      boolean showAlwaysDigitNumbers, DayViewAdapter adapter) {
    return create(parent, inflater, weekdayNameFormat, listener, today, dividerColor,
        dayBackgroundResId, dayTextColorResId, titleTextStyle, displayHeader, headerTextColor,
        showDayNamesHeaderRowView, showAlwaysDigitNumbers, null, locale, adapter);
  }

  public static MonthView create(ViewGroup parent, LayoutInflater inflater,
      DateFormat weekdayNameFormat, Listener listener, Calendar today, int dividerColor,
      int dayBackgroundResId, int dayTextColorResId, int titleTextStyle, boolean displayHeader,
      int headerTextColor, boolean displayDayNamesHeaderRowView, boolean showAlwaysDigitNumbers,
      List<CalendarCellDecorator> decorators, Locale locale, DayViewAdapter adapter) {
    final MonthView view = (MonthView) inflater.inflate(R.layout.month, parent, false);

    // Set the views
    view.title = new TextView(new ContextThemeWrapper(view.getContext(), titleTextStyle));
    view.grid = (CalendarGridView) view.findViewById(R.id.calendar_grid);
    view.dayNamesHeaderRowView = view.findViewById(R.id.day_names_header_row);

    // Add the month title as the first child of MonthView
    view.addView(view.title, 0);

    view.setDayViewAdapter(adapter);
    view.setDividerColor(dividerColor);
    view.setDayTextColor(dayTextColorResId);
    view.setDisplayHeader(displayHeader);
    view.setHeaderTextColor(headerTextColor);

    if (dayBackgroundResId != 0) {
      view.setDayBackground(dayBackgroundResId);
    }

    view.isRtl = isRtl(locale);
    view.locale = locale;
    view.alwaysDigitNumbers = showAlwaysDigitNumbers;
    int firstDayOfWeek = today.getFirstDayOfWeek();
    final CalendarRowView headerRow = (CalendarRowView) view.grid.getChildAt(0);

    if (displayDayNamesHeaderRowView) {
      final int originalDayOfWeek = today.get(Calendar.DAY_OF_WEEK);
      for (int offset = 0; offset < 7; offset++) {
        today.set(Calendar.DAY_OF_WEEK, getDayOfWeek(firstDayOfWeek, offset, view.isRtl));
        final TextView textView = (TextView) headerRow.getChildAt(offset);
        textView.setText(weekdayNameFormat.format(today.getTime()));
      }
      today.set(Calendar.DAY_OF_WEEK, originalDayOfWeek);
    } else {
      view.dayNamesHeaderRowView.setVisibility(View.GONE);
    }

    view.listener = listener;
    view.decorators = decorators;
    return view;
  }

  private static int getDayOfWeek(int firstDayOfWeek, int offset, boolean isRtl) {
    int dayOfWeek = firstDayOfWeek + offset;
    if (isRtl) {
      return 8 - dayOfWeek;
    }
    return dayOfWeek;
  }

  private static boolean isRtl(Locale locale) {
    // TODO convert the build to gradle and use getLayoutDirection instead of this (on 17+)?
    final int directionality = Character.getDirectionality(locale.getDisplayName(locale).charAt(0));
    return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT
        || directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
  }

  public MonthView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public void setDecorators(List<CalendarCellDecorator> decorators) {
    this.decorators = decorators;
  }

  public List<CalendarCellDecorator> getDecorators() {
    return decorators;
  }

  public void init(MonthDescriptor month, List<List<MonthCellDescriptor>> cells,
      boolean displayOnly, Typeface titleTypeface, Typeface dateTypeface) {
    Logr.d("Initializing MonthView (%d) for %s", System.identityHashCode(this), month);
    long start = System.currentTimeMillis();
    title.setText(month.getLabel());
    NumberFormat numberFormatter;
    if (alwaysDigitNumbers) {
      numberFormatter = NumberFormat.getInstance(Locale.US);
    } else {
      numberFormatter = NumberFormat.getInstance(locale);
    }

    final int numRows = cells.size();
    grid.setNumRows(numRows);
    for (int i = 0; i < 6; i++) {
      CalendarRowView weekRow = (CalendarRowView) grid.getChildAt(i + 1);
      weekRow.setListener(listener);
      if (i < numRows) {
        weekRow.setVisibility(VISIBLE);
        List<MonthCellDescriptor> week = cells.get(i);
        for (int c = 0; c < week.size(); c++) {
          MonthCellDescriptor cell = week.get(isRtl ? 6 - c : c);
          CalendarCellView cellView = (CalendarCellView) weekRow.getChildAt(c);

          String cellDate = numberFormatter.format(cell.getValue());
          if (!cellView.getDayOfMonthTextView().getText().equals(cellDate)) {
            cellView.getDayOfMonthTextView().setText(cellDate);
          }
          cellView.setEnabled(cell.isCurrentMonth());
          cellView.setClickable(!displayOnly);

          cellView.setSelectable(cell.isSelectable());
          cellView.setSelected(cell.isSelected());
          cellView.setCurrentMonth(cell.isCurrentMonth());
          cellView.setToday(cell.isToday());
          cellView.setHoliday(cell.isHoliday());
          cellView.setRangeState(cell.getRangeState());
          cellView.setHighlighted(cell.isHighlighted());
          cellView.setTag(cell);

          if (null != decorators) {
            for (CalendarCellDecorator decorator : decorators) {
              decorator.decorate(cellView, cell.getDate());
            }
          }
        }
      } else {
        weekRow.setVisibility(GONE);
      }
    }

    if (titleTypeface != null) {
      title.setTypeface(titleTypeface);
    }
    if (dateTypeface != null) {
      grid.setTypeface(dateTypeface);
    }

    Logr.d("MonthView.init took %d ms", System.currentTimeMillis() - start);
  }

  public void setDividerColor(int color) {
    grid.setDividerColor(color);
  }

  public void setDayBackground(int resId) {
    grid.setDayBackground(resId);
  }

  public void setDayTextColor(int resId) {
    grid.setDayTextColor(resId);
  }

  public void setDayViewAdapter(DayViewAdapter adapter) {
    grid.setDayViewAdapter(adapter);
  }

  public void setDisplayHeader(boolean displayHeader) {
    grid.setDisplayHeader(displayHeader);
  }

  public void setHeaderTextColor(int color) {
    grid.setHeaderTextColor(color);
  }

  public interface Listener {
    void handleClick(MonthCellDescriptor cell);
  }
}

package com.squareup.timessquare;

import android.view.ContextThemeWrapper;
import android.widget.FrameLayout;
import android.widget.TextView;

import static android.view.Gravity.CENTER_VERTICAL;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class DefaultDayViewAdapter implements DayViewAdapter {
  @Override public void makeCellView(CalendarCellView parent) {
    TextView textView = new TextView(
        new ContextThemeWrapper(parent.getContext(), R.style.CalendarCell_CalendarDate));
    textView.setDuplicateParentStateEnabled(true);

    parent.addView(textView,
        new FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT, CENTER_VERTICAL));
    parent.setDayOfMonthTextView(textView);
  }

  @Override
  public void updateCellView(CalendarCellView parent) { }
}

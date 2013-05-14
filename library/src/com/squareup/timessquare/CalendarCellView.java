// Copyright 2013 Square, Inc.
package com.squareup.timessquare;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class CalendarCellView extends TextView {

  private static final int[] STATE_SELECTABLE = {
      R.attr.state_selectable
  };
  private static final int[] STATE_CURRENT_MONTH = {
      R.attr.state_current_month
  };
  private static final int[] STATE_TODAY = {
      R.attr.state_today
  };

  private boolean isSelectable = false;
  private boolean isCurrentMonth = false;
  private boolean isToday = false;

  public CalendarCellView(Context context) {
    super(context);
  }

  public CalendarCellView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public CalendarCellView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  public void setSelectable(boolean isSelectable) {
    this.isSelectable = isSelectable;
    refreshDrawableState();
  }

  public void setCurrentMonth(boolean isCurrentMonth) {
    this.isCurrentMonth = isCurrentMonth;
    refreshDrawableState();
  }

  public void setToday(boolean isToday) {
    this.isToday = isToday;
    refreshDrawableState();
  }

  @Override protected int[] onCreateDrawableState(int extraSpace) {
    final int[] drawableState = super.onCreateDrawableState(extraSpace + 4);

    if (isSelectable) {
      mergeDrawableStates(drawableState, STATE_SELECTABLE);
    }

    if (isCurrentMonth) {
      mergeDrawableStates(drawableState, STATE_CURRENT_MONTH);
    }

    if (isToday) {
      mergeDrawableStates(drawableState, STATE_TODAY);
    }

    return drawableState;
  }
}

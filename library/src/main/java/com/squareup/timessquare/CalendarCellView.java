// Copyright 2013 Square, Inc.

package com.squareup.timessquare;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.widget.TextView;
import com.squareup.timessquare.MonthCellDescriptor.RangeState;

public class CalendarCellView extends TextView {

  private final int[] STATE_SELECTABLE;
  private final int[] STATE_CURRENT_MONTH;
  private final int[] STATE_TODAY;
  private final int[] STATE_HIGHLIGHTED;
  private final int[] STATE_RANGE_FIRST;
  private final int[] STATE_RANGE_MIDDLE;
  private final int[] STATE_RANGE_LAST;


  private boolean isSelectable = false;
  private boolean isCurrentMonth = false;
  private boolean isToday = false;
  private boolean isHighlighted = false;
  private RangeState rangeState = RangeState.NONE;

  @SuppressWarnings("UnusedDeclaration") //
  public CalendarCellView(Context context, AttributeSet attrs) {
    super(context, attrs);
    Resources res = getResources();
    STATE_SELECTABLE = getAttrIntArray("tsquare_state_selectable");
    STATE_CURRENT_MONTH = getAttrIntArray("tsquare_state_current_month");
    STATE_TODAY = getAttrIntArray("tsquare_state_today");
    STATE_HIGHLIGHTED = getAttrIntArray("tsquare_state_highlighted");
    STATE_RANGE_FIRST = getAttrIntArray("tsquare_state_range_first");
    STATE_RANGE_MIDDLE = getAttrIntArray("tsquare_state_range_middle");
    STATE_RANGE_LAST = getAttrIntArray("tsquare_state_range_last");
  }

  private int[] getAttrIntArray(String name) {
    return new int[] {getResources().getIdentifier(name, "attr", getContext().getPackageName())};
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

  public void setRangeState(MonthCellDescriptor.RangeState rangeState) {
    this.rangeState = rangeState;
    refreshDrawableState();
  }

  public void setHighlighted(boolean highlighted) {
    isHighlighted = highlighted;
    refreshDrawableState();
  }

  public boolean isCurrentMonth() {
    return isCurrentMonth;
  }

  public boolean isToday() {
    return isToday;
  }

  public boolean isSelectable() {
    return isSelectable;
  }

  @Override protected int[] onCreateDrawableState(int extraSpace) {
    final int[] drawableState = super.onCreateDrawableState(extraSpace + 5);

    if (isSelectable) {
      mergeDrawableStates(drawableState, STATE_SELECTABLE);
    }

    if (isCurrentMonth) {
      mergeDrawableStates(drawableState, STATE_CURRENT_MONTH);
    }

    if (isToday) {
      mergeDrawableStates(drawableState, STATE_TODAY);
    }

    if (isHighlighted) {
      mergeDrawableStates(drawableState, STATE_HIGHLIGHTED);
    }

    if (rangeState == MonthCellDescriptor.RangeState.FIRST) {
      mergeDrawableStates(drawableState, STATE_RANGE_FIRST);
    } else if (rangeState == MonthCellDescriptor.RangeState.MIDDLE) {
      mergeDrawableStates(drawableState, STATE_RANGE_MIDDLE);
    } else if (rangeState == RangeState.LAST) {
      mergeDrawableStates(drawableState, STATE_RANGE_LAST);
    }

    return drawableState;
  }
}

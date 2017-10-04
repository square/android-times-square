// Copyright 2013 Square, Inc.

package com.squareup.timessquare;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

public class CalendarCellView extends FrameLayout {
  private static final int[] STATE_SELECTABLE = {
      R.attr.tsquare_state_selectable
  };
  private static final int[] STATE_CURRENT_MONTH = {
      R.attr.tsquare_state_current_month
  };
  private static final int[] STATE_TODAY = {
      R.attr.tsquare_state_today
  };
  private static final int[] STATE_HIGHLIGHTED = {
      R.attr.tsquare_state_highlighted
  };
  private static final int[] STATE_RANGE_FIRST = {
      R.attr.tsquare_state_range_first
  };
  private static final int[] STATE_RANGE_MIDDLE = {
      R.attr.tsquare_state_range_middle
  };
  private static final int[] STATE_RANGE_LAST = {
      R.attr.tsquare_state_range_last
  };

  private boolean isSelectable = false;
  private boolean isCurrentMonth = false;
  private boolean isToday = false;
  private boolean isHighlighted = false;
  private RangeState rangeState = RangeState.NONE;
  private TextView dayOfMonthTextView;

  @SuppressWarnings("UnusedDeclaration") //
  public CalendarCellView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  /**
   * Sets whether the Calendar Cell View is a selectable view.
   *
   * @param isSelectable boolean value stating if the Calendar Cell View is selectable
   */
  public void setSelectable(boolean isSelectable) {
    if (this.isSelectable != isSelectable) {
      this.isSelectable = isSelectable;
      refreshDrawableState();
    }
  }

  /**
   * Sets the currently active month of the Calendar.
   *
   * @param isCurrentMonth boolean value stating if the Calendar Cell View is in the current month
   */
  public void setCurrentMonth(boolean isCurrentMonth) {
    if (this.isCurrentMonth != isCurrentMonth) {
      this.isCurrentMonth = isCurrentMonth;
      refreshDrawableState();
    }
  }

  /**
   * Set the Calendar Cell View to represent TODAY on the calendar
   *
   * @param isToday boolean value stating if the Calendar Cell View is today
   */
  public void setToday(boolean isToday) {
    if (this.isToday != isToday) {
      this.isToday = isToday;
      refreshDrawableState();
    }
  }

  /**
   * Set the range state of the Calendar Cell View. This sets whether the Calendar Cell View is at
   * the beginning, in the middle, or at the end of the selected range of dates.
   *
   * @param rangeState {@link RangeState} representing the position of the Calendar Cell View in the
   *                                     selected range.
   */
  public void setRangeState(RangeState rangeState) {
    if (this.rangeState != rangeState) {
      this.rangeState = rangeState;
      refreshDrawableState();
    }
  }

  /**
   * Set if this cell view is highlighted or not, changes the state of the Calendar Cell View
   * which can have effects on how it is displayed.
   *
   * @param isHighlighted boolean value stating if the Calendar Cell View is highlighted
   */
  public void setHighlighted(boolean isHighlighted) {
    if (this.isHighlighted != isHighlighted) {
      this.isHighlighted = isHighlighted;
      refreshDrawableState();
    }
  }

  /**
   * Check if this Calendar Cell View is contained within the current month.
   *
   * @return boolean value stating if the Calendar Cell View is in the current month
   */
  public boolean isCurrentMonth() {
    return isCurrentMonth;
  }

  /**
   * Check if this Calendar Cell View is today.
   *
   * @return boolean value stating if the Calendar Cell View represents today
   */
  public boolean isToday() {
    return isToday;
  }

  /**
   * Check if this Calendar Cell View is available for selection
   *
   * @return boolean value stating if the Calendar Cell View is selectable
   */
  public boolean isSelectable() {
    return isSelectable;
  }

  /**
   * Check if this Calendar Cell View is highlighted, for instance if the date has been selected
   * by the user or the system.
   *
   * @return boolean value stating if the Calendar Cell View is highlighted
   */
  public boolean isHighlighted() {
    return isHighlighted;
  }

  /**
   * Returns the Range State of the Calendar Cell View. If a cell view is highlighted it could
   * be part of a range of selected dates, this method will tell you if the Calendar Cell View
   * is at the beginning, in the middle, or at the end of the range of selected dates.
   *
   * @return {@link RangeState} of the Calendar Cell View
   */
  public RangeState getRangeState() {
    return rangeState;
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

    if (rangeState == RangeState.FIRST) {
      mergeDrawableStates(drawableState, STATE_RANGE_FIRST);
    } else if (rangeState == RangeState.MIDDLE) {
      mergeDrawableStates(drawableState, STATE_RANGE_MIDDLE);
    } else if (rangeState == RangeState.LAST) {
      mergeDrawableStates(drawableState, STATE_RANGE_LAST);
    }

    return drawableState;
  }

  /**
   * Set the {@link TextView} that displays the day of the month in this Calendar Cell View
   *
   * @param textView TextView for displaying the day of the month.
   */
  public void setDayOfMonthTextView(TextView textView) {
    dayOfMonthTextView = textView;
  }


  /**
   * Returns the {@link TextView} that will display the day of the month this Calendar Cell View
   * is representing. Throws an {@link IllegalStateException} if no {@link TextView} is available.
   *
   * @return The TextView for this Calendar Cell View
   */
  public TextView getDayOfMonthTextView() {
    if (dayOfMonthTextView == null) {
      throw new IllegalStateException(
              "You have to setDayOfMonthTextView in your custom DayViewAdapter."
      );
    }
    return dayOfMonthTextView;
  }
}

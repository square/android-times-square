// Copyright 2013 Square, Inc.

package com.squareup.timessquare;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.squareup.timessquare.MonthCellDescriptor.RangeState;

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
  private static final int[] STATE_BLOCKED = {
      R.attr.tsquare_state_blocked
  };
  private static final int[] STATE_SURFING_FIRST = {
      R.attr.tsquare_state_surfing_first
  };
  private static final int[] STATE_SURFING_MIDDLE = {
      R.attr.tsquare_state_surfing_middle
  };
  private static final int[] STATE_SURFING_LAST = {
      R.attr.tsquare_state_surfing_last
  };
  private static final int[] STATE_HOSTING_FIRST = {
      R.attr.tsquare_state_hosting_first
  };
  private static final int[] STATE_HOSTING_MIDDLE = {
      R.attr.tsquare_state_hosting_middle
  };
  private static final int[] STATE_HOSTING_LAST = {
      R.attr.tsquare_state_hosting_last
  };
  private boolean isSelectable = false;
  private boolean isCurrentMonth = false;
  private boolean isToday = false;
  private boolean isHighlighted = false;
  private boolean isBlocked = false;
  private RangeState surfingState = RangeState.NONE;
  private RangeState hostingState = RangeState.NONE;
  private RangeState rangeState = RangeState.NONE;
  private TextView dayOfMonthTextView;

  @SuppressWarnings("UnusedDeclaration") //
  public CalendarCellView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public void setSelectable(boolean isSelectable) {
    if (this.isSelectable != isSelectable) {
      this.isSelectable = isSelectable;
      refreshDrawableState();
    }
  }

  public void setCurrentMonth(boolean isCurrentMonth) {
    if (this.isCurrentMonth != isCurrentMonth) {
      this.isCurrentMonth = isCurrentMonth;
      refreshDrawableState();
    }
  }

  public void setToday(boolean isToday) {
    if (this.isToday != isToday) {
      this.isToday = isToday;
      refreshDrawableState();
    }
  }

  public void setRangeState(MonthCellDescriptor.RangeState rangeState) {
    if (this.rangeState != rangeState) {
      this.rangeState = rangeState;
      refreshDrawableState();
    }
  }

  public void setHighlighted(boolean isHighlighted) {
    if (this.isHighlighted != isHighlighted) {
      this.isHighlighted = isHighlighted;
      refreshDrawableState();
    }
  }

  public void setBlocked(boolean isBlocked) {
    if (this.isBlocked != isBlocked) {
      this.isBlocked = isBlocked;
      refreshDrawableState();
    }
  }

  public void setSurfingState(RangeState surfingState) {
    if (this.surfingState != surfingState) {
      this.surfingState = surfingState;
      refreshDrawableState();
    }
  }

  public void setHostingState(RangeState hostingState) {
    if (this.hostingState != hostingState) {
      this.hostingState = hostingState;
      refreshDrawableState();
    }
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

  public boolean isBlocked() {
    return isBlocked;
  }

  public RangeState getHostingState() {
    return hostingState;
  }

  public RangeState getSurfingState() {
    return surfingState;
  }

  public boolean isHighlighted() {
    return isHighlighted;
  }

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

    if (isBlocked) {
      mergeDrawableStates(drawableState, STATE_BLOCKED);
    }

    if (surfingState == RangeState.FIRST) {
      mergeDrawableStates(drawableState, STATE_SURFING_FIRST);
    } else if (surfingState == RangeState.MIDDLE) {
      mergeDrawableStates(drawableState, STATE_SURFING_MIDDLE);
    } else if (surfingState == RangeState.LAST) {
      mergeDrawableStates(drawableState, STATE_SURFING_LAST);
    }

    if (hostingState == RangeState.FIRST) {
      mergeDrawableStates(drawableState, STATE_HOSTING_FIRST);
    } else if (hostingState == RangeState.MIDDLE) {
      mergeDrawableStates(drawableState, STATE_HOSTING_MIDDLE);
    } else if (hostingState == RangeState.LAST) {
      mergeDrawableStates(drawableState, STATE_HOSTING_LAST);
    }

    return drawableState;
  }

  public void setDayOfMonthTextView(TextView textView) {
    dayOfMonthTextView = textView;
  }

  public TextView getDayOfMonthTextView() {
    if (dayOfMonthTextView == null) {
      throw new IllegalStateException(
              "You have to setDayOfMonthTextView in your custom DayViewAdapter."
      );
    }
    return dayOfMonthTextView;
  }
}

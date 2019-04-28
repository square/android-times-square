// Copyright 2013 Square, Inc.

package com.squareup.timessquare;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CalendarCellView extends RelativeLayout {

    private boolean isSelectable = false;
    private boolean isCurrentMonth = false;
    private boolean isToday = false;
    private boolean isHighlighted = false;
    private RangeState rangeState = RangeState.NONE;
    private TextView dayOfMonthTextView;

    @SuppressWarnings("UnusedDeclaration") //
    public CalendarCellView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(getContext(), R.layout.view_calendar_day, this);
        dayOfMonthTextView = findViewById(R.id.calendar_day_text_view);
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

    public void setRangeState(RangeState rangeState) {
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

    public boolean isCurrentMonth() {
        return isCurrentMonth;
    }

    public boolean isToday() {
        return isToday;
    }

    public boolean isSelectable() {
        return isSelectable;
    }

    public boolean isHighlighted() {
        return isHighlighted;
    }

    public RangeState getRangeState() {
        return rangeState;
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

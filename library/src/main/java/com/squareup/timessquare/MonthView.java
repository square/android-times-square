// Copyright 2012 Square, Inc.
package com.squareup.timessquare;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MonthView extends LinearLayout implements View.OnClickListener {
    TextView monthHeader;
    TextView yearHeader;
    GridLayout grid;
    private Listener listener;
    private List<CalendarCellDecorator> decorators;
    private boolean isRtl;
    private Locale locale;
    private boolean alwaysDigitNumbers;

    public static MonthView create(ViewGroup parent, LayoutInflater inflater,
                                   DateFormat weekdayNameFormat, Listener listener, Calendar today, int dividerColor,
                                   int dayBackgroundResId, int dayTextColorResId, int titleTextStyle, boolean displayHeader,
                                   int headerTextColor, boolean displayDayNamesHeaderRowView, boolean showAlwaysDigitNumbers,
                                   List<CalendarCellDecorator> decorators, Locale locale, DayViewAdapter adapter, HeaderCreator headerCreator) {
        final MonthView view = (MonthView) inflater.inflate(R.layout.month, parent, false);


        if (headerCreator == null) {
            headerCreator = new DefaultHeaderCreator(titleTextStyle);
        }

        // Set the Header views
        view.monthHeader = headerCreator.createMonthTitleHeader(parent.getContext());
        view.yearHeader = headerCreator.createYearTitleHeader(parent.getContext());

        LinearLayout headerLayout = new LinearLayout(parent.getContext());
        headerLayout.setOrientation(LinearLayout.HORIZONTAL);

        headerLayout.addView(view.monthHeader);
        headerLayout.addView(view.yearHeader);

        view.grid = view.findViewById(R.id.calendar_grid);

        // Add the month title as the first child of MonthView
        view.addView(headerLayout, 0);

        view.isRtl = isRtl(locale);
        view.locale = locale;
        view.alwaysDigitNumbers = showAlwaysDigitNumbers;
        int firstDayOfWeek = today.getFirstDayOfWeek();
        final TextView[] headerRow = {
                (TextView) view.grid.getChildAt(0),
                (TextView) view.grid.getChildAt(1),
                (TextView) view.grid.getChildAt(2),
                (TextView) view.grid.getChildAt(3),
                (TextView) view.grid.getChildAt(4),
                (TextView) view.grid.getChildAt(5),
                (TextView) view.grid.getChildAt(6)
        };

        final int originalDayOfWeek = today.get(Calendar.DAY_OF_WEEK);
        for (int offset = 0; offset < 7; offset++) {
            today.set(Calendar.DAY_OF_WEEK, getDayOfWeek(firstDayOfWeek, offset, view.isRtl));
            final TextView textView = headerRow[offset];
            textView.setText(weekdayNameFormat.format(today.getTime()).substring(0, 1));
        }
        today.set(Calendar.DAY_OF_WEEK, originalDayOfWeek);


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
        monthHeader.setText(new SimpleDateFormat("MMMM", locale).format(month.getDate()));
        yearHeader.setText(new SimpleDateFormat("yyyy", locale).format(month.getDate()));
        NumberFormat numberFormatter;
        if (alwaysDigitNumbers) {
            numberFormatter = NumberFormat.getInstance(Locale.US);
        } else {
            numberFormatter = NumberFormat.getInstance(locale);
        }

        final int numRows = cells.size();// Weeks with days
        for (int i = 0; i < 6; i++) {
            if (i < numRows) {
                List<MonthCellDescriptor> week = cells.get(i);
                for (int c = 0; c < week.size(); c++) {
                    CalendarCellView cellView = (CalendarCellView) grid.getChildAt((i + 1) * 7 + c);
                    cellView.setVisibility(View.VISIBLE);
                    MonthCellDescriptor cell = week.get(isRtl ? 6 - c : c);

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
                    cellView.setRangeState(cell.getRangeState());
                    cellView.setHighlighted(cell.isHighlighted());
                    cellView.setTag(cell);
                    cellView.setOnClickListener(this);

                    if (null != decorators) {
                        for (CalendarCellDecorator decorator : decorators) {
                            decorator.decorate(cellView, cell.getDate());
                        }
                    }
                }
            } else {
                for (int c = 0; c < 7; c++) {
                    CalendarCellView cellView = (CalendarCellView) grid.getChildAt((i + 1) * 7 + c);
                    cellView.setOnClickListener(null);
                    cellView.setVisibility(GONE);
                }
            }
        }


        Logr.d("MonthView.init took %d ms", System.currentTimeMillis() - start);
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.handleClick((MonthCellDescriptor) v.getTag());
        }

    }

    public interface Listener {
        void handleClick(MonthCellDescriptor cell);
    }


    public interface HeaderCreator {
        public TextView createMonthTitleHeader(Context context);

        public TextView createYearTitleHeader(Context context);
    }
}


class DefaultHeaderCreator implements MonthView.HeaderCreator {

    int titleTextStyle;

    public DefaultHeaderCreator(int titleTextStyle) {
        this.titleTextStyle = titleTextStyle;
    }

    @Override
    public TextView createYearTitleHeader(Context context) {
        return new TextView(new ContextThemeWrapper(context, titleTextStyle));
    }

    @Override
    public TextView createMonthTitleHeader(Context context) {
        return new TextView(new ContextThemeWrapper(context, titleTextStyle));
    }
}

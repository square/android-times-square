package com.squareup.timessquare.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.timessquare.CalendarCellDecorator;
import com.squareup.timessquare.CalendarPickerView;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import static com.squareup.timessquare.CalendarPickerView.SelectionMode.RANGE;

/**
 * Created by mervegencer on 05/01/2017.
 * Hitit Computer Services 2017
 */

public class CalendarBottomSheetDialog extends BottomSheetDialogFragment {

    CalendarPickerView calendarPickerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = new CalendarPickerView(getContext(), null);

        calendarPickerView = (CalendarPickerView) contentView;

        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);

        Date today = new Date();

        calendarPickerView.setDecorators(Collections.<CalendarCellDecorator>emptyList());
        calendarPickerView.init(today, nextYear.getTime()).inMode(RANGE);

        return contentView;
    }
}

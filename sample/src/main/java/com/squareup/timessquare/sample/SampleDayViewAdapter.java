package com.squareup.timessquare.sample;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.squareup.timessquare.CalendarCellView;
import com.squareup.timessquare.DayViewAdapter;
import com.squareup.timessquare.MonthCellDescriptor;
import java.util.Random;

public class SampleDayViewAdapter implements DayViewAdapter {
  @Override public void makeCellView(CalendarCellView parent) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View layout = inflater.inflate(R.layout.day_view_custom, parent, false);
    parent.addView(layout);
    parent.setDayOfMonthTextView((TextView) layout.findViewById(R.id.day_view));
  }
  
  @Override
  public void updateCellView(CalendarCellView parent) {
    MonthCellDescriptor cellDescriptor = (MonthCellDescriptor) parent.getTag();
    if (parent.findViewById(R.id.hour_view) != null) {
      TextView hoursView = (TextView) parent.findViewById(R.id.hour_view);
      if(cellDescriptor != null && cellDescriptor.getDate() != null) {
        //Showing hours with Random number generator
        hoursView.setText(""+new Random().nextInt(24));
      }
    }
  }
}

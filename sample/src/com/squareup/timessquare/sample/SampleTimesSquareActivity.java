package com.squareup.timessquare.sample;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import com.squareup.timessquare.CalendarPickerView;
import com.squareup.timessquare.CalendarPickerView.SelectionMode;
import com.squareup.timessquare.Event;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

public class SampleTimesSquareActivity extends Activity
    implements CalendarPickerView.OnDateSelectedListener {
  private static final String TAG = "SampleTimesSquareActivity";
  private CalendarPickerView calendar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.sample_calendar_picker);

    final Calendar nextYear = Calendar.getInstance();
    nextYear.add(Calendar.YEAR, 1);

    final Calendar lastYear = Calendar.getInstance();
    lastYear.add(Calendar.YEAR, -1);

    calendar = (CalendarPickerView) findViewById(R.id.calendar_view);

    calendar.setOnDateSelectedListener(this);

    calendar.init(lastYear.getTime(), nextYear.getTime()) //
        .inMode(SelectionMode.SINGLE) //
        .withSelectedDate(new Date());

    final Button single = (Button) findViewById(R.id.button_single);
    final Button multi = (Button) findViewById(R.id.button_multi);
    final Button range = (Button) findViewById(R.id.button_range);
    final Button dialog = (Button) findViewById(R.id.button_dialog);
    final Button events = (Button) findViewById(R.id.button_events);

    single.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        single.setEnabled(false);
        multi.setEnabled(true);
        range.setEnabled(true);
        events.setEnabled(true);

        calendar.init(new Date(), nextYear.getTime()) //
            .inMode(SelectionMode.SINGLE) //
            .withSelectedDate(new Date());
      }
    });

    multi.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        single.setEnabled(true);
        multi.setEnabled(false);
        range.setEnabled(true);
        events.setEnabled(true);

        Calendar today = Calendar.getInstance();
        ArrayList<Date> dates = new ArrayList<Date>();
        for (int i = 0; i < 5; i++) {
          today.add(Calendar.DAY_OF_MONTH, 3);
          dates.add(today.getTime());
        }
        calendar.init(new Date(), nextYear.getTime()) //
            .inMode(SelectionMode.MULTIPLE) //
            .withSelectedDates(dates);
      }
    });

    range.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        single.setEnabled(true);
        multi.setEnabled(true);
        range.setEnabled(false);
        events.setEnabled(true);

        Calendar today = Calendar.getInstance();
        ArrayList<Date> dates = new ArrayList<Date>();
        today.add(Calendar.DATE, 3);
        dates.add(today.getTime());
        today.add(Calendar.DATE, 5);
        dates.add(today.getTime());
        calendar.init(new Date(), nextYear.getTime()) //
            .inMode(SelectionMode.RANGE) //
            .withSelectedDates(dates);
      }
    });

    dialog.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        CalendarPickerView dialogView =
            (CalendarPickerView) getLayoutInflater().inflate(R.layout.dialog, null, false);
        dialogView.init(new Date(), nextYear.getTime());
        new AlertDialog.Builder(SampleTimesSquareActivity.this).setTitle("I'm a dialog!")
            .setView(dialogView)
            .setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
              }
            })
            .create()
            .show();
      }
    });

    events.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        single.setEnabled(true);
        multi.setEnabled(true);
        range.setEnabled(true);
        events.setEnabled(false);


        // Add event 1
        int event1Color = getResources().getColor(android.R.color.holo_orange_light);
        int event1TxtColor = getResources().getColor(android.R.color.white);

        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.DAY_OF_MONTH, 9);

        Event event1 = new Event(calendar1.getTime(), event1Color, event1TxtColor);


        // Add event 2

        int event2Color = getResources().getColor(android.R.color.holo_green_dark);
        int event2TxtColor = getResources().getColor(android.R.color.holo_red_light);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.DAY_OF_MONTH, 19);

        Event event2 = new Event(calendar2.getTime(), event2Color, event2TxtColor);

        List<Event> events = new ArrayList<Event>();

        events.add(event1);
        events.add(event2);

        // Init calendar

        Calendar today = Calendar.getInstance();

        calendar.init(new Date(), nextYear.getTime(), events) //
            .inMode(SelectionMode.SINGLE) //
            .withSelectedDate(today.getTime());
      }
    });

    findViewById(R.id.done_button).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        Log.d(TAG, "Selected time in millis: " + calendar.getSelectedDate().getTime());
        String toast = "Selected: " + calendar.getSelectedDate().getTime();
        Toast.makeText(SampleTimesSquareActivity.this, toast, LENGTH_SHORT).show();
      }
    });
  }

  @Override public void onDateSelected(Date date) {
    Toast.makeText(this, date.toString(), Toast.LENGTH_SHORT).show();
  }

  @Override public void onEventSelected(Date date, Event event) {
    Toast.makeText(this, "Event selected: " + date.toString(), Toast.LENGTH_SHORT).show();
  }
}

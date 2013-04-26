package com.squareup.timessquare.sample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import com.squareup.timessquare.CalendarPickerView;
import com.squareup.timessquare.CalendarPickerView.SelectionMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import static android.widget.Toast.LENGTH_SHORT;

public class SampleTimesSquareActivity extends Activity {
  private static final String TAG = "SampleTimesSquareActivity";
  private CalendarPickerView calendar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.calendar_picker);

    final Calendar nextYear = Calendar.getInstance();
    nextYear.add(Calendar.YEAR, 1);

    calendar = (CalendarPickerView) findViewById(R.id.calendar_view);
    calendar.init(new Date(), new Date(), nextYear.getTime());

    Button single = (Button) findViewById(R.id.button_single);
    single.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        calendar.init(new Date(), new Date(), nextYear.getTime());
      }
    });

    Button multi = (Button) findViewById(R.id.button_multi);
    multi.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Calendar today = Calendar.getInstance();
        ArrayList<Date> dates = new ArrayList<Date>();
        for (int i = 0; i < 5; i++) {
          today.add(Calendar.DAY_OF_MONTH, 3);
          dates.add(today.getTime());
        }
        calendar.init(dates, new Date(), nextYear.getTime(),
            SelectionMode.MULTI);
      }
    });

    Button period = (Button) findViewById(R.id.button_period);
    period.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Calendar today = Calendar.getInstance();
        ArrayList<Date> dates = new ArrayList<Date>();
        today.add(Calendar.DAY_OF_MONTH, 3);
        dates.add(today.getTime());
        today.add(Calendar.DAY_OF_MONTH, 5);
        dates.add(today.getTime());
        calendar.init(dates, new Date(), nextYear.getTime(),
            SelectionMode.PERIOD);
      }
    });

    Button sPeriod = (Button) findViewById(R.id.button_speriod);
    sPeriod.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Calendar today = Calendar.getInstance();
        ArrayList<Date> dates = new ArrayList<Date>();
        today.add(Calendar.DAY_OF_MONTH, 3);
        dates.add(today.getTime());
        today.add(Calendar.DAY_OF_MONTH, 5);
        dates.add(today.getTime());
        calendar.init(dates, new Date(), nextYear.getTime(),
            SelectionMode.SELECTEDPERIOD);
      }
    });

    findViewById(R.id.done_button).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        Log.d(TAG, "Selected time in millis: "
            + calendar.getSelectedDate().getTime());
        String toast = "Selected: " + calendar.getSelectedDate().getTime();
        Toast.makeText(SampleTimesSquareActivity.this, toast, LENGTH_SHORT)
            .show();
      }
    });
  }
}

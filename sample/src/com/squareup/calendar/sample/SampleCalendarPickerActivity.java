package com.squareup.calendar.sample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import com.squareup.calendar.CalendarPickerView;
import java.util.Calendar;
import java.util.Date;

import static android.widget.Toast.LENGTH_SHORT;

public class SampleCalendarPickerActivity extends Activity {
  private static final String TAG = "SampleCalendarPickerActivity";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.calendar_picker);

    Calendar nextYear = Calendar.getInstance();
    nextYear.add(Calendar.YEAR, 1);

    final CalendarPickerView calendar = (CalendarPickerView) findViewById(R.id.calendar_view);
    calendar.init(new Date(), new Date(), nextYear.getTime());

    findViewById(R.id.done_button).setOnClickListener(new OnClickListener() {
      @Override public void onClick(View view) {
        Log.d(TAG, "Selected time in millis: " + calendar.getSelectedDate().getTime());
        String toast = "Selected: " + calendar.getSelectedDate().getTime();
        Toast.makeText(SampleCalendarPickerActivity.this, toast, LENGTH_SHORT).show();
      }
    });
  }
}

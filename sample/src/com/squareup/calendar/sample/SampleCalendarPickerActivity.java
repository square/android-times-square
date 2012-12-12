package com.squareup.calendar.sample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.squareup.calendar.CalendarPickerView;
import java.util.Calendar;
import java.util.Date;

public class SampleCalendarPickerActivity extends Activity {
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.calendar_picker);
    final CalendarPickerView calendar = (CalendarPickerView) findViewById(R.id.calendar_view);
    final Calendar nextYear = Calendar.getInstance();
    nextYear.add(Calendar.YEAR, 1);
    calendar.init(new Date(), new Date(), nextYear.getTime());
    View done = findViewById(R.id.done_button);
    done.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        Log.d("CalendarSample", "Selected time in millis: " + calendar.getSelectedDateMillis());
        final String toast = "Selected: " + calendar.getSelectedDate().getTime();
        Toast.makeText(SampleCalendarPickerActivity.this, toast, Toast.LENGTH_SHORT).show();
      }
    });
  }
}

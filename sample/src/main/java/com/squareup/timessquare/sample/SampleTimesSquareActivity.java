package com.squareup.timessquare.sample;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import com.squareup.timessquare.CalendarCellDecorator;
import com.squareup.timessquare.CalendarPickerView;
import com.squareup.timessquare.CalendarPickerView.SelectionMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.widget.Toast.LENGTH_SHORT;

public class SampleTimesSquareActivity extends Activity {
  private static final String TAG = "SampleTimesSquareActivity";
  private CalendarPickerView calendar;
  private AlertDialog theDialog;
  private CalendarPickerView dialogView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.sample_calendar_picker);

    final Calendar nextYear = Calendar.getInstance();
    nextYear.add(Calendar.YEAR, 1);

    final Calendar lastYear = Calendar.getInstance();
    lastYear.add(Calendar.YEAR, -1);

    calendar = (CalendarPickerView) findViewById(R.id.calendar_view);
    calendar.init(lastYear.getTime(), nextYear.getTime()) //
        .inMode(SelectionMode.SINGLE) //
        .withSelectedDate(new Date());

    initButtonListeners(nextYear, lastYear);
  }

  private void initButtonListeners(final Calendar nextYear, final Calendar lastYear) {
    final Button single = (Button) findViewById(R.id.button_single);
    final Button multi = (Button) findViewById(R.id.button_multi);
    final Button range = (Button) findViewById(R.id.button_range);
    final Button displayOnly = (Button) findViewById(R.id.button_display_only);
    final Button dialog = (Button) findViewById(R.id.button_dialog);
    final Button customized = (Button) findViewById(R.id.button_customized);
    final Button decorator = (Button) findViewById(R.id.button_decorator);
    final Button rtl = (Button) findViewById(R.id.button_rtl);
    single.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        single.setEnabled(false);
        multi.setEnabled(true);
        range.setEnabled(true);
        displayOnly.setEnabled(true);
        decorator.setEnabled(true);

        calendar.setDecorators(Collections.<CalendarCellDecorator>emptyList());
        calendar.init(lastYear.getTime(), nextYear.getTime()) //
            .inMode(SelectionMode.SINGLE) //
            .withSelectedDate(new Date());
      }
    });

    multi.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        single.setEnabled(true);
        multi.setEnabled(false);
        range.setEnabled(true);
        displayOnly.setEnabled(true);
        decorator.setEnabled(true);

        Calendar today = Calendar.getInstance();
        ArrayList<Date> dates = new ArrayList<Date>();
        for (int i = 0; i < 5; i++) {
          today.add(Calendar.DAY_OF_MONTH, 3);
          dates.add(today.getTime());
        }
        calendar.setDecorators(Collections.<CalendarCellDecorator>emptyList());
        calendar.init(new Date(), nextYear.getTime()) //
            .inMode(SelectionMode.MULTIPLE) //
            .withSelectedDates(dates);
      }
    });

    range.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        single.setEnabled(true);
        multi.setEnabled(true);
        range.setEnabled(false);
        displayOnly.setEnabled(true);
        decorator.setEnabled(true);

        Calendar today = Calendar.getInstance();
        ArrayList<Date> dates = new ArrayList<Date>();
        today.add(Calendar.DATE, 3);
        dates.add(today.getTime());
        today.add(Calendar.DATE, 5);
        dates.add(today.getTime());
        calendar.setDecorators(Collections.<CalendarCellDecorator>emptyList());
        calendar.init(new Date(), nextYear.getTime()) //
            .inMode(SelectionMode.RANGE) //
            .withSelectedDates(dates);
      }
    });

    displayOnly.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        single.setEnabled(true);
        multi.setEnabled(true);
        range.setEnabled(true);
        displayOnly.setEnabled(false);
        decorator.setEnabled(true);

        calendar.setDecorators(Collections.<CalendarCellDecorator>emptyList());
        calendar.init(new Date(), nextYear.getTime()) //
            .inMode(SelectionMode.SINGLE) //
            .withSelectedDate(new Date()) //
            .displayOnly();
      }
    });

    dialog.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View view) {
        dialogView = (CalendarPickerView) getLayoutInflater().inflate(R.layout.dialog, null, false);
        dialogView.init(lastYear.getTime(), nextYear.getTime()) //
            .withSelectedDate(new Date());
        theDialog =
            new AlertDialog.Builder(SampleTimesSquareActivity.this).setTitle("I'm a dialog!")
                .setView(dialogView)
                .setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                  @Override public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                  }
                })
                .create();
        theDialog.setOnShowListener(new DialogInterface.OnShowListener() {
          @Override public void onShow(DialogInterface dialogInterface) {
            Log.d(TAG, "onShow: fix the dimens!");
            dialogView.fixDialogDimens();
          }
        });
        theDialog.show();
      }
    });

    customized.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View view) {
        dialogView = (CalendarPickerView) getLayoutInflater() //
            .inflate(R.layout.dialog_customized, null, false);
        dialogView.init(lastYear.getTime(), nextYear.getTime()).withSelectedDate(new Date());
        theDialog =
            new AlertDialog.Builder(SampleTimesSquareActivity.this).setTitle("Pimp my calendar !")
                .setView(dialogView)
                .setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                  @Override public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                  }
                })
                .create();
        theDialog.setOnShowListener(new DialogInterface.OnShowListener() {
          @Override public void onShow(DialogInterface dialogInterface) {
            Log.d(TAG, "onShow: fix the dimens!");
            dialogView.fixDialogDimens();
          }
        });
        theDialog.show();
      }
    });

    decorator.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        single.setEnabled(true);
        multi.setEnabled(true);
        range.setEnabled(true);
        displayOnly.setEnabled(true);
        decorator.setEnabled(false);

        List<CalendarCellDecorator> decorators = Collections.<CalendarCellDecorator>emptyList();
        decorators.add(new SampleDecorator());
        calendar.setDecorators(decorators);
        calendar.init(lastYear.getTime(), nextYear.getTime()) //
            .inMode(SelectionMode.SINGLE) //
            .withSelectedDate(new Date());
      }
    });

    rtl.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View view) {
        dialogView = (CalendarPickerView) getLayoutInflater().inflate(R.layout.dialog, null, false);
        dialogView.init(lastYear.getTime(), nextYear.getTime(), new Locale("iw", "IL")) //
            .withSelectedDate(new Date());
        theDialog = new AlertDialog.Builder(SampleTimesSquareActivity.this) //
            .setTitle("I'm right-to-left!")
            .setView(dialogView)
            .setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
              }
            })
            .create();
        theDialog.setOnShowListener(new DialogInterface.OnShowListener() {
          @Override public void onShow(DialogInterface dialogInterface) {
            Log.d(TAG, "onShow: fix the dimens!");
            dialogView.fixDialogDimens();
          }
        });
        theDialog.show();
      }
    });

    findViewById(R.id.done_button).setOnClickListener(new OnClickListener() {
      @Override public void onClick(View view) {
        Log.d(TAG, "Selected time in millis: " + calendar.getSelectedDate().getTime());
        String toast = "Selected: " + calendar.getSelectedDate().getTime();
        Toast.makeText(SampleTimesSquareActivity.this, toast, LENGTH_SHORT).show();
      }
    });
  }

  @Override public void onConfigurationChanged(Configuration newConfig) {
    boolean applyFixes = theDialog != null && theDialog.isShowing();
    if (applyFixes) {
      Log.d(TAG, "Config change: unfix the dimens so I'll get remeasured!");
      dialogView.unfixDialogDimens();
    }
    super.onConfigurationChanged(newConfig);
    if (applyFixes) {
      dialogView.post(new Runnable() {
        @Override public void run() {
          Log.d(TAG, "Config change done: re-fix the dimens!");
          dialogView.fixDialogDimens();
        }
      });
    }
  }
}

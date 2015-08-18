// Copyright 2012 Square, Inc.
package com.squareup.timessquare;

import android.app.Activity;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.intellij.lang.annotations.MagicConstant;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.squareup.timessquare.CalendarPickerView.SelectionMode.MULTIPLE;
import static com.squareup.timessquare.CalendarPickerView.SelectionMode.RANGE;
import static com.squareup.timessquare.CalendarPickerView.SelectionMode.SINGLE;
import static com.squareup.timessquare.MonthCellDescriptor.RangeState.FIRST;
import static com.squareup.timessquare.MonthCellDescriptor.RangeState.LAST;
import static com.squareup.timessquare.MonthCellDescriptor.RangeState.MIDDLE;
import static com.squareup.timessquare.MonthCellDescriptor.RangeState.NONE;
import static java.util.Calendar.APRIL;
import static java.util.Calendar.AUGUST;
import static java.util.Calendar.DATE;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.DECEMBER;
import static java.util.Calendar.FEBRUARY;
import static java.util.Calendar.JANUARY;
import static java.util.Calendar.JULY;
import static java.util.Calendar.JUNE;
import static java.util.Calendar.MARCH;
import static java.util.Calendar.MAY;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.NOVEMBER;
import static java.util.Calendar.OCTOBER;
import static java.util.Calendar.SEPTEMBER;
import static java.util.Calendar.YEAR;
import static org.fest.assertions.api.ANDROID.assertThat;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;

@RunWith(RobolectricTestRunner.class) //
@Config(manifest = "src/main/AndroidManifest.xml")
public class CalendarPickerViewTest {

  static {
    // Set the default locale to a different one than the locale used for the tests to ensure that
    // the CalendarPickerView does not rely on any other locale than the configured one --
    // especially not the default locale.
    Locale.setDefault(Locale.GERMANY);
  }

  private Activity activity;
  private Locale locale;
  private CalendarPickerView view;
  private Calendar today;
  private Date maxDate;
  private Date minDate;

  @Before public void setUp() throws Exception {
    activity = Robolectric.buildActivity(Activity.class).create().start().resume().get();
    locale = Locale.US;
    view = new CalendarPickerView(activity, null);
    today = Calendar.getInstance(locale);
    today.set(2012, NOVEMBER, 16, 0, 0);
    minDate = today.getTime();
    today.set(2013, NOVEMBER, 16, 0, 0);
    maxDate = today.getTime();
    today.set(2012, NOVEMBER, 16, 0, 0);
    Date startDate = today.getTime();
    view.init(minDate, maxDate, locale) //
        .inMode(SINGLE) //
        .withSelectedDate(startDate);

    // Do not change the internal state of the CalendarPickerView until init() has run.
    view.today.setTime(startDate);
  }

  @Test public void testInitDecember() throws Exception {
    Calendar dec2012 = buildCal(2012, DECEMBER, 1);
    Calendar dec2013 = buildCal(2013, DECEMBER, 1);
    view.init(dec2012.getTime(), dec2013.getTime(), locale) //
        .inMode(SINGLE) //
        .withSelectedDate(dec2012.getTime());
    assertThat(view.months).hasSize(12);
  }

  @Test public void testInitJanuary() throws Exception {
    Calendar jan2012 = buildCal(2012, JANUARY, 1);
    Calendar jan2013 = buildCal(2013, JANUARY, 1);
    view.init(jan2012.getTime(), jan2013.getTime(), locale) //
        .inMode(SINGLE) //
        .withSelectedDate(jan2012.getTime());
    assertThat(view.months).hasSize(12);
  }

  @Test public void testInitMidyear() throws Exception {
    Calendar may2012 = buildCal(2012, MAY, 1);
    Calendar may2013 = buildCal(2013, MAY, 1);
    view.init(may2012.getTime(), may2013.getTime(), locale) //
        .inMode(SINGLE) //
        .withSelectedDate(may2012.getTime());
    assertThat(view.months).hasSize(12);
  }

  @Test public void testOnlyShowingFourWeeks() throws Exception {
    List<List<MonthCellDescriptor>> cells = selectDateAndGetCells(FEBRUARY, 2015, today);
    assertThat(cells).hasSize(4);

    // Last cell should be 1.
    assertCell(cells, 0, 0, 1, true, false, false, false, NONE);

    // Last cell should be 28.
    assertCell(cells, 3, 6, 28, true, false, false, false, NONE);
  }

  @Test public void testOnlyShowingFiveWeeks() throws Exception {
    List<List<MonthCellDescriptor>> cells = selectDateAndGetCells(FEBRUARY, 2013, today);
    assertThat(cells).hasSize(5);

    // First cell is the 27th of January.
    assertCell(cells, 0, 0, 27, false, false, false, false, NONE);

    // First day of Feb falls on the 5th cell.
    assertCell(cells, 0, 5, 1, true, false, false, true, NONE);

    // Last day of Feb falls on the 5th row, 5th column.
    assertCell(cells, 4, 4, 28, true, false, false, true, NONE);

    // Last cell should be March 2nd.
    assertCell(cells, 4, 6, 2, false, false, false, false, NONE);
  }

  @Test public void testWeirdOverlappingYear() throws Exception {
    List<List<MonthCellDescriptor>> cells = selectDateAndGetCells(JANUARY, 2013, today);
    assertThat(cells).hasSize(5);
  }

  @Test public void testShowingSixWeeks() throws Exception {
    List<List<MonthCellDescriptor>> cells = selectDateAndGetCells(DECEMBER, 2012, today);
    assertThat(cells).hasSize(6);

    // First cell is the 27th of November.
    assertCell(cells, 0, 0, 25, false, false, false, false, NONE);

    // First day of December falls on the 6th cell.
    assertCell(cells, 0, 6, 1, true, false, false, true, NONE);

    // Last day of December falls on the 6th row, 2nd column.
    assertCell(cells, 5, 1, 31, true, false, false, true, NONE);

    // Last cell should be January 5th.
    assertCell(cells, 5, 6, 5, false, false, false, false, NONE);
  }

  @Test public void testIsSelected() throws Exception {
    Calendar nov29 = buildCal(2012, NOVEMBER, 29);

    List<List<MonthCellDescriptor>> cells = selectDateAndGetCells(NOVEMBER, 2012, nov29);
    assertThat(cells).hasSize(5);
    // Make sure the cell is selected when it's in November.
    assertCell(cells, 4, 4, 29, true, true, false, true, NONE);

    cells = selectDateAndGetCells(DECEMBER, 2012, nov29);
    assertThat(cells).hasSize(6);
    // Make sure the cell is not selected when it's in December.
    assertCell(cells, 0, 4, 29, false, false, false, false, NONE);
  }

  @Test public void testTodayIsToday() throws Exception {
    List<List<MonthCellDescriptor>> cells = selectDateAndGetCells(NOVEMBER, 2012, today);
    assertCell(cells, 2, 5, 16, true, true, true, true, NONE);
  }

  @Test public void testSelectabilityInFirstMonth() throws Exception {
    List<List<MonthCellDescriptor>> cells = selectDateAndGetCells(NOVEMBER, 2012, today);
    // 10/29 is not selectable because it's in the previous month.
    assertCell(cells, 0, 0, 28, false, false, false, false, NONE);
    // 11/1 is not selectable because it's < minDate (11/16/12).
    assertCell(cells, 0, 4, 1, true, false, false, false, NONE);
    // 11/16 is selectable because it's == minDate (11/16/12).
    assertCell(cells, 2, 5, 16, true, true, true, true, NONE);
    // 11/20 is selectable because it's > minDate (11/16/12).
    assertCell(cells, 3, 2, 20, true, false, false, true, NONE);
    // 12/1 is not selectable because it's in the next month.
    assertCell(cells, 4, 6, 1, false, false, false, false, NONE);
  }

  @Test public void testSelectabilityInLastMonth() throws Exception {
    List<List<MonthCellDescriptor>> cells = selectDateAndGetCells(NOVEMBER, 2013, today);
    // 10/29 is not selectable because it's in the previous month.
    assertCell(cells, 0, 0, 27, false, false, false, false, NONE);
    // 11/1 is selectable because it's < maxDate (11/16/13).
    assertCell(cells, 0, 5, 1, true, false, false, true, NONE);
    // 11/15 is selectable because it's one less than maxDate (11/16/13).
    assertCell(cells, 2, 5, 15, true, false, false, true, NONE);
    // 11/16 is not selectable because it's > maxDate (11/16/13).
    assertCell(cells, 2, 6, 16, true, false, false, false, NONE);
  }

  @Test public void testInitSingleWithMultipleSelections() throws Exception {
    List<Date> selectedDates = new ArrayList<Date>();
    selectedDates.add(minDate);
    // This one should work.
    view.init(minDate, maxDate, locale) //
        .inMode(SINGLE) //
        .withSelectedDates(selectedDates);

    // Now add another date and try init'ing again in SINGLE mode.
    Calendar secondSelection = buildCal(2012, NOVEMBER, 17);
    selectedDates.add(secondSelection.getTime());
    try {
      view.init(minDate, maxDate, locale) //
          .inMode(SINGLE) //
          .withSelectedDates(selectedDates);
      fail("Should not have been able to init() with SINGLE mode && multiple selected dates");
    } catch (IllegalArgumentException expected) {
    }
  }

  @Test public void testInitSeveralWithRangeSelections() throws Exception {
    List<Date> selectedDates = new ArrayList<Date>();
    Calendar firstSelection = buildCal(2012, NOVEMBER, 17);
    selectedDates.add(firstSelection.getTime());
    Calendar secondSelection = buildCal(2012, NOVEMBER, 21);
    selectedDates.add(secondSelection.getTime());
    Calendar thirdSelection = buildCal(2012, NOVEMBER, 25);
    selectedDates.add(thirdSelection.getTime());
    try {
      view.init(minDate, maxDate, locale) //
          .inMode(RANGE) //
          .withSelectedDates(selectedDates);
      fail("Should not have been able to init() with RANGE mode && three selected dates");
    } catch (IllegalArgumentException expected) {
    }
  }

  @Test public void testNullInitArguments() throws Exception {
    final Date validDate = today.getTime();
    try {
      view.init(validDate, validDate, locale) //
          .inMode(SINGLE) //
          .withSelectedDate(null);
      fail("Should not have been able to pass in a null startDate");
    } catch (IllegalArgumentException expected) {
    }
    try {
      view.init(null, validDate, locale) //
          .inMode(SINGLE) //
          .withSelectedDate(validDate);
      fail("Should not have been able to pass in a null minDate");
    } catch (IllegalArgumentException expected) {
    }
    try {
      view.init(validDate, null, locale) //
          .inMode(SINGLE) //
          .withSelectedDate(validDate);
      fail("Should not have been able to pass in a null maxDate");
    } catch (IllegalArgumentException expected) {
    }
    try {
      view.init(validDate, validDate, null) //
          .inMode(SINGLE) //
          .withSelectedDate(validDate);
      fail("Should not have been able to pass in a null locale");
    } catch (IllegalArgumentException expected) {
    }
  }

  @Test public void testMinAndMaxMixup() throws Exception {
    final Date minDate = today.getTime();
    today.add(YEAR, -1);
    final Date maxDate = today.getTime();
    try {
      view.init(minDate, maxDate, locale) //
          .inMode(SINGLE) //
          .withSelectedDate(minDate);
      fail("Should not have been able to pass in a maxDate < minDate");
    } catch (IllegalArgumentException expected) {
    }
  }

  @Test public void testSelectedNotInRange() throws Exception {
    final Date minDate = today.getTime();
    today.add(YEAR, 1);
    final Date maxDate = today.getTime();
    today.add(YEAR, 1);
    Date selectedDate = today.getTime();
    try {
      view.init(minDate, maxDate, locale) //
          .inMode(SINGLE) //
          .withSelectedDate(selectedDate);
      fail("Should not have been able to pass in a selectedDate > maxDate");
    } catch (IllegalArgumentException expected) {
    }
    today.add(YEAR, -5);
    selectedDate = today.getTime();
    try {
      view.init(minDate, maxDate, locale) //
          .inMode(SINGLE) //
          .withSelectedDate(selectedDate);
      fail("Should not have been able to pass in a selectedDate < minDate");
    } catch (IllegalArgumentException expected) {
    }
  }

  /**
   * Verify the expectation that the set of dates excludes the max.
   * In other words, the date interval is [minDate, maxDate)
   */
  @Test(expected = IllegalArgumentException.class)
  public void testSelectedNotInRange_maxDateExcluded() throws Exception {
    view.init(minDate, maxDate, locale) //
        .inMode(SINGLE) //
        .withSelectedDate(maxDate);
  }

  @Test public void testNotCallingInit() throws Exception {
    view = new CalendarPickerView(activity, null);
    try {
      view.measure(0, 0);
      fail("Should have thrown an IllegalStateException!");
    } catch (IllegalStateException expected) {
    }
  }

  @Test public void testShowingOnlyOneMonth() throws Exception {
    Calendar feb1 = buildCal(2013, FEBRUARY, 1);
    Calendar mar1 = buildCal(2013, MARCH, 1);
    view.init(feb1.getTime(), mar1.getTime(), locale) //
        .inMode(SINGLE) //
        .withSelectedDate(feb1.getTime());
    assertThat(view.months).hasSize(1);
  }

  @Test public void selectDateThrowsExceptionForDatesOutOfRange() {
    view.init(minDate, maxDate, locale) //
        .inMode(SINGLE) //
        .withSelectedDate(today.getTime());
    Calendar outOfRange = buildCal(2015, FEBRUARY, 1);
    try {
      view.selectDate(outOfRange.getTime());
      fail("selectDate should've blown up with an out of range date");
    } catch (IllegalArgumentException expected) {
    }
  }

  @Test public void selectDateReturnsTrueForDateInRange() {
    view.init(minDate, maxDate, locale) //
        .inMode(SINGLE) //
        .withSelectedDate(today.getTime());
    Calendar inRange = buildCal(2013, FEBRUARY, 1);
    boolean wasAbleToSetDate = view.selectDate(inRange.getTime());
    assertThat(wasAbleToSetDate).isTrue();
  }

  @Test public void selectDateDoesntSelectDisabledCell() {
    view.init(minDate, maxDate, locale) //
        .inMode(SINGLE) //
        .withSelectedDate(today.getTime());
    Calendar jumpToCal = buildCal(2013, FEBRUARY, 1);
    boolean wasAbleToSetDate = view.selectDate(jumpToCal.getTime());
    assertThat(wasAbleToSetDate).isTrue();
    assertThat(view.selectedCells.get(0).isSelectable()).isTrue();
  }

  @Test public void testMultiselectWithNoInitialSelections() throws Exception {
    view.init(minDate, maxDate, locale) //
        .inMode(MULTIPLE);
    assertThat(view.selectionMode).isEqualTo(MULTIPLE);
    assertThat(view.getSelectedDates()).isEmpty();

    view.selectDate(minDate);
    assertThat(view.getSelectedDates()).hasSize(1);

    Calendar secondSelection = buildCal(2012, NOVEMBER, 17);
    view.selectDate(secondSelection.getTime());
    assertThat(view.getSelectedDates()).hasSize(2);
    assertThat(view.getSelectedDates().get(1)).hasTime(secondSelection.getTimeInMillis());
  }

  @Test public void testOnDateConfiguredListener() {
    final Calendar testCal = Calendar.getInstance(locale);
    view.setDateSelectableFilter(new CalendarPickerView.DateSelectableFilter() {
      @Override public boolean isDateSelectable(Date date) {
        testCal.setTime(date);
        int dayOfWeek = testCal.get(DAY_OF_WEEK);
        return dayOfWeek > 1 && dayOfWeek < 7;
      }
    });
    view.init(minDate, maxDate, locale) //
        .inMode(SINGLE) //
        .withSelectedDate(today.getTime());
    Calendar jumpToCal = Calendar.getInstance(locale);
    jumpToCal.setTime(today.getTime());
    jumpToCal.add(MONTH, 2);
    jumpToCal.set(DAY_OF_WEEK, 1);
    boolean wasAbleToSetDate = view.selectDate(jumpToCal.getTime());
    assertThat(wasAbleToSetDate).isFalse();

    jumpToCal.set(DAY_OF_WEEK, 2);
    wasAbleToSetDate = view.selectDate(jumpToCal.getTime());
    assertThat(wasAbleToSetDate).isTrue();
  }

  @Test public void testWithoutDateSelectedListener() throws Exception {
    view.init(minDate, maxDate, locale) //
        .inMode(SINGLE) //
        .withSelectedDate(today.getTime());

    Calendar jumpToCal = Calendar.getInstance(locale);
    jumpToCal.setTime(today.getTime());
    jumpToCal.add(DATE, 1);
    MonthCellDescriptor cellToClick =
        new MonthCellDescriptor(jumpToCal.getTime(), true, true, true, true, true, 0,
            MonthCellDescriptor.RangeState.NONE);
    view.listener.handleClick(cellToClick);

    assertThat(view.selectedCals.get(0).get(DATE)).isEqualTo(jumpToCal.get(DATE));
  }

  @Test public void testRangeSelectionWithNoInitialSelection() throws Exception {
    view.init(minDate, maxDate, locale) //
        .inMode(RANGE);
    assertThat(view.selectedCals).hasSize(0);
    assertThat(view.selectedCells).hasSize(0);

    Calendar nov18 = buildCal(2012, NOVEMBER, 18);
    view.selectDate(nov18.getTime());
    assertOneDateSelected();

    Calendar nov24 = buildCal(2012, NOVEMBER, 24);
    view.selectDate(nov24.getTime());
    assertRangeSelected();

    assertRangeSelectionBehavior();
  }

  @Test public void testInitWithoutHighlightingCells() {
    view.init(minDate, maxDate, locale) //
        .inMode(SINGLE);

    assertThat(view.highlightedCals).hasSize(0);
    assertThat(view.highlightedCells).hasSize(0);
  }

  @Test public void testHighlightingCells() {
    final Calendar highlightedCal = buildCal(2012, NOVEMBER, 20);

    view.init(minDate, maxDate, locale)
        .inMode(SINGLE)
        .withHighlightedDate(highlightedCal.getTime());

    assertThat(view.highlightedCals).hasSize(1);
    assertThat(view.highlightedCells).hasSize(1);

    List<List<MonthCellDescriptor>> cells = getCells(NOVEMBER, 2012);
    assertThat(cells.get(3).get(2).isHighlighted()).isTrue();
  }

  private void assertRangeSelectionBehavior() {
    // Start a new range in the middle of the current (Nov 18 - Nov 24) one.
    Calendar nov20 = buildCal(2012, NOVEMBER, 20);
    view.selectDate(nov20.getTime());
    assertOneDateSelected();

    // Finish that range.
    Calendar nov26 = buildCal(2012, NOVEMBER, 26);
    view.selectDate(nov26.getTime());
    assertRangeSelected();

    // Start a new range in the middle of the current (Nov 20 - Nov 26) one.
    Calendar nov24 = buildCal(2012, NOVEMBER, 24);
    view.selectDate(nov24.getTime());
    assertOneDateSelected();

    // Only Nov 24 is selected: going backward should start a new range.
    view.selectDate(nov20.getTime());
    assertOneDateSelected();
  }

  @Test public void testRangeWithTwoInitialSelections() throws Exception {
    Calendar nov18 = buildCal(2012, NOVEMBER, 18);
    Calendar nov24 = buildCal(2012, NOVEMBER, 24);
    List<Date> selectedDates = Arrays.asList(nov18.getTime(), nov24.getTime());
    view.init(minDate, maxDate, locale) //
        .inMode(RANGE) //
        .withSelectedDates(selectedDates);
    assertRangeSelected();

    assertRangeSelectionBehavior();
  }

  @Test public void testRangeWithOneInitialSelection() throws Exception {
    Calendar nov18 = buildCal(2012, NOVEMBER, 18);
    Calendar nov24 = buildCal(2012, NOVEMBER, 24);
    List<Date> selectedDates = Arrays.asList(nov18.getTime());
    view.init(minDate, maxDate, locale) //
        .inMode(RANGE) //
        .withSelectedDates(selectedDates);
    assertOneDateSelected();

    view.selectDate(nov24.getTime());
    assertRangeSelected();

    assertRangeSelectionBehavior();
  }

  private void assertRangeSelected() {
    assertThat(view.selectedCals).hasSize(2);
    assertThat(view.selectedCells).hasSize(7);
    assertThat(view.getSelectedDates()).hasSize(7);
  }

  private void assertOneDateSelected() {
    assertThat(view.selectedCals).hasSize(1);
    assertThat(view.selectedCells).hasSize(1);
    assertThat(view.getSelectedDates()).hasSize(1);
  }

  @Test public void testRangeStateOnDateSelections() {
    Calendar startCal = buildCal(2012, NOVEMBER, 17);
    Calendar endCal = buildCal(2012, NOVEMBER, 24);

    view.init(minDate, maxDate, locale) //
        .inMode(RANGE);

    boolean wasAbleToSetDate = view.selectDate(startCal.getTime());
    assertThat(wasAbleToSetDate).isTrue();

    wasAbleToSetDate = view.selectDate(endCal.getTime());
    assertThat(wasAbleToSetDate).isTrue();

    List<List<MonthCellDescriptor>> cells = getCells(NOVEMBER, 2012);
    assertCell(cells, 2, 6, 17, true, true, false, true, FIRST);
    assertCell(cells, 3, 0, 18, true, false, false, true, MIDDLE);
    assertCell(cells, 3, 1, 19, true, false, false, true, MIDDLE);
    assertCell(cells, 3, 2, 20, true, false, false, true, MIDDLE);
    assertCell(cells, 3, 3, 21, true, false, false, true, MIDDLE);
    assertCell(cells, 3, 4, 22, true, false, false, true, MIDDLE);
    assertCell(cells, 3, 5, 23, true, false, false, true, MIDDLE);
    assertCell(cells, 3, 6, 24, true, true, false, true, LAST);
  }

  @Test public void testLocaleSetting() throws Exception {
    view.init(minDate, maxDate, Locale.GERMAN);
    MonthView monthView = (MonthView) view.getAdapter().getView(1, null, null);
    CalendarRowView header = (CalendarRowView) monthView.grid.getChildAt(0);
    TextView firstDay = (TextView) header.getChildAt(0);
    assertThat(firstDay).hasTextString("Mo"); // Montag = Monday
    assertThat(monthView.title).hasTextString("Dezember 2012");
  }

  @Test public void testRightToLeftLocale() throws Exception {
    view.init(minDate, maxDate, new Locale("iw", "IL"));
    MonthView monthView = (MonthView) view.getAdapter().getView(1, null, null);
    CalendarRowView header = (CalendarRowView) monthView.grid.getChildAt(0);
    TextView firstDay = (TextView) header.getChildAt(0);
    assertThat(firstDay).hasTextString("ש"); // Last day of the week (Saturday) is the first cell.
    CalendarRowView firstWeek = (CalendarRowView) monthView.grid.getChildAt(1);
    TextView firstDate = (TextView) firstWeek.getChildAt(0);
    assertThat(firstDate).hasTextString("1");
    CalendarRowView secondWeek = (CalendarRowView) monthView.grid.getChildAt(2);
    TextView secondDate = (TextView) secondWeek.getChildAt(6);
    assertThat(secondDate).hasTextString("2");
    assertThat(monthView.title).hasTextString("דצמבר 2012");
  }

  @Test public void testFirstDayOfWeekIsMonday() throws Exception {
    Locale greatBritain = new Locale("en", "GB");

    // Verify that firstDayOfWeek is actually Monday.
    Calendar cal = Calendar.getInstance(greatBritain);
    assertThat(cal.getFirstDayOfWeek()).isEqualTo(Calendar.MONDAY);

    view.init(minDate, maxDate, greatBritain);
    MonthView monthView = (MonthView) view.getAdapter().getView(1, null, null);
    CalendarRowView header = (CalendarRowView) monthView.grid.getChildAt(0);
    TextView firstDay = (TextView) header.getChildAt(0);
    assertThat(firstDay).hasTextString("Mon"); // Monday!

    List<List<MonthCellDescriptor>> cells = getCells(SEPTEMBER, 2013);
    assertThat(cells).hasSize(6);
    assertCell(cells, 0, 0, 26, false, false, false, false, NONE);
    assertCell(cells, 1, 0, 2, true, false, false, true, NONE);
    assertCell(cells, 5, 0, 30, true, false, false, true, NONE);
  }

  @Test public void testSetShortWeekdays() throws Exception {
    String[] capitalDays = { "", "S", "M", "T", "W", "T", "F", "S" };

    Calendar cal = Calendar.getInstance(Locale.getDefault());
    assertThat(cal.getFirstDayOfWeek()).isEqualTo(Calendar.MONDAY);

    view.init(minDate, maxDate, Locale.getDefault()) //
        .setShortWeekdays(capitalDays);
    MonthView monthView = (MonthView) view.getAdapter().getView(1, null, null);
    CalendarRowView header = (CalendarRowView) monthView.grid.getChildAt(0);
    TextView firstDay = (TextView) header.getChildAt(0);
    assertThat(firstDay).hasTextString("M"); // Monday!
    TextView secondDay = (TextView) header.getChildAt(1);
    assertThat(secondDay).hasTextString("T"); // Tuesday!
    TextView thirdDay = (TextView) header.getChildAt(2);
    assertThat(thirdDay).hasTextString("W"); // Wednesday!
  }

  @Test public void testCellClickInterceptor() throws Exception {
    view.init(minDate, maxDate, Locale.getDefault());
    view.setCellClickInterceptor(new CalendarPickerView.CellClickInterceptor() {
      Calendar cal = Calendar.getInstance(locale);

      @Override public boolean onCellClicked(Date date) {
        cal.setTime(date);
        return cal.get(MONTH) == NOVEMBER && cal.get(DAY_OF_MONTH) == 18;
      }
    });
    Calendar jumpToCal = Calendar.getInstance(locale);
    jumpToCal.setTime(today.getTime());
    jumpToCal.set(DAY_OF_MONTH, 17);
    MonthCellDescriptor cellToClick =
        new MonthCellDescriptor(jumpToCal.getTime(), true, true, true, true, true, 0,
            MonthCellDescriptor.RangeState.NONE);
    view.listener.handleClick(cellToClick);

    assertThat(view.selectedCals.get(0).get(DATE)).isEqualTo(17);

    jumpToCal.set(DAY_OF_MONTH, 18);
    cellToClick = new MonthCellDescriptor(jumpToCal.getTime(), true, true, true, true, true, 0,
        MonthCellDescriptor.RangeState.NONE);
    view.listener.handleClick(cellToClick);

    assertThat(view.selectedCals.get(0).get(DATE)).isEqualTo(17);
  }

  private static void assertCell(List<List<MonthCellDescriptor>> cells, int row, int col,
      int expectedVal, boolean expectedCurrentMonth, boolean expectedSelected,
      boolean expectedToday, boolean expectedSelectable,
      MonthCellDescriptor.RangeState expectedRangeState) {
    final MonthCellDescriptor cell = cells.get(row).get(col);
    assertThat(cell.getValue()).isEqualTo(expectedVal);
    assertThat(cell.isCurrentMonth()).isEqualTo(expectedCurrentMonth);
    assertThat(cell.isSelected()).isEqualTo(expectedSelected);
    assertThat(cell.isToday()).isEqualTo(expectedToday);
    assertThat(cell.isSelectable()).isEqualTo(expectedSelectable);
    assertThat(cell.getRangeState()).isEqualTo(expectedRangeState);
  }

  private List<List<MonthCellDescriptor>> selectDateAndGetCells(int month, int year,
      Calendar selectedDate) {
    view.selectDate(selectedDate.getTime());
    return getCells(month, year);
  }

  private List<List<MonthCellDescriptor>> getCells(int month, int year) {
    Calendar cal = Calendar.getInstance(locale);
    cal.set(DAY_OF_MONTH, 1);
    cal.set(YEAR, year);
    cal.set(MONTH, month);
    return view.getMonthCells(new MonthDescriptor(month, year, cal.getTime(), "January 2012"), cal);
  }

  private Calendar buildCal(int year, @MagicConstant(intValues = {
      JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER,
      DECEMBER
  }) int month, int day) {
    Calendar jumpToCal = Calendar.getInstance(locale);
    jumpToCal.set(year, month, day);
    CalendarPickerView.setMidnight(jumpToCal);
    return jumpToCal;
  }
}

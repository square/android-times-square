// Copyright 2012 Square, Inc.
package com.squareup.calendar;

import android.app.Activity;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DECEMBER;
import static java.util.Calendar.FEBRUARY;
import static java.util.Calendar.JANUARY;
import static java.util.Calendar.MAY;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.NOVEMBER;
import static java.util.Calendar.YEAR;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;

@RunWith(RobolectricTestRunner.class)
public class CalendarPickerViewTest {
  private CalendarPickerView view;
  private Calendar today;

  @Before
  public void setUp() throws Exception {
    view = new CalendarPickerView(new Activity(), null);
    today = Calendar.getInstance();
    today.set(2012, NOVEMBER, 16, 0, 0);
    Date minDate = today.getTime();
    today.set(2013, NOVEMBER, 16, 0, 0);
    Date maxDate = today.getTime();
    today.set(2012, NOVEMBER, 16, 0, 0);
    Date startDate = today.getTime();
    view.today.setTime(startDate);
    view.init(startDate, minDate, maxDate);
  }

  @Test
  public void testInitDecember() throws Exception {
    Calendar dec2012 = Calendar.getInstance();
    dec2012.set(2012, DECEMBER, 1);
    Calendar dec2013 = Calendar.getInstance();
    dec2013.set(2013, DECEMBER, 1);
    view.init(dec2012.getTime(), dec2012.getTime(), dec2013.getTime());
    assertThat(view.months).hasSize(13);
  }

  @Test
  public void testInitJanuary() throws Exception {
    Calendar jan2012 = Calendar.getInstance();
    jan2012.set(2012, JANUARY, 1);
    Calendar jan2013 = Calendar.getInstance();
    jan2013.set(2013, JANUARY, 1);
    view.init(jan2012.getTime(), jan2012.getTime(), jan2013.getTime());
    assertThat(view.months).hasSize(13);
  }

  @Test
  public void testInitMidyear() throws Exception {
    Calendar may2012 = Calendar.getInstance();
    may2012.set(2012, MAY, 1);
    Calendar may2013 = Calendar.getInstance();
    may2013.set(2013, MAY, 1);
    view.init(may2012.getTime(), may2012.getTime(), may2013.getTime());
    assertThat(view.months).hasSize(13);
  }

  @Test
  public void testOnlyShowingFourWeeks() throws Exception {
    List<List<MonthCellDescriptor>> cells = getCells(FEBRUARY, 2015, today);
    assertThat(cells).hasSize(4);

    // Last cell should be 1.
    assertCell(cells, 0, 0, 1, true, false, false, false);

    // Last cell should be 28.
    assertCell(cells, 3, 6, 28, true, false, false, false);
  }

  @Test
  public void testOnlyShowingFiveWeeks() throws Exception {
    List<List<MonthCellDescriptor>> cells = getCells(FEBRUARY, 2013, today);
    assertThat(cells).hasSize(5);

    // First cell is the 27th of January.
    assertCell(cells, 0, 0, 27, false, false, false, false);

    // First day of Feb falls on the 5th cell.
    assertCell(cells, 0, 5, 1, true, false, false, true);

    // Last day of Feb falls on the 5th row, 5th column.
    assertCell(cells, 4, 4, 28, true, false, false, true);

    // Last cell should be March 2nd.
    assertCell(cells, 4, 6, 2, false, false, false, false);
  }

  @Test
  public void testWeirdOverlappingYear() throws Exception {
    List<List<MonthCellDescriptor>> cells = getCells(JANUARY, 2013, today);
    assertThat(cells).hasSize(5);
  }

  @Test
  public void testShowingSixWeeks() throws Exception {
    List<List<MonthCellDescriptor>> cells = getCells(DECEMBER, 2012, today);
    assertThat(cells).hasSize(6);

    // First cell is the 27th of November.
    assertCell(cells, 0, 0, 25, false, false, false, false);

    // First day of December falls on the 6th cell.
    assertCell(cells, 0, 6, 1, true, false, false, true);

    // Last day of December falls on the 6th row, 2nd column.
    assertCell(cells, 5, 1, 31, true, false, false, true);

    // Last cell should be January 5th.
    assertCell(cells, 5, 6, 5, false, false, false, false);
  }

  @Test
  public void testIsSelected() throws Exception {
    Calendar nov29 = Calendar.getInstance();
    nov29.set(YEAR, 2012);
    nov29.set(MONTH, NOVEMBER);
    nov29.set(DAY_OF_MONTH, 29);

    List<List<MonthCellDescriptor>> cells = getCells(NOVEMBER, 2012, nov29);
    assertThat(cells).hasSize(5);
    // Make sure the cell is selected when it's in November.
    assertCell(cells, 4, 4, 29, true, true, false, true);

    cells = getCells(DECEMBER, 2012, nov29);
    assertThat(cells).hasSize(6);
    // Make sure the cell is not selected when it's in December.
    assertCell(cells, 0, 4, 29, false, false, false, false);
  }

  @Test
  public void testTodayIsToday() throws Exception {
    List<List<MonthCellDescriptor>> cells = getCells(NOVEMBER, 2012, today);
    assertCell(cells, 2, 5, 16, true, true, true, true);
  }

  @Test
  public void testSelectabilityInFirstMonth() throws Exception {
    List<List<MonthCellDescriptor>> cells = getCells(NOVEMBER, 2012, today);
    // 10/29 is not selectable because it's in the previous month.
    assertCell(cells, 0, 0, 28, false, false, false, false);
    // 11/1 is not selectable because it's < minDate (11/16/12).
    assertCell(cells, 0, 4, 1, true, false, false, false);
    // 11/16 is selectable because it's == minDate (11/16/12).
    assertCell(cells, 2, 5, 16, true, true, true, true);
    // 11/20 is selectable because it's > minDate (11/16/12).
    assertCell(cells, 3, 2, 20, true, false, false, true);
    // 12/1 is not selectable because it's in the next month.
    assertCell(cells, 4, 6, 1, false, false, false, false);
  }

  @Test
  public void testSelectabilityInLastMonth() throws Exception {
    List<List<MonthCellDescriptor>> cells = getCells(NOVEMBER, 2013, today);
    // 10/29 is not selectable because it's in the previous month.
    assertCell(cells, 0, 0, 27, false, false, false, false);
    // 11/1 is selectable because it's < maxDate (11/16/13).
    assertCell(cells, 0, 5, 1, true, false, false, true);
    // 11/15 is selectable because it's one less than maxDate (11/16/13).
    assertCell(cells, 2, 5, 15, true, false, false, true);
    // 11/16 is not selectable because it's > maxDate (11/16/13).
    assertCell(cells, 2, 6, 16, true, false, false, false);
  }

  @Test
  public void testNullDates() throws Exception {
    final Date validDate = today.getTime();
    try {
      view.init(null, validDate, validDate);
      fail("Should not have been able to pass in a null startDate");
    } catch (IllegalArgumentException expected) {
    }
    try {
      view.init(validDate, null, validDate);
      fail("Should not have been able to pass in a null minDate");
    } catch (IllegalArgumentException expected) {
    }
    try {
      view.init(validDate, validDate, null);
      fail("Should not have been able to pass in a null maxDate");
    } catch (IllegalArgumentException expected) {
    }
  }

  @Test
  public void testZeroDates() throws Exception {
    final Date validDate = today.getTime();
    final Date zeroDate = new Date(0L);
    try {
      view.init(zeroDate, validDate, validDate);
      fail("Should not have been able to pass in a zero startDate");
    } catch (IllegalArgumentException expected) {
    }
    try {
      view.init(validDate, zeroDate, validDate);
      fail("Should not have been able to pass in a zero minDate");
    } catch (IllegalArgumentException expected) {
    }
    try {
      view.init(validDate, validDate, zeroDate);
      fail("Should not have been able to pass in a zero maxDate");
    } catch (IllegalArgumentException expected) {
    }
  }

  @Test
  public void testMinAndMaxMixup() throws Exception {
    final Date minDate = today.getTime();
    today.add(YEAR, -1);
    final Date maxDate = today.getTime();
    try {
      view.init(minDate, minDate, maxDate);
      fail("Should not have been able to pass in a maxDate < minDate");
    } catch (IllegalArgumentException expected) {
    }
  }

  @Test
  public void testSelectedNotInRange() throws Exception {
    final Date minDate = today.getTime();
    today.add(YEAR, 1);
    final Date maxDate = today.getTime();
    today.add(YEAR, 1);
    Date selectedDate = today.getTime();
    try {
      view.init(selectedDate, minDate, maxDate);
      fail("Should not have been able to pass in a selectedDate > maxDate");
    } catch (IllegalArgumentException expected) {
    }
    today.add(YEAR, -5);
    selectedDate = today.getTime();
    try {
      view.init(selectedDate, minDate, maxDate);
      fail("Should not have been able to pass in a selectedDate < minDate");
    } catch (IllegalArgumentException expected) {
    }
  }

  @Test
  public void testNotCallingInit() throws Exception {
    view = new CalendarPickerView(new Activity(), null);
    try {
      view.onMeasure(0, 0);
      fail("Should have thrown an IllegalStateException!");
    } catch (IllegalStateException expected) {
    }
  }

  private static void assertCell(List<List<MonthCellDescriptor>> cells, int row, int col,
      int expectedVal, boolean expectedCurrentMonth, boolean expectedSelected,
      boolean expectedToday, boolean expectedSelectable) {
    final MonthCellDescriptor cell = cells.get(row).get(col);
    assertThat(cell.getValue()).isEqualTo(expectedVal);
    assertThat(cell.isCurrentMonth()).isEqualTo(expectedCurrentMonth);
    assertThat(cell.isSelected()).isEqualTo(expectedSelected);
    assertThat(cell.isToday()).isEqualTo(expectedToday);
    assertThat(cell.isSelectable()).isEqualTo(expectedSelectable);
  }

  private List<List<MonthCellDescriptor>> getCells(int month, int year,
      Calendar selectedDate) {
    Calendar cal = Calendar.getInstance();
    cal.set(MONTH, month);
    cal.set(YEAR, year);
    return view.getMonthCells(new MonthDescriptor(month, year, "January 2012"), cal, selectedDate);
  }
}

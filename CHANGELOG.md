Change Log
==========

Version 1.7.7 *(2017-12-08)*
----------------------------

 * Improved: `tsquare_displayAlwaysDigitNumbers` now also applies to the year in Burmese/Arabic

Version 1.7.6 *(2017-12-04)*
----------------------------

 * Improved: better month name formatting for Chinese/Japenese.
 * New: `tsquare_displayAlwaysDigitNumbers` to override the locale setting and show digits on day cells.


Version 1.7.5 *(2017-10-12)*
----------------------------

 * New: `tsquare_titleTextStyle` allows full styling of the month title view
 * Removed: `tsquare_titleTextColor` (use `tsquare_titleTextStyle` instead)

Version 1.7.4 *(2017-10-03)*
----------------------------

 * New: `tsquare_displayDayNamesHeaderRow` allows hiding the day names header row

Version 1.7.3 *(2017-05-03)*
----------------------------

 * New: withMonthsReverseOrder for reversing the order of months
 * New: RangeState is now accessible so getRangeState actually works
 * New language: Esperanto

Version 1.7.2 *(2017-04-06)*
----------------------------

 * New: clearSelectedDates() method so you can clear an entire RANGE

Version 1.7.1 *(2017-02-28)*
----------------------------

 * Improved: significant performance improvement for large calendar ranges
 * New: isHighlighted() and getRangeState()

Version 1.7.0 *(2016-10-26)*
----------------------------

 * New: Support for specifying the timezone
 * New languages: Serbian and Russian!

Version 1.6.5 *(2016-02-08)*
----------------------------

 * New: Support for arabic numbers
 * New: Performance improvement in CalendarCellView
 * New: Support for completely custom cell views
 * New: Support for a custom "highlighted" color
 * New language: Polish!

Version 1.6.4 *(2015-09-01)*
----------------------------

 * Convert build to gradle to hopefully fix another support-library issue.

Version 1.6.3 *(2015-08-18)*
----------------------------

 * Fix: compatibility with appcompat 23

Version 1.6.2 *(2015-04-28)*
----------------------------

 * New: Right-To-Left layout support.
 * Fix: applying decorators after views are rendered now works

Version 1.6.1 *(2015-04-13)*
----------------------------

 * New: CalendarCellView accessors for various states.  Useful for CalendarCellDecorator implementation.

Version 1.6.0 *(2015-04-10)*
----------------------------

 * New: `CalendarCellDecorator` - Flexibility to do whatever custom logic you want on top of our cells.
 * New: `clearHighlightedDates()` - Unhighlights all previously highlighted days.
 * New: `scrollToDate(Date date)` - Scroll to the month that given date belongs to.
 * New: `containsDate(List<Calendar> selectedCals, Date date)` - if date is within selectedCals
 * Fix: When calculating cell width, make sure to cover all of totalWidth.

Version 1.5.0 *(2014-12-15)*
----------------------------

 * New: Use CellClickInterceptor to implement custom cell-click logic
 * Fix: Correct default header/title text colors.
 * Fix: Disallow passing > 2 dates to withSelectedDates when in RANGE mode.

Version 1.4.1 *(2014-10-27)*
----------------------------

 * New: Individual typeface options for the header and days.
 * Fix: Prevent warnings which might have been logged about relayout. 


Version 1.4.0 *(2014-10-14)*
----------------------------

 * New: Styling options for typeface, hiding the header, and specifying a divider.


Version 1.3.0 *(2014-08-08)*
----------------------------

 * New: `setShortWeekdays` method allows supplying alternate day names in accordance
   with `DateFormatSymbols.setShortWeekdays`.


Version 1.2.1 *(2014-06-09)*
----------------------------

 * Fix: Revert to older maven-android-plugin to fix .aar artifact.


Version 1.2.0 *(2014-06-09)*
----------------------------

 * New: Better support for theming the widget.
 * New: Optional smooth scroll support for `selectDate`.
 * Fix: Properly clear highlighted dates when initializing the widget.


Version 1.1.1 *(2014-02-26)*
----------------------------

 * Fix: Ensure cell drawables properly invalidate on state change.
 * Fix: Revert to older maven-android-plugin to fix .aar artifact.


Version 1.1.0 *(2014-01-28)*
----------------------------

 * New: `OnDateSelected` listener.
 * New: `setSelectedDate` initialization method.
 * New: Multi-date selection and range selection modes.
 * New: `OnDateConfiguredListener` controls which dates are selectable along with
   `OnInvalidDateSelectedListener` when an invalid date selection is attempted.
 * New: Use custom selector states for styling.
 * Fix: Use standalone month name when supported.
 * Fix: Use current calendar's proper first day of week.


Version 1.0.3 *(2013-02-25)*
----------------------------

 * Fix: scroll to selected date after init.


Version 1.0.2 *(2013-02-15)*
----------------------------

 * Fix: Months with no selectable dates are no longer displayed.


Version 1.0.1 *(2013-02-13)*
----------------------------

 * Fix: `NullPointerException` when clicking on day headers.
 * Fix: Better row measurement cachine.


Version 1.0.0 *(2013-01-31)*
----------------------------

Initial release.

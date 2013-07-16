// Copyright 2012 Square, Inc.
package com.squareup.timessquare;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import static android.view.View.MeasureSpec.AT_MOST;
import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.makeMeasureSpec;

/**
 * ViewGroup that draws a grid of calendar cells.  All children must be {@link CalendarRowView}s.
 * The first row is assumed to be a header and no divider is drawn above it.
 */
public class CalendarGridView extends ViewGroup {
  private final Paint dividerPaint = new Paint();
  private int oldWidthMeasureSize;
  private int oldNumRows;

  public CalendarGridView(Context context, AttributeSet attrs) {
    super(context, attrs);
    dividerPaint.setColor(getResources().getColor(R.color.calendar_divider));
  }

  @Override public void addView(View child, int index, ViewGroup.LayoutParams params) {
    if (getChildCount() == 0) {
      ((CalendarRowView) child).setIsHeaderRow(true);
    }
    super.addView(child, index, params);
  }

  @Override protected void dispatchDraw(Canvas canvas) {
    super.dispatchDraw(canvas);
    final ViewGroup row = (ViewGroup) getChildAt(1);
    int top = row.getTop();
    int bottom = getBottom();
    // Left side border.
    final int left = row.getChildAt(0).getLeft() + getLeft();
    canvas.drawLine(left, top, left, bottom, dividerPaint);

    // Each cell's right-side border.
    for (int c = 0; c < 7; c++) {
      int x = left + row.getChildAt(c).getRight() - 1;
      canvas.drawLine(x, top, x, bottom, dividerPaint);
    }
  }

  @Override protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
    final boolean retVal = super.drawChild(canvas, child, drawingTime);
    // Draw a bottom border.
    final int bottom = child.getBottom() - 1;
    canvas.drawLine(child.getLeft(), bottom, child.getRight(), bottom, dividerPaint);
    return retVal;
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    Logr.d("Grid.onMeasure w=%s h=%s", MeasureSpec.toString(widthMeasureSpec),
        MeasureSpec.toString(heightMeasureSpec));
    int widthMeasureSize = MeasureSpec.getSize(widthMeasureSpec);
    if (oldWidthMeasureSize == widthMeasureSize) {
      Logr.d("SKIP Grid.onMeasure");
      setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
      return;
    }
    long start = System.currentTimeMillis();
    oldWidthMeasureSize = widthMeasureSize;
    int cellSize = widthMeasureSize / 7;
    // Remove any extra pixels since /7 is unlikely to give whole nums.
    widthMeasureSize = cellSize * 7;
    int totalHeight = 0;
    final int rowWidthSpec = makeMeasureSpec(widthMeasureSize, EXACTLY);
    final int rowHeightSpec = makeMeasureSpec(cellSize, EXACTLY);
    for (int c = 0, numChildren = getChildCount(); c < numChildren; c++) {
      final View child = getChildAt(c);
      if (child.getVisibility() == View.VISIBLE) {
        if (c == 0) { // It's the header: height should be wrap_content.
          measureChild(child, rowWidthSpec, makeMeasureSpec(cellSize, AT_MOST));
        } else {
          measureChild(child, rowWidthSpec, rowHeightSpec);
        }
        totalHeight += child.getMeasuredHeight();
      }
    }
    final int measuredWidth = widthMeasureSize + 2; // Fudge factor to make the borders show up.
    setMeasuredDimension(measuredWidth, totalHeight);
    Logr.d("Grid.onMeasure %d ms", System.currentTimeMillis() - start);
  }

  @Override protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    long start = System.currentTimeMillis();
    top = 0;
    for (int c = 0, numChildren = getChildCount(); c < numChildren; c++) {
      final View child = getChildAt(c);
      final int rowHeight = child.getMeasuredHeight();
      child.layout(left, top, right, top + rowHeight);
      top += rowHeight;
    }
    Logr.d("Grid.onLayout %d ms", System.currentTimeMillis() - start);
  }

  public void setNumRows(int numRows) {
    if (oldNumRows != numRows) {
      // If the number of rows changes, make sure we do a re-measure next time around.
      oldWidthMeasureSize = 0;
    }
    oldNumRows = numRows;
  }
}

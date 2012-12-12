// Copyright 2012 Square, Inc.
package com.squareup.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import static android.view.View.MeasureSpec.AT_MOST;
import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.makeMeasureSpec;

/** TableRow that draws a divider between each cell. To be used with {@link CalendarGridView}. */
public class CalendarRowView extends ViewGroup implements View.OnClickListener {
  private Paint dividerPaint;
  private MonthView.Listener listener;

  public CalendarRowView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override public void addView(View child, int index, ViewGroup.LayoutParams params) {
    child.setOnClickListener(this);
    super.addView(child, index, params);
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    long start = System.currentTimeMillis();
    final int totalWidth = MeasureSpec.getSize(widthMeasureSpec);
    int cellSize = totalWidth / 7;
    int cellWidthSpec = makeMeasureSpec(cellSize, EXACTLY);
    //noinspection SuspiciousNameCombination
    int cellHeightSpec = cellWidthSpec;
    if (dividerPaint == null) { // This is the header row: make the height wrap_content.
      cellHeightSpec = makeMeasureSpec(cellSize, AT_MOST);
    }
    int rowHeight = 0;
    for (int c = 0, numChildren = getChildCount(); c < numChildren; c++) {
      final View child = getChildAt(c);
      child.measure(cellWidthSpec, cellHeightSpec);
      // The row height is the height of the tallest cell.
      if (child.getMeasuredHeight() > rowHeight) {
        rowHeight = child.getMeasuredHeight();
      }
    }
    final int widthWithPadding = totalWidth + getPaddingLeft() + getPaddingRight();
    final int heightWithPadding = rowHeight + getPaddingTop() + getPaddingBottom();
    setMeasuredDimension(widthWithPadding, heightWithPadding);
    Logr.d("Row.onMeasure " + (System.currentTimeMillis() - start) + "ms");
  }

  @Override protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    long start = System.currentTimeMillis();
    int cellHeight = bottom - top;
    int cellWidth = (right - left) / 7;
    for (int c = 0, numChildren = getChildCount(); c < numChildren; c++) {
      final View child = getChildAt(c);
      child.layout(c * cellWidth, 0, (c + 1) * cellWidth, cellHeight);
    }
    Logr.d("Row.onLayout " + (System.currentTimeMillis() - start) + "ms");
  }

  @Override protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
    final boolean retVal = super.drawChild(canvas, child, drawingTime);
    if (dividerPaint != null) {
      final int index = indexOfChild(child);
      final int top = child.getTop();
      final int bottom = child.getBottom();
      if (index == 0) {
        // Draw a left border.
        canvas.drawLine(child.getLeft() + 1, top, child.getLeft() + 1, bottom, dividerPaint);
      }
      // Draw a right border.
      canvas.drawLine(child.getRight() - 1, top, child.getRight() - 1, bottom, dividerPaint);
    }
    return retVal;
  }

  public void setDividerPaint(Paint dividerPaint) {
    this.dividerPaint = dividerPaint;
  }

  @Override public void onClick(View v) {
    listener.handleClick((MonthCellDescriptor) v.getTag());
  }

  public void setListener(MonthView.Listener listener) {
    this.listener = listener;
  }
}

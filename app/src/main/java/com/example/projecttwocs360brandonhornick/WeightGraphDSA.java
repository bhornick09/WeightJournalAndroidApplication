package com.example.projecttwocs360brandonhornick;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class WeightGraphDSA extends View {
    private Paint linePaint, pointPaint, textPaint, axisPaint; // used to style graph

    // data structures for holding dateLabels and weightData
    private List<Float> weightData = new ArrayList<>();
    private List<String> dateLabels = new ArrayList<>();
    private final int padding = 100; // space for labels

    public WeightGraphDSA(Context context, AttributeSet attrs) {
        // paint used for connecting points
        super(context, attrs);
        linePaint = new Paint();
        linePaint.setColor(Color.parseColor("#BB86FC"));
        linePaint.setStrokeWidth(6f);
        linePaint.setAntiAlias(true);

        // for individual data points
        pointPaint = new Paint();
        pointPaint.setColor(Color.WHITE);
        pointPaint.setAntiAlias(true);

        // labels
        textPaint = new Paint();
        textPaint.setColor(Color.GRAY);
        textPaint.setTextSize(30f);
        textPaint.setAntiAlias(true);

        // axis paint
        axisPaint = new Paint();
        axisPaint.setColor(Color.DKGRAY);
        axisPaint.setStrokeWidth(2f);
    }

    public void setData(List<Float> weights, List<String> dates) {
        this.weightData = weights;
        this.dateLabels = dates;
        invalidate(); // redraw after data is changed or set
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // ensure there are at least 2 data points
        if (weightData.size() < 2) {

            // write message if not
            String message = "Add entries to see your progress!";

            // center text on the canvas
            float x = getWidth() / 2f;
            float y = getHeight() / 2f;

            // use text paint
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setTextSize(40f);

            canvas.drawText(message, x, y, textPaint);
            return;
        }

        // get drawing area w and h
        float width = getWidth() - (padding * 2);
        float height = getHeight() - (padding * 2);

        // Find range for Y scale (DSA Min/Max Algorithm)
        float maxW = 0;
        float minW = Float.MAX_VALUE;

        for (float w : weightData) {
            if (w > maxW) maxW = w;
            if (w < minW) minW = w;
        }

        // add buffer to top and bottom of Y axis
        maxW += 10;
        minW -= 10;
        float range = maxW - minW;

        // horizontal distance between each data point based on size
        float xStep = width / (weightData.size() - 1);

        // main drawing loop
        for (int i = 0; i < weightData.size(); i++) {
            // map data to x and y points
            float x = padding + (i * xStep);
            float y = padding + height - ((weightData.get(i) - minW) / range * height);

            // draw line to next point
            if (i < weightData.size() - 1) {
                float nextX = padding + ((i + 1) * xStep);
                float nextY = padding + height - ((weightData.get(i + 1) - minW) / range * height);
                canvas.drawLine(x, y, nextX, nextY, linePaint);
            }

            // draw the current data point
            canvas.drawCircle(x, y, 8f, pointPaint);

            // Draw date label (Bottom Axis) every few points to avoid crowding
            if (i % (Math.max(1, weightData.size() / 5)) == 0) {
                String date = dateLabels.get(i).substring(5); // MM-DD
                canvas.drawText(date, x - 30, getHeight() - 40, textPaint);
            }
        }

        // draw y axis labels (Min/Max)
        canvas.drawText(String.format("%.1f", maxW), 10, padding, textPaint);
        canvas.drawText(String.format("%.1f", minW), 10, getHeight() - padding, textPaint);
    }
}
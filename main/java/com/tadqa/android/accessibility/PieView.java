package com.tadqa.android.accessibility;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.tadqa.android.R;

public class PieView extends View {

    float[] nutritionValues;
    Paint white;
    Paint caloriesColor, fatColor, proteinColor, carbohydrateColor;
    Paint caloriesOffColor, fatOffColor, proteinOffColor, carbohydrateOffColor;
    RectF points, cuttingPoints, whiteCircle;
    int width = 0, height = 0;

    public PieView(Context context) {
        super(context);

        init(context, null);
    }

    public PieView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        nutritionValues = new float[]{0, 0, 0, 0};

        /**
         * Colors for the outer pie
         */
        fatColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        fatColor.setColor(getResources().getColor(R.color.md_red_400));
        proteinColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        proteinColor.setColor(getResources().getColor(R.color.md_green_400));
        carbohydrateColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        carbohydrateColor.setColor(getResources().getColor(R.color.md_yellow_400));
        caloriesColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        caloriesColor.setColor(getResources().getColor(R.color.md_blue_400));

        /**
         * Color for the white pie
         */
        white = new Paint(Paint.ANTI_ALIAS_FLAG);
        white.setColor(Color.WHITE);

        /**
         * Colors for the inner pie
         */
        fatOffColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        fatOffColor.setColor(getResources().getColor(R.color.md_red_300));
        proteinOffColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        proteinOffColor.setColor(getResources().getColor(R.color.md_green_300));
        carbohydrateOffColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        carbohydrateOffColor.setColor(getResources().getColor(R.color.md_yellow_300));
        caloriesOffColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        caloriesOffColor.setColor(getResources().getColor(R.color.md_blue_300));
    }

    public void setValues(float[] integerList) {
        nutritionValues = integerList;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.width  = w;
        this.height = h;

        /**
         * Setting up the RectF for the pie.
         */
        Log.d("Phone Width", String.valueOf(width));

        int padding = 0;
        int pieThickness = 0;

        if ((width * 2) >= 360 && (width * 2) < 480) {
            padding = 15;
            pieThickness = width - 2 * padding;
        } else if ((width * 2) >= 480 && (width * 2) < 720 ){
            padding = 20;
            pieThickness = width - 2 * padding;
        } else if ((width * 2) >= 720 && (width * 2) < 1080) {
            padding = 30;
            pieThickness = width - 2 * padding;
        } else if ((width * 2) >= 1080 && (width * 2) < 1440) {
            padding = 45;
            pieThickness = width - 2 * padding;
        } else if ((width * 2) >= 1440 && (width * 2) < 2160) {
            padding = 60;
            pieThickness = width - 2 * padding;
        }

        points = new RectF(padding, padding, pieThickness, pieThickness);
        cuttingPoints = new RectF(3 * padding,
                3 * padding,
                width - 4 * padding,
                width - 4 * padding);
        whiteCircle = new RectF(4 * padding,
                4 * padding,
                width - 5 * padding,
                width - 5 * padding);

        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawArc(points,
                0,
                nutritionValues[0],
                true,
                fatColor);

        canvas.drawArc(points,
                nutritionValues[0],
                nutritionValues[1],
                true,
                proteinColor);

        canvas.drawArc(points,
                nutritionValues[0] + nutritionValues[1],
                nutritionValues[2],
                true,
                carbohydrateColor);

        canvas.drawArc(points,
                nutritionValues[0] + nutritionValues[1] + nutritionValues[2],
                nutritionValues[3],
                true,
                caloriesColor);

        canvas.drawArc(cuttingPoints,
                0,
                nutritionValues[0],
                true,
                fatOffColor);

        canvas.drawArc(cuttingPoints,
                nutritionValues[0],
                nutritionValues[1],
                true,
                proteinOffColor);

        canvas.drawArc(cuttingPoints,
                nutritionValues[0] + nutritionValues[1],
                nutritionValues[2],
                true,
                carbohydrateOffColor);

        canvas.drawArc(cuttingPoints,
                nutritionValues[0] + nutritionValues[1] + nutritionValues[2],
                nutritionValues[3],
                true,
                caloriesOffColor);

        canvas.drawArc(whiteCircle,
                0,
                360,
                true,
                white);
    }
}
package com.tadqa.android.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;

public class TrackView extends View {

    int height;
    int width;

    private int status = Status.PENDING;

    Paint activatedTextColor;
    Paint deactivatedTextColor;

    Paint activatedDotColor;
    Paint activatedDotColorOutline;
    Paint deactivatedDotColor;

    Paint activatedLineColor;
    Paint deactivatedLineColor;

    Paint canceledDotColor;

    Paint canceledDotColorOutline;

    Paint canceledTextColor;

    private int lineWidth = 0;
    private int viewPadding = 0;
    private float dot_size = 3;
    private int topMargin = 0;
    private Context mContext;
    private ArrayList<TrackViewData> trackViewDataList;

    public TrackView(Context context) {
        super(context);

        init(context, null);
    }

    public TrackView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        mContext = context;

        float textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 13, mContext.getResources().getDisplayMetrics());

        activatedDotColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        activatedDotColor.setColor(Color.parseColor("#00C853"));

        deactivatedDotColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        deactivatedDotColor.setColor(Color.parseColor("#848484"));

        activatedTextColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        activatedTextColor.setStrokeWidth(2);
        activatedTextColor.setTextSize(textSize);
        activatedTextColor.setColor(Color.parseColor("#00C853"));

        deactivatedTextColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        deactivatedTextColor.setStrokeWidth(2);
        deactivatedTextColor.setTextSize(textSize);
        deactivatedTextColor.setColor(Color.parseColor("#848484"));

        canceledTextColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        canceledTextColor.setStrokeWidth(2);
        canceledTextColor.setTextSize(textSize);
        canceledTextColor.setColor(Color.RED);

        canceledDotColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        canceledDotColor.setColor(Color.RED);

        canceledDotColorOutline = new Paint(Paint.ANTI_ALIAS_FLAG);
        canceledDotColorOutline.setStrokeWidth(3);
        canceledDotColorOutline.setColor(Color.RED);
        canceledDotColorOutline.setStyle(Paint.Style.STROKE);
        canceledDotColorOutline.setStrokeJoin(Paint.Join.ROUND);
        canceledDotColorOutline.setStrokeCap(Paint.Cap.ROUND);

        activatedDotColorOutline = new Paint(Paint.ANTI_ALIAS_FLAG);
        activatedDotColorOutline.setStrokeWidth(3);
        activatedDotColorOutline.setColor(Color.parseColor("#00C853"));
        activatedDotColorOutline.setStyle(Paint.Style.STROKE);
        activatedDotColorOutline.setStrokeJoin(Paint.Join.ROUND);
        activatedDotColorOutline.setStrokeCap(Paint.Cap.ROUND);

        float lineWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, mContext.getResources().getDisplayMetrics());

        activatedLineColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        activatedLineColor.setStrokeWidth(lineWidth);
        activatedLineColor.setColor(Color.parseColor("#00C853"));

        deactivatedLineColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        deactivatedLineColor.setColor(Color.parseColor("#848484"));
        deactivatedLineColor.setStrokeWidth(lineWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawLine(canvas);
        drawCircle(canvas);
        drawText(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        width = getMeasuredWidth();
        /**
         * @viewPadding is the gap between the view boundary and
         *  drawn view.
         */
        viewPadding = 120;

        /**
         * @topMargin is the gap between dot and text drawn below it.
         */
        topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                25,
                mContext.getResources().getDisplayMetrics());

        height = getMeasuredHeight();
        /**
         * Three lines are to be drawn so the
         *  total length of lines would be 3 * lineWidth (minus padding)
         */
        lineWidth = (width - 2 * viewPadding) / 3;
    }

    public float getPixelFromDp(float densityPixel) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, densityPixel, mContext.getResources().getDisplayMetrics());
    }

    public void setStatus(int status) {
        this.status = status;
        invalidate();
    }

    public void drawLine(Canvas canvas) {
        if (status == Status.PENDING) {
            canvas.drawLine(viewPadding, height / 2, viewPadding + lineWidth, height / 2, deactivatedLineColor);
            canvas.drawLine(viewPadding + lineWidth, height / 2, viewPadding + 2 * lineWidth, height / 2, deactivatedLineColor);
            canvas.drawLine(viewPadding + 2 * lineWidth, height / 2, viewPadding + 3 * lineWidth, height / 2, deactivatedLineColor);
        } else if (status == Status.RECEIVED) {
            canvas.drawLine(viewPadding, height / 2, viewPadding + lineWidth, height / 2, activatedLineColor);
            canvas.drawLine(viewPadding + lineWidth, height / 2, viewPadding + 2 * lineWidth, height / 2, deactivatedLineColor);
            canvas.drawLine(viewPadding + 2 * lineWidth, height / 2, viewPadding + 3 * lineWidth, height / 2, deactivatedLineColor);
        } else if (status == Status.DISPATCHED) {
            canvas.drawLine(viewPadding, height / 2, viewPadding + lineWidth, height / 2, activatedLineColor);
            canvas.drawLine(viewPadding + lineWidth, height / 2, viewPadding + 2 * lineWidth, height / 2, activatedLineColor);
            canvas.drawLine(viewPadding + 2 * lineWidth, height / 2, viewPadding + 3 * lineWidth, height / 2, deactivatedLineColor);
        } else if (status == Status.DELIVERED) {
            canvas.drawLine(viewPadding, height / 2, viewPadding + lineWidth, height / 2, activatedLineColor);
            canvas.drawLine(viewPadding + lineWidth, height / 2, viewPadding + 2 * lineWidth, height / 2, activatedLineColor);
            canvas.drawLine(viewPadding + 2 * lineWidth, height / 2, viewPadding + 3 * lineWidth, height / 2, activatedLineColor);
        } else if (status == Status.CANCELED) {
            canvas.drawLine(viewPadding, height / 2, viewPadding + lineWidth, height / 2, deactivatedLineColor);
            canvas.drawLine(viewPadding + lineWidth, height / 2, viewPadding + 2 * lineWidth, height / 2, deactivatedLineColor);
            canvas.drawLine(viewPadding + 2 * lineWidth, height / 2, viewPadding + 3 * lineWidth, height / 2, deactivatedLineColor);
        }
    }

    public void drawCircle(Canvas canvas) {
        switch (status) {
            case Status.CANCELED:
                canvas.drawCircle(viewPadding, height / 2, getPixelFromDp(2 * dot_size), canceledDotColorOutline);
                canvas.drawCircle(viewPadding, height / 2, getPixelFromDp(dot_size), canceledDotColor);
                canvas.drawCircle(viewPadding + lineWidth, height / 2, getPixelFromDp(dot_size), deactivatedDotColor);
                canvas.drawCircle(viewPadding + 2 * lineWidth, height / 2, getPixelFromDp(dot_size), deactivatedDotColor);
                canvas.drawCircle(viewPadding + 3 * lineWidth, height / 2, getPixelFromDp(dot_size), deactivatedDotColor);
                break;
            case Status.PENDING:
                canvas.drawCircle(viewPadding, height / 2, getPixelFromDp(2 * dot_size), activatedDotColorOutline);
                canvas.drawCircle(viewPadding, height / 2, getPixelFromDp(dot_size), activatedDotColor);
                canvas.drawCircle(viewPadding + lineWidth, height / 2, getPixelFromDp(dot_size), deactivatedDotColor);
                canvas.drawCircle(viewPadding + 2 * lineWidth, height / 2, getPixelFromDp(dot_size), deactivatedDotColor);
                canvas.drawCircle(viewPadding + 3 * lineWidth, height / 2, getPixelFromDp(dot_size), deactivatedDotColor);
                break;
            case Status.RECEIVED:
                canvas.drawCircle(viewPadding, height / 2, getPixelFromDp(dot_size), activatedDotColor);
                canvas.drawCircle(viewPadding + lineWidth, height / 2, getPixelFromDp(2 * dot_size), activatedDotColorOutline);
                canvas.drawCircle(viewPadding + lineWidth, height / 2, getPixelFromDp(dot_size), activatedDotColor);
                canvas.drawCircle(viewPadding + 2 * lineWidth, height / 2, getPixelFromDp(dot_size), deactivatedDotColor);
                canvas.drawCircle(viewPadding + 3 * lineWidth, height / 2, getPixelFromDp(dot_size), deactivatedDotColor);
                break;
            case Status.DISPATCHED:
                canvas.drawCircle(viewPadding, height / 2, getPixelFromDp(dot_size), activatedDotColor);
                canvas.drawCircle(viewPadding + 2 * lineWidth, height / 2, getPixelFromDp(2 * dot_size), activatedDotColorOutline);
                canvas.drawCircle(viewPadding + lineWidth, height / 2, getPixelFromDp(dot_size), activatedDotColor);
                canvas.drawCircle(viewPadding + 2 * lineWidth, height / 2, getPixelFromDp(dot_size), activatedDotColor);
                canvas.drawCircle(viewPadding + 3 * lineWidth, height / 2, getPixelFromDp(dot_size), deactivatedDotColor);
                break;
            case Status.DELIVERED:
                canvas.drawCircle(viewPadding, height / 2, getPixelFromDp(dot_size), activatedDotColor);
                canvas.drawCircle(viewPadding + lineWidth, height / 2, getPixelFromDp(dot_size), activatedDotColor);
                canvas.drawCircle(viewPadding + 2 * lineWidth, height / 2, getPixelFromDp(dot_size), activatedDotColor);
                canvas.drawCircle(viewPadding + 3 * lineWidth, height / 2, 2 * getPixelFromDp(dot_size), activatedDotColorOutline);
                canvas.drawCircle(viewPadding + 3 * lineWidth, height / 2, getPixelFromDp(dot_size), activatedDotColor);
                break;
        }
    }

    public void drawText(Canvas canvas) {

        /**
         * Calculating all positions of heading in this view
         */
        setTrackViewData();

        switch (status) {
            case Status.CANCELED:
                canvas.drawText("Canceled",
                        trackViewDataList.get(0).getPositionX(),
                        trackViewDataList.get(0).getPositionY(), canceledTextColor);
                canvas.drawText(trackViewDataList.get(1).getHeading(),
                        trackViewDataList.get(1).getPositionX(),
                        trackViewDataList.get(1).getPositionY(), deactivatedTextColor);
                canvas.drawText(trackViewDataList.get(2).getHeading(),
                        trackViewDataList.get(2).getPositionX(),
                        trackViewDataList.get(2).getPositionY(), deactivatedTextColor);
                canvas.drawText(trackViewDataList.get(3).getHeading(),
                        trackViewDataList.get(3).getPositionX(),
                        trackViewDataList.get(3).getPositionY(), deactivatedTextColor);
                break;
            case Status.PENDING:
                canvas.drawText(trackViewDataList.get(0).getHeading(),
                        trackViewDataList.get(0).getPositionX(),
                        trackViewDataList.get(0).getPositionY(), activatedTextColor);
                canvas.drawText(trackViewDataList.get(1).getHeading(),
                        trackViewDataList.get(1).getPositionX(),
                        trackViewDataList.get(1).getPositionY(), deactivatedTextColor);
                canvas.drawText(trackViewDataList.get(2).getHeading(),
                        trackViewDataList.get(2).getPositionX(),
                        trackViewDataList.get(2).getPositionY(), deactivatedTextColor);
                canvas.drawText(trackViewDataList.get(3).getHeading(),
                        trackViewDataList.get(3).getPositionX(),
                        trackViewDataList.get(3).getPositionY(), deactivatedTextColor);
                break;
            case Status.RECEIVED:
                canvas.drawText(trackViewDataList.get(0).getHeading(),
                        trackViewDataList.get(0).getPositionX(),
                        trackViewDataList.get(0).getPositionY(), activatedTextColor);
                canvas.drawText(trackViewDataList.get(1).getHeading(),
                        trackViewDataList.get(1).getPositionX(),
                        trackViewDataList.get(1).getPositionY(), activatedTextColor);
                canvas.drawText(trackViewDataList.get(2).getHeading(),
                        trackViewDataList.get(2).getPositionX(),
                        trackViewDataList.get(2).getPositionY(), deactivatedTextColor);
                canvas.drawText(trackViewDataList.get(3).getHeading(),
                        trackViewDataList.get(3).getPositionX(),
                        trackViewDataList.get(3).getPositionY(), deactivatedTextColor);
                break;
            case Status.DISPATCHED:
                canvas.drawText(trackViewDataList.get(0).getHeading(),
                        trackViewDataList.get(0).getPositionX(),
                        trackViewDataList.get(0).getPositionY(), activatedTextColor);
                canvas.drawText(trackViewDataList.get(1).getHeading(),
                        trackViewDataList.get(1).getPositionX(),
                        trackViewDataList.get(1).getPositionY(), activatedTextColor);
                canvas.drawText(trackViewDataList.get(2).getHeading(),
                        trackViewDataList.get(2).getPositionX(),
                        trackViewDataList.get(2).getPositionY(), activatedTextColor);
                canvas.drawText(trackViewDataList.get(3).getHeading(),
                        trackViewDataList.get(3).getPositionX(),
                        trackViewDataList.get(3).getPositionY(), deactivatedTextColor);
                break;
            case Status.DELIVERED:
                canvas.drawText(trackViewDataList.get(0).getHeading(),
                        trackViewDataList.get(0).getPositionX(),
                        trackViewDataList.get(0).getPositionY(), activatedTextColor);
                canvas.drawText(trackViewDataList.get(1).getHeading(),
                        trackViewDataList.get(1).getPositionX(),
                        trackViewDataList.get(1).getPositionY(), activatedTextColor);
                canvas.drawText(trackViewDataList.get(2).getHeading(),
                        trackViewDataList.get(2).getPositionX(),
                        trackViewDataList.get(2).getPositionY(), activatedTextColor);
                canvas.drawText(trackViewDataList.get(3).getHeading(),
                        trackViewDataList.get(3).getPositionX(),
                        trackViewDataList.get(3).getPositionY(), activatedTextColor);
                break;
        }
    }

    public static class Status {
        public final static int CANCELED = -1;
        public final static int PENDING = 0;
        public final static int RECEIVED = 1;
        public final static int DISPATCHED = 2;
        public final static int DELIVERED = 3;
    }

    public class TrackViewData {

        String heading = "";
        int positionX = 0;
        int positionY = 0;

        public String getHeading() {
            return heading;
        }

        public void setHeading(String heading) {
            this.heading = heading;
        }

        public int getPositionX() {
            return positionX;
        }

        public void setPositionX(int positionX) {
            this.positionX = positionX;
        }

        public int getPositionY() {
            return positionY;
        }

        public void setPositionY(int positionY) {
            this.positionY = positionY;
        }
    }

    public int[] getTextPoints(String text, Paint paint, int topMargin, int referenceX, int referenceY) {
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return new int[]{referenceX - bounds.width() / 2, referenceY - bounds.height() / 2 + topMargin};
    }

    public void setTrackViewData() {

        trackViewDataList = new ArrayList<>();

        TrackViewData pendingTrackViewData = new TrackViewData();
        pendingTrackViewData.setHeading("Pending");
        pendingTrackViewData.setPositionX(getTextPoints("Pending", activatedTextColor, topMargin, viewPadding, height / 2)[0]);
        pendingTrackViewData.setPositionY(getTextPoints("Pending", activatedTextColor, topMargin, viewPadding, height / 2)[1]);
        trackViewDataList.add(pendingTrackViewData);

        TrackViewData preparedTrackViewData = new TrackViewData();
        preparedTrackViewData.setHeading("Received");
        preparedTrackViewData.setPositionX(getTextPoints("Received", activatedTextColor, topMargin, viewPadding + lineWidth, height / 2)[0]);
        preparedTrackViewData.setPositionY(getTextPoints("Received", activatedTextColor, topMargin, viewPadding + lineWidth, height / 2)[1]);
        trackViewDataList.add(preparedTrackViewData);

        TrackViewData dispatchedTrackViewData = new TrackViewData();
        dispatchedTrackViewData.setHeading("Dispatched");
        dispatchedTrackViewData.setPositionX(getTextPoints("Dispatched", activatedTextColor, topMargin, viewPadding + 2 * lineWidth, height / 2)[0]);
        dispatchedTrackViewData.setPositionY(getTextPoints("Dispatched", activatedTextColor, topMargin, viewPadding + 2 * lineWidth, height / 2)[1]);
        trackViewDataList.add(dispatchedTrackViewData);

        TrackViewData deliveredTrackViewData = new TrackViewData();
        deliveredTrackViewData.setHeading("Delivered");
        deliveredTrackViewData.setPositionX(getTextPoints("Delivered", activatedTextColor, topMargin, viewPadding + 3 * lineWidth, height / 2)[0]);
        deliveredTrackViewData.setPositionY(getTextPoints("Delivered", activatedTextColor, topMargin, viewPadding + 3 * lineWidth, height / 2)[1]);
        trackViewDataList.add(deliveredTrackViewData);
    }
}

package team.lf.task421;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CustomView extends View {

    private Paint mMainTextPaint;
    private float mInnerRadius;
    private float mSectorRadius;
    private float mTotalRadius;

    private List<Sector> mSectors;
    private int mCountOfBlue;
    private int mTotalSectors;

    private RectF mStandartBounds;
    private RectF mTotalBounds;
    private float mValueSum = 0;
    private float mStrokeSize = 0.1f;
    private Paint mInnerCirclePaint;




    public CustomView(Context context) {
        super(context);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {


        mMainTextPaint = new Paint();
        mMainTextPaint.setColor(getResources().getColor(R.color.chartBlue));
        mMainTextPaint.setStyle(Paint.Style.STROKE);
        mMainTextPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.large_text));
        mMainTextPaint.setTextAlign(Paint.Align.CENTER);
        mMainTextPaint.setAntiAlias(true);


        mInnerCirclePaint = new Paint();
        mInnerCirclePaint.setColor(context.getResources().getColor(R.color.chartRisk));
        mInnerCirclePaint.setStyle(Paint.Style.FILL);
        mInnerCirclePaint.setAntiAlias(true);

        //attrs
        TypedArray typedArray = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.CustomView, 0, 0);
        mCountOfBlue =typedArray.getInt(R.styleable.CustomView_count, 0);
        mTotalSectors =typedArray.getInt(R.styleable.CustomView_total, 0);

        mSectors = sectorFactory(mCountOfBlue, mTotalSectors, mStrokeSize);


        for (Sector s : mSectors) {
            mValueSum += s.mValue;
        }

        for (Sector s : mSectors) {
            s.calculate(mValueSum);
        }

        mStandartBounds = new RectF();
        mTotalBounds = new RectF();


    }

    public List<Sector> sectorFactory(int count, int total, float strokeSize){
        List<Sector> sectors = new ArrayList<>(total);
        if(count == total || count> total) {
            sectors.add(new Sector(1f, getResources().getColor(R.color.chartBlue)));
            return sectors;
        } else if(count == 0) {
            sectors.add(new Sector(1f, getResources().getColor(R.color.chartGrey)));
            return sectors;
        } else  {
            for(int i = 0; i<count; i++){
                sectors.add(new Sector(1f, getResources().getColor(R.color.chartBlue)));
                sectors.add(new Sector(strokeSize, getResources().getColor(R.color.chartRisk)));
            }
            for(int i = 0; i<(total-count); i++){
                sectors.add(new Sector(1f, getResources().getColor(R.color.chartGrey)));
                sectors.add(new Sector(strokeSize, getResources().getColor(R.color.chartRisk)));
            }
            return sectors;
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        mInnerRadius = mMainTextPaint.measureText("10");
        mSectorRadius = mInnerRadius * 1.1f;
        mTotalRadius = mInnerRadius * 1.2f;
        int desiredDiameter = (int) (mInnerRadius * 4f);
        int measuredWidth = resolveSize(desiredDiameter, widthMeasureSpec);
        int measuredHeight = resolveSize(desiredDiameter, heightMeasureSpec);
        mTotalBounds.set(0, 0, measuredWidth, measuredHeight);
        mStandartBounds.set(mTotalBounds);
        mStandartBounds.inset(measuredWidth * 0.22f, measuredHeight * 0.22f);
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int cx = getWidth() / 2;
        int cy = getHeight() / 2;

        float startAngle = -90f + mStrokeSize * 30;
        for (Sector s : mSectors) {
            startAngle = s.draw(canvas, mStandartBounds, startAngle);
        }

        canvas.drawCircle(cx, cy, mInnerRadius, mInnerCirclePaint);
        canvas.drawText(String.valueOf(mCountOfBlue),cx, cy+mMainTextPaint.getTextSize()/3,mMainTextPaint );

    }


    private static class Sector {
        private float mValue;
        private Paint mPaint;

        private float mAngle;
        private float mPercent;
        private float mStartAngle;
        private float mEndAngle;

        private Sector(float value, int color) {
            mValue = value;
            mPaint = new Paint();
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setAntiAlias(true);
            mPaint.setColor(color);
        }

        private float draw(Canvas canvas, RectF bounds, float startAngle) {
            canvas.drawArc(bounds, startAngle, mAngle, true, mPaint);
            return startAngle + mAngle;
        }


        private void calculate(float valueSum) {
            mAngle = mValue / valueSum * 360f;
            mPercent = mValue / valueSum * 100f;
        }
    }
}

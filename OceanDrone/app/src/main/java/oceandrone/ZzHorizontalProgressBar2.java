package oceandrone;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;


/**
 * A Horizontal ProgressBar that is customized easily.
 * Created by zz on 2016/9/22.
 */
public class ZzHorizontalProgressBar2 extends View {

    private int max;
    private int progress;
    private int bgColor;
    private int progressColor;
    private int padding;
    private boolean openGradient;
    private int gradientFrom;
    private int gradientTo;
    private boolean showSecondProgress;
    private int secondProgress;
    private int secondProgressShape;
    private String text;

    private Paint secondProgressPaint;
    private Paint secondGradientPaint;
    private Paint progressPaint;
    private Paint gradientPaint;
    private Paint bgPaint;
    private boolean openSecondGradient;
    private int secondGradientFrom;
    private int secondGradientTo;
    private int secondProgressColor;
    /**
     * 画文字时底部的坐标
     */
    private float textBottomY;
    /**
     * 画中间的百分比文字的画笔
     */
    private Paint paintText ;
    /**
     * 百分比文字的颜色
     */
    private int paintTextColor = 0xff666666;
    /**
     * 包裹文字的矩形
     */
    private Rect rect = new Rect();
    /**
     * 要画的文字的宽度
     */
    private int textWidth;
    /**
     * 得到自定义视图的Y轴中心点
     */
    private int viewCenterY;


    public ZzHorizontalProgressBar2(Context context) {
        super(context);
        init(context, null);
    }

    public ZzHorizontalProgressBar2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ZzHorizontalProgressBar2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        initAttrs(context, attrs);
        initPaths();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ZzHorizontalProgressBar);
        max = a.getInteger(R.styleable.ZzHorizontalProgressBar_zpb_max, 100);
        progress = a.getInteger(R.styleable.ZzHorizontalProgressBar_zpb_progress, 0);
        bgColor = a.getColor(R.styleable.ZzHorizontalProgressBar_zpb_bg_color, 0xffffffff);
        progressColor = a.getColor(R.styleable.ZzHorizontalProgressBar_zpb_pb_color, 0xffFF4081);
        padding = a.getDimensionPixelSize(R.styleable.ZzHorizontalProgressBar_zpb_padding, 2);
        showSecondProgress = a.getBoolean(R.styleable.ZzHorizontalProgressBar_zpb_show_second_progress, false);
        openGradient = a.getBoolean(R.styleable.ZzHorizontalProgressBar_zpb_open_gradient, false);
        gradientFrom = a.getColor(R.styleable.ZzHorizontalProgressBar_zpb_gradient_from, 0xff33ffcc);
        gradientTo = a.getColor(R.styleable.ZzHorizontalProgressBar_zpb_gradient_to, 0xffFF4081);
        //text=a.getInt(R.styleable.ZzHorizontalProgressBar_zpb_text,"");
        openSecondGradient = a.getBoolean(R.styleable.ZzHorizontalProgressBar_zpb_open_second_gradient, false);
        a.recycle();
    }

    private void initPaths() {
        progressPaint = new Paint();
        progressPaint.setColor(progressColor);
        progressPaint.setStyle(Paint.Style.FILL);
        progressPaint.setAntiAlias(true);


        gradientPaint = new Paint();
        gradientPaint.setStyle(Paint.Style.FILL);
        gradientPaint.setAntiAlias(true);


        bgPaint = new Paint();
        bgPaint.setColor(bgColor);
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setAntiAlias(true);

        paintText = new Paint();
        paintText.setColor(paintTextColor);
        paintText.setTextSize(30);
        paintText.setAntiAlias(true);
        paintText.setTypeface(Typeface.DEFAULT);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
        drawProgress(canvas);
    }

    private void drawProgress(Canvas canvas) {
        int width = getWidth();
        if (width % 2 != 0) {
            //Fix Me
            width = width - 1;
        }
        float percent = 0;
        if (max != 0) {
            percent = progress * 1.0f / max;
        }
        int progressHeight = getHeight() - padding * 2;
        if (progressHeight % 2 != 0) {
            progressHeight = progressHeight - 1;
        }
        if (openGradient) {
            int progressWidth = width - padding * 2;
            float mDx = progressWidth * percent;

            int colors[] = new int[2];
            float positions[] = new float[2];
            //from color
            colors[0] = gradientFrom;
            positions[0] = 0;
            //to color
            colors[1] = gradientTo;
            positions[1] = 1;
            LinearGradient shader = new LinearGradient(
                    padding + progressHeight / 2, padding, padding + progressHeight / 2 + mDx, padding + progressHeight,
                    colors,
                    positions,
                    Shader.TileMode.MIRROR);
            //gradient
            gradientPaint.setShader(shader);

            int radius = width > getHeight() ? getHeight() / 2 : width / 2;
            if (mDx < getHeight()) {
                //left circle
                canvas.drawCircle(padding + progressHeight / 2, padding + progressHeight / 2, progressHeight / 2, gradientPaint);
            } else {
                //progress line
                RectF rectF = new RectF(padding, padding, padding + mDx, padding + progressHeight);
                canvas.drawRoundRect(rectF, radius, radius, gradientPaint);
            }

        } else {
            int progressWidth = width - padding * 2 - progressHeight;
            float dx = progressWidth * percent;
            progressPaint.setColor(progressColor);
            //left circle
            canvas.drawCircle(padding + progressHeight / 2, padding + progressHeight / 2, progressHeight / 2, progressPaint);
            //right circle
            canvas.drawCircle(padding + progressHeight / 2 + dx, padding + progressHeight / 2, progressHeight / 2, progressPaint);
            //middle line
            RectF midRecf = new RectF(padding + progressHeight / 2, padding, padding + progressHeight / 2 + dx, padding + progressHeight);
            canvas.drawRect(midRecf, progressPaint);
        }

        canvas.drawText("完成时间预估"+progress + "%", 0, progressHeight-padding, paintText);

    }

    private void drawBackground(Canvas canvas) {
        int bgHeight = getHeight();
        if (bgHeight % 2 != 0) {
            bgHeight = bgHeight - 1;
        }
        int width = getWidth();
        if (width % 2 != 0) {
            //Fix Me
            width = width - 1;
        }

        //left circle
        canvas.drawCircle(bgHeight / 2, bgHeight / 2, bgHeight / 2, bgPaint);
        //right circle
        canvas.drawCircle(width - bgHeight / 2, bgHeight / 2, bgHeight / 2, bgPaint);
        //middle line
        RectF midRecf = new RectF(bgHeight / 2, 0, width - bgHeight / 2, bgHeight);
        canvas.drawRect(midRecf, bgPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
        invalidate();
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    public boolean isShowSecondProgress() {
        return showSecondProgress;
    }

    public void setShowSecondProgress(boolean showSecondProgress) {
        this.showSecondProgress = showSecondProgress;
        invalidate();
    }

    public int getSecondProgress() {
        return secondProgress;
    }

    public void setSecondProgress(int secondProgress) {
        this.secondProgress = secondProgress;
        invalidate();
    }

    public int getSecondProgressShape() {
        return secondProgressShape;
    }

    public void setSecondProgressShape(int secondProgressShape) {
        this.secondProgressShape = secondProgressShape;
        invalidate();
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
        bgPaint.setColor(bgColor);
        invalidate();
    }

    public boolean isOpenSecondGradient() {
        return openSecondGradient;
    }

    public void setOpenSecondGradient(boolean openSecondGradient) {
        this.openSecondGradient = openSecondGradient;
        invalidate();
    }

    public int getSecondGradientFrom() {
        return secondGradientFrom;
    }

    public void setSecondGradientFrom(int secondGradientFrom) {
        this.secondGradientFrom = secondGradientFrom;
        invalidate();
    }

    public int getSecondGradientTo() {
        return secondGradientTo;
    }

    public void setSecondGradientTo(int secondGradientTo) {
        this.secondGradientTo = secondGradientTo;
        invalidate();
    }

    public int getSecondProgressColor() {
        return secondProgressColor;
    }

    public void setSecondProgressColor(int secondProgressColor) {
        this.secondProgressColor = secondProgressColor;
        secondProgressPaint.setColor(secondProgressColor);
        invalidate();
    }

    public int getProgressColor() {
        return progressColor;
    }

    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
        progressPaint.setColor(progressColor);
        invalidate();
    }

    public int getPadding() {
        return padding;
    }

    public void setPadding(int padding) {
        this.padding = padding;
        invalidate();
    }

    /**
     * get the percentage value of progress and max.
     *
     * @return percentage value
     */
    public int getPercentage() {
        if (max == 0) {
            return 0;
        }
        return (int) (progress * 100.0 / max);
    }


}

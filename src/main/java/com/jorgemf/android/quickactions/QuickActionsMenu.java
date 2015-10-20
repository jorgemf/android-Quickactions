package com.jorgemf.android.quickactions;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class QuickActionsMenu extends View {

    private Animation hideAnimation;

    private Animation viewAnimation;

    private QuickActionsListener listener;

    private ArgbEvaluator mArgbEvaluator = new ArgbEvaluator();

    private AccelerateInterpolator mInterpolator = new AccelerateInterpolator(1);

    private float radius;

    private float radiusAction;

    private float textPadding;

    private float textMargin;

    private float scaleGrow;

    private float angleActions;

    private int backgroundColor;

    private int actionBackgroundColor;

    private int actionBackgroundActiveColor;

    private int textColor;

    private int textSize;

    private ArrayList<QuickAction> quickActions;

    private Object tag;

    private Paint paintBackground;

    private Paint paintActionsBackground;

    private Paint paintText;

    private float height;

    private float halfHeight;

    private float width;

    private float halfWidth;

    private int actionActive;

    private float touchX;

    private float touchY;

    private Rect bounds;

    private RectF textBounds;

    private int[] auxLeftTop;

    public QuickActionsMenu(Context context) {
        this(context, null);
    }

    public QuickActionsMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QuickActionsMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        hideAnimation = AnimationUtils.loadAnimation(context, R.anim.quick_action_hide);
        hideAnimation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        viewAnimation = AnimationUtils.loadAnimation(context, R.anim.quick_action_show);

        quickActions = new ArrayList<QuickAction>();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.QuickActions, defStyle, 0);
        //noinspection ConstantConditions
        final float scale = context.getResources().getDisplayMetrics().density;

        //noinspection ConstantConditions
        radius = a.getDimensionPixelSize(R.styleable.QuickActions_quickaction_radius, (int) (80 * scale));
        radiusAction = a.getDimensionPixelSize(R.styleable.QuickActions_quickaction_radiusAction, (int) (20 * scale));
        textPadding = a.getDimensionPixelSize(R.styleable.QuickActions_quickaction_textPadding, (int) (6 * scale));
        textMargin = a.getDimensionPixelSize(R.styleable.QuickActions_quickaction_marginPadding, (int) (6 * scale));
        scaleGrow = a.getFloat(R.styleable.QuickActions_quickaction_scaleGrow, 0.28f) + 1.0f;
        angleActions = (float) (a.getFloat(R.styleable.QuickActions_quickaction_angleActions, 40) * 2 * Math.PI / 360);
        backgroundColor = a.getColor(R.styleable.QuickActions_quickaction_backgroundColor, Color.WHITE & 0x99FFFFFF); // add some transparency
        actionBackgroundColor = a.getColor(R.styleable.QuickActions_quickaction_actionBackgroundColor, Color.LTGRAY);
        actionBackgroundActiveColor = a.getColor(R.styleable.QuickActions_quickaction_actionBackgroundActiveColor, Color.WHITE);
        textColor = a.getColor(R.styleable.QuickActions_android_textColor, Color.WHITE);
        textSize = a.getDimensionPixelSize(R.styleable.QuickActions_android_textSize, (int) (14 * scale));

        a.recycle();

        if (isInEditMode()) {
            Resources resources = getResources();
            assert resources != null;
            quickActions.add(new QuickAction("text 1", resources.getDrawable(android.R.drawable.ic_btn_speak_now)));
            quickActions.add(new QuickAction("text 2", resources.getDrawable(android.R.drawable.ic_delete)));
            quickActions.add(new QuickAction("text 3", resources.getDrawable(android.R.drawable.ic_input_add)));
//			quickActions.add(new QuickAction("text 4", resources.getDrawable(android.R.drawable.ic_dialog_alert)));
        }

        height = (radius + radiusAction * scaleGrow + radiusAction * (scaleGrow - 1)) * 2 + textSize * 2.5f + textPadding * 2 + textMargin * 2;
        halfHeight = height / 2;
        width = (radius + radiusAction * scaleGrow + radiusAction * (scaleGrow - 1)) * 2 + textSize;
        halfWidth = width / 2;

        paintBackground = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBackground.setShader(new RadialGradient(halfWidth, halfHeight, radius + radiusAction, backgroundColor, backgroundColor & 0x00FFFFFF, TileMode.MIRROR));

        paintActionsBackground = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintActionsBackground.setColor(actionBackgroundColor);
        paintActionsBackground.setStyle(Paint.Style.FILL_AND_STROKE);

        paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText.setColor(textColor);
        paintText.setTextSize(textSize);
        paintText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        bounds = new Rect();
        textBounds = new RectF();

        auxLeftTop = new int[2];
    }

    public void show(float x, float y, Object tag) {
        this.tag = tag;
        ViewGroup parentView = (ViewGroup) getParent();
        int[] location = new int[2];
        parentView.getLocationOnScreen(location);
        x -= location[0];
        y -= location[1];
        ViewGroup.LayoutParams params = getLayoutParams();
        if (params instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) params;
            layoutParams.setMargins((int) (x - halfWidth), (int) (y - halfHeight), 0, 0);
            setLayoutParams(layoutParams);
        } else {
            setTranslationX(x - halfWidth);
            setTranslationY(y - halfHeight);
        }
        touchX = halfWidth;
        touchY = halfHeight;
        if (getVisibility() != View.VISIBLE) {
            setVisibility(View.VISIBLE);
//            startAnimation(viewAnimation);
            listener.onShow(this, tag, x, y);
        }
    }

    public void hide() {
        if (getVisibility() == View.VISIBLE) {
            startAnimation(hideAnimation);
        }
    }

    public void setListener(QuickActionsListener listener) {
        this.listener = listener;
    }

    public QuickAction addAction(int textResId, int iconResId) {
        QuickAction quickAction = new QuickAction(getContext(), textResId, iconResId);
        this.quickActions.add(quickAction);
        return quickAction;
    }

    public QuickAction addAction(int textResId, int iconResId, int colorActiveBackgroundResId, int iconActiveResId) {
        QuickAction quickAction = new QuickAction(getContext(), textResId, iconResId, colorActiveBackgroundResId, iconActiveResId);
        this.quickActions.add(quickAction);
        return quickAction;
    }

    public QuickAction addAction(CharSequence text, Drawable icon) {
        QuickAction quickAction = new QuickAction(text, icon);
        this.quickActions.add(quickAction);
        return quickAction;
    }

    public QuickAction addAction(CharSequence text, Drawable icon, int colorActiveBackground, Drawable iconActive) {
        QuickAction quickAction = new QuickAction(text, icon, colorActiveBackground, iconActive);
        this.quickActions.add(quickAction);
        return quickAction;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension((int) width, (int) height);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (actionActive < 0) {
                    hide();
                    if (listener != null) {
                        listener.onMenuDismiss(this, tag);
                    }
                } else {
                    hide();
                    if (listener != null) {
                        listener.onActionSelected(this, tag, actionActive);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float screenX = event.getRawX();
                float screenY = event.getRawY();
                getLocationOnScreen(auxLeftTop);
                touchX = screenX - auxLeftTop[0];
                touchY = screenY - auxLeftTop[1];
//				System.out.println("touchX=[" + (int) touchX + "] " + "touchY=[" + (int) touchY + "]" + "  size=" + (int) size + " left,top=" + (int) getLeft() + "," + (int) getTop() + " screenX,screenY=" + (int) screenX + "," + (int) screenY);
                break;
            default:
                // nothing
        }
//		System.out.println("touchX=[" + touchX + "] " + "touchY=[" + touchY + "]" + " action=" + event.getAction() + "  size=" + size + " left,top=" + getLeft() + "," + getTop() + " screenX,screenY=" + screenX + "," + screenY);
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        View view = (View) getParent();
        if (view != null) {
            actionActive = -1;
            int parentWidth = view.getWidth();
            int parentHeight = view.getHeight();
            ViewGroup.LayoutParams params = getLayoutParams();
            float positionX;
            float positionY;
            if (params instanceof RelativeLayout.LayoutParams) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) params;
                positionX = layoutParams.leftMargin;
                positionY = layoutParams.topMargin;
            } else {
                positionX = getTranslationX();
                positionY = getTranslationY();
            }
            float centerY = positionY + halfHeight;
            float centerX = positionX + halfWidth;
            double angle = 0;
            if (centerY < halfHeight || centerY > parentHeight - halfHeight || centerX < halfWidth || centerX > parentWidth - halfWidth) {
                if (centerX < halfWidth && centerY < halfWidth) { // top left corner
                    float vectorY = (positionY + halfHeight) - halfHeight;
                    float vectorX = (positionX + halfWidth) - halfWidth;
                    angle = Math.atan2(vectorY, vectorX);
                } else if (centerX > parentWidth - halfWidth && centerY < halfHeight) { // top right corner
                    float vectorY = (positionY + halfHeight) - halfHeight;
                    float vectorX = (positionX + halfWidth) - (parentWidth - halfWidth);
                    angle = Math.atan2(vectorY, vectorX);
                } else if (centerX > parentWidth - halfWidth && centerY > parentHeight - halfHeight) { // bottom right corner
                    float vectorY = (positionY + halfHeight) - (parentHeight - halfHeight);
                    float vectorX = (positionX + halfWidth) - (parentWidth - halfWidth);
                    angle = Math.atan2(vectorY, vectorX);
                } else if (centerX < halfWidth && centerY > parentHeight - halfHeight) { // bottom left corner
                    float vectorY = (positionY + halfHeight) - (parentHeight - halfHeight);
                    float vectorX = (positionX + halfWidth) - halfWidth;
                    angle = Math.atan2(vectorY, vectorX);
                } else {
                    float vectorY = (positionY + halfHeight) / parentHeight - 0.5f;
                    float vectorX = (positionX + halfWidth) / parentWidth - 0.5f;
                    angle = Math.atan2(vectorY, vectorX);
                }
            } else {
                angle = Math.PI / 2;
            }
            double initialAngle = angle - Math.PI - ((quickActions.size() - 1) * angleActions) / 2;
            canvas.drawCircle(halfWidth, halfHeight, radius, paintBackground);
            int actionNumber = 0;
            for (QuickAction action : quickActions) {
                double calculatedAngle = initialAngle + actionNumber * angleActions;
                float x = (float) Math.cos(calculatedAngle) * radius + halfWidth;
                float y = (float) Math.sin(calculatedAngle) * radius + halfHeight;
                if (isInEditMode() && actionNumber == 0) {
                    touchX = x + radiusAction / 2;
                    touchY = y + radiusAction / 2;
                }
                float dx = x - touchX;
                float dy = y - touchY;
                float distance = (float) Math.sqrt(dx * dx + dy * dy);
                boolean activeAction = distance < radiusAction * scaleGrow;
                Drawable icon = action.getImage();
                if (activeAction) {
                    actionActive = actionNumber;
                    if (action.getImageActive() != null) {
                        icon = action.getImageActive();
                    }
                }
                float scaleRadius = 1f;
                distance -= radiusAction;
                float minDistance = radius / 3;
                if (distance < minDistance) {
                    if (distance < 0) {
                        distance = 0;
                    }
                    float ratio = distance / minDistance;
                    scaleRadius = scaleGrow - (scaleGrow - 1) * ratio * ratio;
                    float calculatedRadius = radius + radiusAction * (scaleRadius - 1);
                    x = (float) Math.cos(calculatedAngle) * calculatedRadius + halfWidth;
                    y = (float) Math.sin(calculatedAngle) * calculatedRadius + halfHeight;
                    float colorRatio = mInterpolator.getInterpolation(1 - ratio);
                    paintActionsBackground.setColor(
                            (Integer) mArgbEvaluator.evaluate(
                                    colorRatio,
                                    actionBackgroundColor,
                                    action.getColorBackgroundActive())
                    );
                }
                float scaledRadius = radiusAction * scaleRadius;
                canvas.drawCircle(x, y, scaledRadius, paintActionsBackground);
                paintActionsBackground.setColor(actionBackgroundColor);

                icon.setBounds((int) (x - scaledRadius), (int) (y - scaledRadius), (int) (x + scaledRadius), (int) (y + scaledRadius));
                canvas.save();
                icon.draw(canvas);
                canvas.restore();

                action.x = x;
                action.y = y;
                action.active = activeAction;

                actionNumber++;
            }
            actionNumber = 0;
            for (QuickAction action : quickActions) {
                if (action.active) {
                    String text = action.getText().toString();
                    paintText.getTextBounds(text, 0, text.length(), bounds);
//					canvas.drawText("bounds   " + bounds.width() + "x" + bounds.height(), textMargin, (textSize + textPadding) * (1 + actionNumber) + textPadding, paintText);
                    float textX = action.x - bounds.width() / 2;
                    float textY = action.y - radiusAction * scaleGrow - textPadding - textMargin;
                    textBounds.set(
                            textX - textPadding, // left
                            textY - bounds.height() - textPadding,// top
                            textX + bounds.width() + textPadding,// right
                            textY + textPadding// bottom
                    );
                    canvas.drawRoundRect(textBounds, textPadding, textPadding, paintActionsBackground);
                    canvas.drawText(text, textX, textY, paintText);
                }
                actionNumber++;
            }

//			paintText.setColor(Color.RED);
//			canvas.drawLine(0, touchY, size, touchY, paintText);
//			canvas.drawLine(touchX, 0, touchX, size, paintText);
//			paintText.setColor(textColor);

//			paintText.setColor(Color.RED);
//			canvas.drawText("parentWidth   " + parentWidth, textMargin, (textSize + textPadding) * 1 + textPadding, paintText);
//			canvas.drawText("parentHeight   " + parentHeight, textMargin, (textSize + textPadding) * 2 + textPadding, paintText);
//			canvas.drawText("getTranslationX() " + getTranslationX(), textMargin, (textSize + textPadding) * 3 + textPadding, paintText);
//			canvas.drawText("getTranslationY() " + getTranslationY(), textMargin, (textSize + textPadding) * 4 + textPadding, paintText);
//			canvas.drawText("size    " + size, textMargin, (textSize + textPadding) * 5 + textPadding, paintText);
//			canvas.drawText("vector    " + vectorX + " " + vectorY, textMargin, (textSize + textPadding) * 6 + textPadding, paintText);
//			canvas.drawText("angle    " + Math.round(angle * 100) / 100f + " " + Math.round(angle / Math.PI / 2 * 360), textMargin, (textSize + textPadding) * 7 + textPadding, paintText);
        }

    }

}

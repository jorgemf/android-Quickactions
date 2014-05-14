package com.jorgemf.android.quickactions;

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
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class QuickActionsMenu extends View {

	private Animation hideAnimation;

	private Animation viewAnimation;

	private QuickActionsListener listener;

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

	private float size;

	private float halfSize;

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
		viewAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationEnd(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
		viewAnimation.setFillEnabled(false);

		quickActions = new ArrayList<QuickAction>();

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.QuickActions, defStyle, 0);
		//noinspection ConstantConditions
		final float scale = context.getResources().getDisplayMetrics().density;

		//noinspection ConstantConditions
		radius = a.getDimensionPixelSize(R.styleable.QuickActions_quickaction_radius, (int) (80 * scale));
		radiusAction = a.getDimensionPixelSize(R.styleable.QuickActions_quickaction_radiusAction, (int) (22 * scale));
		textPadding = a.getDimensionPixelSize(R.styleable.QuickActions_quickaction_textPadding, (int) (4 * scale));
		textMargin = a.getDimensionPixelSize(R.styleable.QuickActions_quickaction_marginPadding, (int) (6 * scale));
		scaleGrow = a.getFloat(R.styleable.QuickActions_quickaction_scaleGrow, 0.28f) + 1.0f;
		angleActions = (float) (a.getFloat(R.styleable.QuickActions_quickaction_angleActions, 55) * 2 * Math.PI / 360);
		backgroundColor = a.getColor(R.styleable.QuickActions_quickaction_backgroundColor, Color.WHITE);
		actionBackgroundColor = a.getColor(R.styleable.QuickActions_quickaction_actionBackgroundColor, Color.LTGRAY);
		actionBackgroundActiveColor = a.getColor(R.styleable.QuickActions_quickaction_actionBackgroundActiveColor, Color.WHITE);
		textColor = a.getColor(R.styleable.QuickActions_android_textColor, Color.WHITE);
		textSize = a.getDimensionPixelSize(R.styleable.QuickActions_android_textSize, (int) (13 * scale));

		a.recycle();

		if (isInEditMode()) {
			Resources resources = getResources();
			quickActions.add(new QuickAction("text 1", resources.getDrawable(android.R.drawable.ic_btn_speak_now)));
			quickActions.add(new QuickAction("text 2", resources.getDrawable(android.R.drawable.ic_delete)));
			quickActions.add(new QuickAction("text 3", resources.getDrawable(android.R.drawable.ic_input_add)));
//			quickActions.add(new QuickAction("text 4", resources.getDrawable(android.R.drawable.ic_dialog_alert)));
		}

		size = (radius + radiusAction * scaleGrow + radiusAction * (scaleGrow - 1)) * 2 + textSize * 2.5f + textPadding * 2 + textMargin;
		halfSize = size / 2;

		paintBackground = new Paint(Paint.ANTI_ALIAS_FLAG);
		paintBackground.setColor(backgroundColor);
		paintBackground.setStyle(Paint.Style.FILL_AND_STROKE);
		paintBackground.setShader(new RadialGradient(halfSize, halfSize, radius + radiusAction, backgroundColor, Color.TRANSPARENT, TileMode.MIRROR));

		paintActionsBackground = new Paint(Paint.ANTI_ALIAS_FLAG);
		paintActionsBackground.setColor(actionBackgroundColor);
		paintActionsBackground.setStyle(Paint.Style.FILL_AND_STROKE);

		paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
		paintText.setColor(textColor);
		paintText.setTextSize(textSize);

		bounds = new Rect();
		textBounds = new RectF();

		auxLeftTop = new int[2];
	}

	public void show(float x, float y, Object tag) {
		this.tag = tag;
		ViewGroup.LayoutParams params = getLayoutParams();
		if (params instanceof RelativeLayout.LayoutParams) {
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) params;
			layoutParams.setMargins((int) (x - halfSize), (int) (y - halfSize), 0, 0);
			setLayoutParams(layoutParams);
		} else {
			setTranslationX(x - halfSize);
			setTranslationY(y - halfSize);
		}
		if (getVisibility() != View.VISIBLE) {
			setVisibility(View.VISIBLE);
			startAnimation(viewAnimation);
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

	public void addAction(int textResId, int iconResId) {
		this.quickActions.add(new QuickAction(getContext(), textResId, iconResId));
	}

	public void addAction(CharSequence text, Drawable background) {
		this.quickActions.add(new QuickAction(text, background));
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension((int) size, (int) size);
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
			float vectorY = (positionY + halfSize) - Math.min(parentHeight / 2, size);
			float vectorX = (positionX + halfSize) - parentWidth / 2;
			double angle = Math.atan2(vectorY, vectorX);
			double initialAngle = angle - Math.PI - ((quickActions.size() - 1) * angleActions) / 2;
			canvas.drawCircle(size / 2, size / 2, radius, paintBackground);
			int actionNumber = 0;
			for (QuickAction action : quickActions) {
				double calculatedAngle = initialAngle + actionNumber * angleActions;
				float x = (float) Math.cos(calculatedAngle) * radius + halfSize;
				float y = (float) Math.sin(calculatedAngle) * radius + halfSize;
				if (isInEditMode() && actionNumber == 0) {
					touchX = x + radiusAction / 2;
					touchY = y + radiusAction / 2;
				}
				float dx = x - touchX;
				float dy = y - touchY;
				float distance = (float) Math.sqrt(dx * dx + dy * dy);
				boolean activeAction = distance < radiusAction;
				Drawable icon = action.getImage();
				if (activeAction) {
					actionActive = actionNumber;
					if (action.getImageActive() != null) {
						icon = action.getImageActive();
					}
					if (action.getColorBackgroundActive() != 0) {
						paintActionsBackground.setColor(action.getColorBackgroundActive());
					} else {
						paintActionsBackground.setColor(actionBackgroundActiveColor);
					}
				}
				float scaleRadius = 1f;
				distance -= radiusAction;
				float minDistance = radius * 2 / 3;
				if (distance < minDistance) {
					if (distance < 0) {
						distance = 0;
					}
					scaleRadius = scaleGrow - (scaleGrow - 1) * distance / minDistance;
					float calculatedRadius = radius + radiusAction * (scaleRadius - 1);
					x = (float) Math.cos(calculatedAngle) * calculatedRadius + halfSize;
					y = (float) Math.sin(calculatedAngle) * calculatedRadius + halfSize;
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
					canvas.drawRoundRect(textBounds, textPadding * 2, textPadding * 2, paintActionsBackground);
					canvas.drawText(text, textX, textY, paintText);
				}
				actionNumber++;
			}

			paintText.setColor(Color.RED);
			canvas.drawLine(0, touchY, size, touchY, paintText);
			canvas.drawLine(touchX, 0, touchX, size, paintText);
			paintText.setColor(textColor);

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

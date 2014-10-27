package com.jorgemf.android.quickactions;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

public class QuickAction {

	protected float x;

	protected float y;

	protected boolean active;

	private CharSequence text;

	private Drawable image;

	private Drawable imageActive;

	private int colorBackgroundActive;

	public QuickAction(Context context, int textRestId, int drawableResId) {
		Resources resources = context.getResources();
		text = resources.getString(textRestId);
		image = resources.getDrawable(drawableResId);
		this.colorBackgroundActive = 0;
		this.imageActive = null;
	}

	public QuickAction(Context context, int textRestId, int drawableResId, int colorBackgroundActiveResId, int imageActiveResId) {
		Resources resources = context.getResources();
		text = resources.getString(textRestId);
		image = resources.getDrawable(drawableResId);
		colorBackgroundActive = resources.getColor(colorBackgroundActiveResId);
		imageActive = resources.getDrawable(imageActiveResId);
	}

	public QuickAction(CharSequence text, Drawable image) {
		this.text = text;
		this.image = image;
		this.colorBackgroundActive = 0;
		this.imageActive = null;
	}

	public QuickAction(CharSequence text, Drawable image, int colorBackgroundActive, Drawable imageActive) {
		this.text = text;
		this.image = image;
		this.colorBackgroundActive = colorBackgroundActive;
		this.imageActive = imageActive;
	}

	public Drawable getImage() {
		return image;
	}

	public void setImage(Drawable image) {
		this.image = image;
	}

	public CharSequence getText() {
		return text;
	}

	public void setText(CharSequence text) {
		this.text = text;
	}

	public Drawable getImageActive() {
		return imageActive;
	}

	public void setImageActive(Drawable imageActive) {
		this.imageActive = imageActive;
	}

	public int getColorBackgroundActive() {
		return colorBackgroundActive;
	}

	public void setColorBackgroundActive(int colorBackgroundActive) {
		this.colorBackgroundActive = colorBackgroundActive;
	}
}

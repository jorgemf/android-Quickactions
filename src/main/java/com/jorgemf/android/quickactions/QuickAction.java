package com.jorgemf.android.quickactions;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

public class QuickAction {

	private CharSequence text;

	private Drawable image;

	protected float x;

	protected float y;

	protected boolean active;

	public QuickAction(Context context, int textRestId, int drawableResId) {
		Resources resources = context.getResources();
		text = resources.getString(textRestId);
		image = resources.getDrawable(drawableResId);
	}

	public QuickAction(CharSequence text, Drawable image) {
		this.text = text;
		this.image = image;
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
}

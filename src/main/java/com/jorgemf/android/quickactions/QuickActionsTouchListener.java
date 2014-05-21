package com.jorgemf.android.quickactions;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class QuickActionsTouchListener implements OnTouchListener {

	private GestureDetector gesturesDetector;

	private QuickActionsMenu quickActionsMenu;

	private Object tag;

	public QuickActionsTouchListener(Context context, QuickActionsMenu quickActionsMenu) {
		this.quickActionsMenu = quickActionsMenu;
		gesturesDetector = new GestureDetector(context, new GestureDetector.OnGestureListener() {

			@Override
			public boolean onDown(MotionEvent e) {
				return true;
			}

			@Override
			public void onShowPress(MotionEvent e) {

			}

			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				return true;
			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
				return false;
			}

			@Override
			public void onLongPress(MotionEvent e) {
				QuickActionsMenu quickActionsMenu = QuickActionsTouchListener.this.quickActionsMenu;
				quickActionsMenu.show(e.getRawX(), e.getRawY(), tag);
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				return false;
			}
		});
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (quickActionsMenu.getVisibility() != View.VISIBLE) {
			tag = v.getTag();
			return gesturesDetector.onTouchEvent(event);
		}
		return false;
	}

}

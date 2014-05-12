package com.jorgemf.android.quickactions;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends Activity {

	private QuickActionsMenu quickActionsMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		quickActionsMenu = (QuickActionsMenu) findViewById(R.id.quick_actions_menu);
		QuickActionsTouchListener touchListener = new QuickActionsTouchListener(this, quickActionsMenu);
		findViewById(R.id.text_view).setOnTouchListener(touchListener);
		quickActionsMenu.setListener(new QuickActionsListener() {
			@Override
			public void onActionSelected(QuickActionsMenu quickActionsMenu, Object tag, int action) {
//				Toast.makeText(MainActivity.this, "Action selected: " + action, Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onMenuDismiss(QuickActionsMenu quickActionsMenu, Object tag) {
//				Toast.makeText(MainActivity.this, "Dismiss without action", Toast.LENGTH_SHORT).show();
			}
		});
		Resources resources = getResources();
		quickActionsMenu.addAction("text 1", resources.getDrawable(android.R.drawable.ic_btn_speak_now));
		quickActionsMenu.addAction("text 2", resources.getDrawable(android.R.drawable.ic_delete));
		quickActionsMenu.addAction("text 3", resources.getDrawable(android.R.drawable.ic_input_add));
//		quickActionsMenu.addAction("text 4", resources.getDrawable(android.R.drawable.ic_dialog_alert));

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (quickActionsMenu.getVisibility() == View.VISIBLE) {
			quickActionsMenu.onTouchEvent(event);
		}
		return super.onTouchEvent(event);
	}
}

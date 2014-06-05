package com.jorgemf.android.quickactions;

public interface QuickActionsListener {

	public void onShow(QuickActionsMenu quickActionsMenu, Object tag, float x, float y);

	public void onActionSelected(QuickActionsMenu quickActionsMenu, Object tag, int action);

	public void onMenuDismiss(QuickActionsMenu quickActionsMenu, Object tag);

}

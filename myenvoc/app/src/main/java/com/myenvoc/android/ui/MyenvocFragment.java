package com.myenvoc.android.ui;

import android.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.myenvoc.commons.UIUtils;

public class MyenvocFragment extends Fragment {
	/**
	 * save activity at creation time, as when activity is disposed (due to
	 * going Back for example), getActivity() will return null (evil ANR);
	 */
	private FragmentActivity activity;

	protected void setupActivity() {
		this.activity = getActivitySafe();

	}

	public FragmentActivity getActivitySafe() {
		return activity;
	}

	public boolean isActivityDead() {
		if (getActivity() == null) {
			return true;
		}
		return UIUtils.isDestroyed(activity);
	}
}

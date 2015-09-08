package com.myenvoc.android.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;

import com.myenvoc.R;

public class CustomAlertDialog {

	public static boolean showDontShowAgainDialog(final String key, final FragmentActivity activity, final int title, final int message,
			final int positiveButtonName, final int negativeButtonName, final DialogInterface.OnClickListener positiveListener,
			final DialogInterface.OnClickListener negativeListener, final DialogInterface.OnClickListener dontShowSelectedListener) {

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext());
		boolean dontShow = preferences.getBoolean(key, false);
		if (dontShow) {
			if (dontShowSelectedListener != null) {
				dontShowSelectedListener.onClick(null, -1);
			}
			return false;
		}

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

		alertDialogBuilder.setTitle(title);

		LayoutInflater inflater = activity.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.dont_show_again_alert_dialog, null);
		TextView messageTextView = (TextView) dialogView.findViewById(R.id.messageTextView);
		messageTextView.setText(message);

		final CheckBox dontShowCheck = (CheckBox) dialogView.findViewById(R.id.dontShow);
		dontShowCheck.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View arg0) {
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext());
				Editor edit = preferences.edit();
				edit.putBoolean(key, dontShowCheck.isChecked());
				edit.commit();
			}
		});

		alertDialogBuilder.setView(dialogView).setPositiveButton(positiveButtonName, positiveListener);

		alertDialogBuilder.setNegativeButton(negativeButtonName, negativeListener);

		alertDialogBuilder.create().show();
		return true;
	}
}

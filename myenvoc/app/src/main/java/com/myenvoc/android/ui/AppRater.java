package com.myenvoc.android.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.myenvoc.R;

public class AppRater {
	private static final String DONT_SHOW_AGAIN = "dontshowagain_rate";
	private final static String APP_PNAME = "com.myenvoc";

	private final static int DAYS_UNTIL_PROMPT = 3;
	private final static int LAUNCHES_UNTIL_PROMPT = 7;

	public static void app_launched(final Context mContext) {
		SharedPreferences prefs = mContext.getSharedPreferences("apprater", 0);
		if (prefs.getBoolean(DONT_SHOW_AGAIN, false)) {
			return;
		}

		SharedPreferences.Editor editor = prefs.edit();

		// Increment launch counter
		long launch_count = prefs.getLong("launch_count", 0) + 1;
		editor.putLong("launch_count", launch_count);

		// Get date of first launch
		Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
		if (date_firstLaunch == 0) {
			date_firstLaunch = System.currentTimeMillis();
			editor.putLong("date_firstlaunch", date_firstLaunch);
		}

		editor.commit();

		// Wait at least n days before opening
		if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
			if (System.currentTimeMillis() >= date_firstLaunch + (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
				showRateDialog(mContext, prefs.edit());
			}
		}

	}

	public static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor) {
		final Dialog dialog = new Dialog(mContext);
		dialog.setTitle(mContext.getString(R.string.rateTitle));

		LinearLayout ll = new LinearLayout(mContext);
		ll.setOrientation(LinearLayout.VERTICAL);

		TextView tv = new TextView(mContext);
		tv.setText(mContext.getString(R.string.rate));

		tv.setTextSize(16);
		tv.setPadding(20, 20, 20, 40);
		tv.setLineSpacing(0, 1.4f);
		tv.setTextColor(mContext.getResources().getColor(R.color.dontShowAgainDialogColor));
		ll.addView(tv);

		Button b1 = new Button(mContext);
		b1.setText(mContext.getString(R.string.rateOkButton));
		b1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				if (editor != null) {
					editor.putBoolean(DONT_SHOW_AGAIN, true);
					editor.commit();
				}
				mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
				dialog.dismiss();
			}
		});
		ll.addView(b1);

		Button b2 = new Button(mContext);
		b2.setText(mContext.getString(R.string.rateLaterButton));
		b2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				dialog.dismiss();
			}
		});
		ll.addView(b2);

		Button b3 = new Button(mContext);
		b3.setText(mContext.getString(R.string.rateNeverButton));
		b3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				if (editor != null) {
					editor.putBoolean(DONT_SHOW_AGAIN, true);
					editor.commit();
				}
				dialog.dismiss();
			}
		});
		ll.addView(b3);

		dialog.setContentView(ll);
		dialog.show();
	}
}
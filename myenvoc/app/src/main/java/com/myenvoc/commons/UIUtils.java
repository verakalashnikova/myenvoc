package com.myenvoc.commons;

import android.content.Context;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.widget.TextView;

public class UIUtils {

	public static TextView createTextView(final Context context) {
		TextView textView = new TextView(context);
		textView.setLineSpacing(0, 1.3f);
		textView.setTextSize(15);
		return textView;
	}

	public static int convertToPixels(final Context context, final int value) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.getResources().getDisplayMetrics());
	}

	/**
	 * NOTE!!! calling getActivity() from background thread is not appropriate
	 * (even with proper HANDLER), as getActivity() may return null if activity
	 * was dismissed, e.g. user didn't wait load completion and tapped back
	 * button. Thus we should declare final Activity a = getActivity() and pass
	 * it to background thread. In the bg thread it's somewhat more performant
	 * to check if activity is already dead and don't render. Even if it's dead
	 * (but not null of course), it's okay to use it for rendering, rendering
	 * results will be garbage collected.
	 * 
	 * @param activity
	 * @return
	 */
	public static boolean isDestroyed(final FragmentActivity activity) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			return activity.isDestroyed();
		}
		return false;
	}
}

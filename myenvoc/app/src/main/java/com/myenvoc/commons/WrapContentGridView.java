package com.myenvoc.commons;

import android.content.Context;
import android.widget.GridView;

public class WrapContentGridView extends GridView {

	public WrapContentGridView(Context context) {
		super(context);		
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int heightSpec;

		// The great Android "hackatlon" (in order to enable
		// "wrap_content" on grid views).
		if (getLayoutParams().height == LayoutParams.WRAP_CONTENT) {
			heightSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		} else {
			heightSpec = heightMeasureSpec;
		}

		super.onMeasure(widthMeasureSpec, heightSpec);
	}
}

package com.myenvoc.android.ui.vocabulary;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.myenvoc.commons.UIUtils;

public class TranslationList extends LinearDataList<String> {
	public TranslationList(final Context context) {
		super(context);
	}

	public TranslationList(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public View buildDataView(final String data, final int number) {
		TextView textView = UIUtils.createTextView(getContext());
		textView.setText(String.valueOf(number) + ". " + data);
		return textView;
	}
}
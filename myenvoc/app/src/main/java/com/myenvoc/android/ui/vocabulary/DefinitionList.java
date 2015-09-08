package com.myenvoc.android.ui.vocabulary;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.myenvoc.android.domain.MyWordContext;

public class DefinitionList extends LinearDataList<MyWordContext> {
	public DefinitionList(final Context context) {
		super(context);
	}

	public DefinitionList(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected View buildDataView(final MyWordContext data, final int number) {
		return ViewMyWordWidget.renderMyWordContext(getContext(), data, number, false, null, null);
	}

}

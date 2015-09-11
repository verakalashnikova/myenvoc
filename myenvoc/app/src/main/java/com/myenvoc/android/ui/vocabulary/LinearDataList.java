package com.myenvoc.android.ui.vocabulary;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.common.collect.Lists;
import com.myenvoc.R;
import com.myenvoc.commons.CommonUtils;

public abstract class LinearDataList<D> extends LinearLayout {

	public interface SelectionListener<D> {

		void onItemSelected(D data, int index);

		void onItemRemove(int index);

	}

	private SelectionListener<D> selectionListener;
	private final List<D> dataList = Lists.newArrayList();

	public LinearDataList(final Context context) {
		super(context);
	}

	public LinearDataList(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	public void setSelectionListener(final SelectionListener<D> selectionListener) {
		this.selectionListener = selectionListener;
	}

	public void removeEntry(final int index) {
		dataList.remove(index);
		int childCount = getChildCount();
		for (int i = index; i < childCount; i++) {
			removeViewAt(index);
		}
		for (int i = index; i < dataList.size(); i++) {
			addEntryToBottomInternal(dataList.get(i));
		}
	}

	public void updateEntry(final D data, final int index) {
		View lineEntry = createLineEntry(data, index);
		removeViewAt(index);
		addView(lineEntry, index);
		dataList.set(index, data);
	}

	public void addEntryToBottom(final D data) {
		addEntryToBottomInternal(data);
		dataList.add(data);
	}

	private void addEntryToBottomInternal(final D data) {
		RelativeLayout layout = createLineEntry(data, getChildCount());
		addView(layout);
	}

	private RelativeLayout createLineEntry(final D data, final int index) {
		RelativeLayout layout = new RelativeLayout(getContext());

		final ImageView removeImageView = new ImageView(getContext());
		int id = CommonUtils.IDS_GENERATOR.getAndIncrement();
		removeImageView.setId(id);
		removeImageView.setImageResource(R.drawable.ic_remove);

		final View dataView = buildDataView(data, index + 1);

		RelativeLayout.LayoutParams removeViewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		removeViewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		removeViewLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		removeViewLayoutParams.setMargins(0, 0, 20, 0);
		layout.addView(removeImageView, removeViewLayoutParams);

		RelativeLayout.LayoutParams dataViewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		dataViewLayoutParams.addRule(RelativeLayout.LEFT_OF, id);
		dataView.setPadding(5, 15, 15, 5);
		layout.addView(dataView, dataViewLayoutParams);
		layout.setBackgroundResource(R.drawable.border_bottom_grey);

		dataView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View view) {
				selectionListener.onItemSelected(data, index);
			}
		});
		removeImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View view) {
				selectionListener.onItemRemove(index);
			}
		});
		return layout;
	}

	protected abstract View buildDataView(final D data, final int number);

}
